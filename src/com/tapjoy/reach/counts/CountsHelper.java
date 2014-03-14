package com.tapjoy.reach.counts;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tapjoy.reach.config.OverallConfig;
import com.tapjoy.reach.hbase.HBaseWrapper;
import com.tapjoy.reach.helper.Helper;
import com.tapjoy.reach.params.KeyEnum;
import com.tapjoy.reach.params.Personas;
import com.tapjoy.reach.params.Source;
import com.tapjoy.reach.service.ResponseModel;

public class CountsHelper implements Helper {

	private static Gson gson = new GsonBuilder().create();
	private static Logger logger = Logger.getLogger(CountsHelper.class);

	@Override
	public ResponseModel getResult(Map<String, List<String>> p) {
		try {
			Map<String, List<String>> params = new HashMap<String, List<String>>(
					p);

			boolean dataQuality = checkDataQuality(p);
			if (!dataQuality) {
				ResponseModel model = new ResponseModel("Invalid parameters",
						HttpResponseStatus.BAD_REQUEST, "application/json");
				return model;
			}

			List<String> personas = params.get(KeyEnum
					.getValue(KeyEnum.persona_name));
			params.remove(KeyEnum.getValue(KeyEnum.persona_name));

			List<String> sources = params.get(KeyEnum.getValue(KeyEnum.source));
			if (sources == null || sources.size() == 0) {
				logger.error("No source in request");
				ResponseModel model = new ResponseModel("HBase error",
						HttpResponseStatus.INTERNAL_SERVER_ERROR,
						"application/json");
				return model;
			}
			params.remove(KeyEnum.getValue(KeyEnum.source));

			Set<Entry<String, List<String>>> entries = params.entrySet();
			List<Entry<String, List<String>>> entriesList = new ArrayList<Map.Entry<String, List<String>>>(
					entries);

			List<String> keyList;
			if (entriesList.size() == 0) {
				String key = "";
				for (int i = 1; i <= KeyEnum.values().length - 2; i++) {
					key = (key.length() > 0 ? key + "-" : key) + "$";
				}
				keyList = new ArrayList<String>();
				keyList.add(key);
			} else {
				Collections.sort(entriesList, new KeyComparator());
				keyList = new KeyParser().collectKeys("", entriesList);
			}

			ResponseModel model = getHBaseResults(keyList, personas, sources);
			return model;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(ex);
			ResponseModel model = new ResponseModel("Unknown error",
					HttpResponseStatus.INTERNAL_SERVER_ERROR,
					"application/json");
			return model;
		}
	}

	private boolean checkDataQuality(Map<String, List<String>> p) {
		if (p.get(KeyEnum.getValue(KeyEnum.device_os_version)) != null
				&& p.get(KeyEnum.getValue(KeyEnum.platform)) == null) {
			return false;
		} else if (p.get(KeyEnum.getValue(KeyEnum.geoip_country)) != null
				&& p.get(KeyEnum.getValue(KeyEnum.geoip_continent)) == null) {
			return false;
		} else if (p.get(KeyEnum.getValue(KeyEnum.geoip_region)) != null
				&& p.get(KeyEnum.getValue(KeyEnum.geoip_country)) == null) {
			return false;
		}

		return true;
	}

	private ResponseModel getHBaseResults(List<String> keyList,
			List<String> personas, List<String> sources) {
		int udidsCount = 0;
		int impCount = 0;
		ResponseModel model = null;
		for (String key : keyList) {
			key = key.toUpperCase();
			Result res = null;
			try {
				res = HBaseWrapper.getOneRecordInTable(key,
						CountsHbaseConstants.COUNTS_TABLE,
						CountsHbaseConstants.TOKEN);
			} catch (ClassNotFoundException | SQLException
					| InterruptedException e1) {
				logger.error(e1);
				model = new ResponseModel("HBase error",
						HttpResponseStatus.INTERNAL_SERVER_ERROR,
						"application/json");
				return model;
			}

			if (res == null) {
				continue;
			}
			
			if(res.list() == null){
				logger.error("No targeting found for key:"+key);
				continue;
			}

			if (personas == null || personas.size() == 0) {
				String udidString = HBaseWrapper.getHBaseResultToString(res,
						CountsHbaseConstants.COLUMN_FAMILY,
						CountsHbaseConstants.UDID_COL_QUALIFIER);
				System.out.println(udidString);
				if(StringUtils.isNotBlank(udidString)){
					udidsCount += Integer.parseInt(udidString);
				}
				
			}

			else {
				for (String persona : personas) {
					int personaId = getPersonaId(persona);
					if (personaId == 0) {
						model = new ResponseModel("Invalid persona",
								HttpResponseStatus.BAD_REQUEST,
								"application/json");
						return model;
					}
					int segmentId = (int) Math.ceil(personaId / 10d);
					String personalColQualifier = CountsHbaseConstants.PERSONA_COL_QUALIFIER_PREFIX
							+ segmentId;
					String personaValue = HBaseWrapper.getHBaseResultToString(
							res, CountsHbaseConstants.COLUMN_FAMILY,
							personalColQualifier);
					int personaUdids = calculateCounts(personaId + 1,
							personaValue);
					System.out.println("persona:" + persona + " #udids:"
							+ personaUdids);
					udidsCount += personaUdids;

				}
			}

			String sourceValue = HBaseWrapper.getHBaseResultToString(res,
					CountsHbaseConstants.COLUMN_FAMILY,
					CountsHbaseConstants.SOURCE_COL_QUALIFIER);
			for (String source : sources) {
				Source s = null;
				try {
					s = Source.valueOf(source);
				} catch (IllegalArgumentException ex) {
					model = new ResponseModel("Invalid source",
							HttpResponseStatus.BAD_REQUEST, "application/json");
					return model;
				}

				int imps = calculateCounts(s.ordinal(), sourceValue);
				System.out.println("source:" + source + " #imps:" + imps);
				impCount += imps;
			}

			/*
			 * for (KeyValue rawVal : res.raw()) { byte[] bytes =
			 * rawVal.getValue(); String val = Bytes.toString(bytes); if (val ==
			 * null || val.equals("null")) { continue; }
			 * 
			 * System.out.println(val); String[] counts = val.split(":");
			 * udidsCount += Integer.parseInt(counts[0]); impCount +=
			 * Integer.parseInt(counts[1]); }
			 */
		}
		CountsModel countsModel = new CountsModel(udidsCount, impCount);
		String json = gson.toJson(countsModel);
		model = new ResponseModel(json, HttpResponseStatus.OK,
				"application/json");
		return model;
	}

	private int getPersonaId(String persona) {
		int id = Personas.getInstance().getPersonaId(persona);
		return id;
	}

	private int calculateCounts(int key, String value) {
		String[] parts = value.split(":");
		if (parts == null) {
			return 0;
		}
		return Integer.parseInt(parts[key]);
	}

}
