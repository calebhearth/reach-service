package com.tapjoy.reach.counts;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.HashSet;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.client.Result;
import org.apache.log4j.Logger;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tapjoy.reach.hbase.HBaseWrapper;
import com.tapjoy.reach.helper.Helper;
import com.tapjoy.reach.params.KeyEnum;
import com.tapjoy.reach.params.Personas;
import com.tapjoy.reach.service.ErrorModel;
import com.tapjoy.reach.service.ResponseModel;

public class CountsHelper implements Helper {

	private static Gson gson = new GsonBuilder().create();
	private static Logger logger = Logger.getLogger(CountsHelper.class);

	@Override
	public ResponseModel getResult(Map<String, List<String>> p) {
		try {
			Map<String, List<String>> params = new HashMap<String, List<String>>(
					p);

			List<String> personas = params.get(KeyEnum
					.getValue(KeyEnum.persona_name));
			params.remove(KeyEnum.getValue(KeyEnum.persona_name));

			Set<Entry<String, List<String>>> entries = params.entrySet();
			List<Entry<String, List<String>>> entriesList = new ArrayList<Map.Entry<String, List<String>>>(
					entries);

			Set<String> keyList;
			if (entriesList.size() == 0) {
				String key = "";
				for (int i = 1; i <= KeyEnum.values().length - 1; i++) {
					key = (key.length() > 0 ? key + "-" : key) + "$";
				}
				keyList = new HashSet<String>();
				keyList.add(key);
			} else {
				Collections.sort(entriesList, new KeyComparator());
				keyList = new KeyParser().createKeys(params, "", 0);
			}

 			ResponseModel model = getHBaseResults(keyList, personas);
			return model;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(ex);
			ErrorModel errorModel = new ErrorModel(HttpResponseStatus.INTERNAL_SERVER_ERROR.getCode(), "Unknown error");
			String error = gson.toJson(errorModel);
			ResponseModel model = new ResponseModel(error,
					HttpResponseStatus.INTERNAL_SERVER_ERROR,
					"application/json");
			return model;
		}
	}

	private ResponseModel getHBaseResults(Set<String> keyList,
			List<String> personas) {
		int reachAudienceCount = 0;
		int impCount = 0;
		int uniqueViewersCount = 0;
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
				ErrorModel errorModel = new ErrorModel(HttpResponseStatus.INTERNAL_SERVER_ERROR.getCode(), "HBase error");
				String error = gson.toJson(errorModel);
				model = new ResponseModel(error,
						HttpResponseStatus.INTERNAL_SERVER_ERROR,
						"application/json");
				return model;
			}

			if (res == null || res.list() == null) {
				logger.error("No targeting found for key:" + key);
				continue;
			}
			
			if(personas != null && personas.size() > 0){
				for(String persona:personas){
					int personaId = getPersonaId(persona);
					if (personaId == 0) {
						ErrorModel errorModel = new ErrorModel(HttpResponseStatus.BAD_REQUEST.getCode(), "Invalid Persona");
						String error = gson.toJson(errorModel);
						model = new ResponseModel(error,
								HttpResponseStatus.INTERNAL_SERVER_ERROR,
								"application/json");
						return model;
					}
					int segmentId = (int) Math.ceil(personaId / 10d);
					
					String colQualifier = "ps"
							+ segmentId;
					String value = HBaseWrapper.getHBaseResultToString(
							res, CountsHbaseConstants.COLUMN_FAMILY,
							colQualifier);
					String val = calculateCounts((personaId % 10),
							value);
					String[] splits = val.split(",");
					reachAudienceCount += Integer.parseInt(splits[0]);
					uniqueViewersCount += Integer.parseInt(splits[1]);
					impCount += Integer.parseInt(splits[2]);					
				}
				
			}
			
			else{
				String reachAudienceColQualifier = "ra";
				String reachAudienceValue = HBaseWrapper.getHBaseResultToString(
						res, CountsHbaseConstants.COLUMN_FAMILY,
						reachAudienceColQualifier);
				if (StringUtils.isNotBlank(reachAudienceValue)) {
					reachAudienceCount += Integer.parseInt(reachAudienceValue);
				}
				
				String impColQualifier = "im";
				String impValue = HBaseWrapper.getHBaseResultToString(
						res, CountsHbaseConstants.COLUMN_FAMILY,
						impColQualifier);
				if (StringUtils.isNotBlank(impValue)) {
					impCount += Integer.parseInt(impValue);
				}
				
				String uniqueViewersColQualifier = "uv";
				String uniqueViewersValue = HBaseWrapper.getHBaseResultToString(
						res, CountsHbaseConstants.COLUMN_FAMILY,
						uniqueViewersColQualifier);
				if (StringUtils.isNotBlank(uniqueViewersValue)) {
					uniqueViewersCount += Integer.parseInt(uniqueViewersValue);
				}
				
			}
			
		}
		CountsModel countsModel = new CountsModel(reachAudienceCount, impCount, uniqueViewersCount);
		String json = gson.toJson(countsModel);
		model = new ResponseModel(json, HttpResponseStatus.OK,
				"application/json");
		return model;
	}

	private int getPersonaId(String persona) {
		int id = Personas.getInstance().getPersonaId(persona);
		return id;
	}

	private String calculateCounts(int key, String value) {
		String[] parts = value.split(":");
		if (parts == null) {
			return "";
		}
		key = key==0 ? 9: key - 1;
		return parts[key];
	}

}
