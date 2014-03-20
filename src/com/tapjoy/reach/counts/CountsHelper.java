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

			ErrorModel errorModel = checkDataQuality(p);
			if (errorModel != null) {
				String error = gson.toJson(errorModel);
				ResponseModel model = new ResponseModel(error,
						HttpResponseStatus.BAD_REQUEST, "application/json");
				return model;
			}

			List<String> personas = params.get(KeyEnum
					.getValue(KeyEnum.persona_name));
			params.remove(KeyEnum.getValue(KeyEnum.persona_name));

			List<String> sources = params.get(KeyEnum.getValue(KeyEnum.source));
		/*	if (sources == null || sources.size() == 0) {
				logger.error("No source in request");
				errorModel = new ErrorModel(HttpResponseStatus.BAD_REQUEST.getCode(), "Missing parameters: sources missing");
				String error = gson.toJson(errorModel);
				ResponseModel model = new ResponseModel(error,
						HttpResponseStatus.BAD_REQUEST,
						"application/json");
				return model;
			}
			//params.remove(KeyEnum.getValue(KeyEnum.source));*/

			Set<Entry<String, List<String>>> entries = params.entrySet();
			List<Entry<String, List<String>>> entriesList = new ArrayList<Map.Entry<String, List<String>>>(
					entries);

			List<String> keyList;
			if (entriesList.size() == 0) {
				String key = "";
				for (int i = 1; i <= KeyEnum.values().length - 1; i++) {
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
			ErrorModel errorModel = new ErrorModel(HttpResponseStatus.INTERNAL_SERVER_ERROR.getCode(), "Unknown error");
			String error = gson.toJson(errorModel);
			ResponseModel model = new ResponseModel(error,
					HttpResponseStatus.INTERNAL_SERVER_ERROR,
					"application/json");
			return model;
		}
	}

	private ErrorModel checkDataQuality(Map<String, List<String>> p) {
		ErrorModel errorModel = null;
		if (p.get(KeyEnum.getValue(KeyEnum.device_os_version)) != null
				&& p.get(KeyEnum.getValue(KeyEnum.platform)) == null) {
			errorModel = new ErrorModel(
					HttpResponseStatus.BAD_REQUEST.getCode(),
					"Missing parameters: platform missing");
		} else if (p.get(KeyEnum.getValue(KeyEnum.geoip_country)) != null
				&& p.get(KeyEnum.getValue(KeyEnum.geoip_continent)) == null) {
			errorModel = new ErrorModel(
					HttpResponseStatus.BAD_REQUEST.getCode(),
					"Missing parameters: geoip_continent missing");
		} else if (p.get(KeyEnum.getValue(KeyEnum.geoip_region)) != null
				&& p.get(KeyEnum.getValue(KeyEnum.geoip_country)) == null) {
			errorModel = new ErrorModel(
					HttpResponseStatus.BAD_REQUEST.getCode(),
					"Missing parameters: geoip_country missing");
		}

		return errorModel;
	}

	private ResponseModel getHBaseResults(List<String> keyList,
			List<String> personas, List<String> sources) {
		int udidsCount = 0;
		int impCount = 0;
		ResponseModel model = null;
		for (String key : keyList) {
			key = key.toUpperCase();
			String src = key.split("-")[0];
			src = src.toLowerCase();
			key = src+key.substring(key.indexOf('-'));
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
			
			if(key.charAt(0) == '$' &&(personas == null || personas.size() == 0) ){
				String udidString = HBaseWrapper.getHBaseResultToString(res,
						CountsHbaseConstants.COLUMN_FAMILY,
						CountsHbaseConstants.DISTINCT_UDID_COL_QUALIFIER);
				if (StringUtils.isNotBlank(udidString)) {
					udidsCount += Integer.parseInt(udidString);
				}
				
				String impString = HBaseWrapper.getHBaseResultToString(res,
						CountsHbaseConstants.COLUMN_FAMILY,
						CountsHbaseConstants.SOURCE_COL_QUALIFIER);
				if (StringUtils.isNotBlank(impString)) {
					impCount += Integer.parseInt(impString);
				}
			}
			
			else if(key.charAt(0) != '$' &&(personas == null || personas.size() == 0)){
				String udidString = HBaseWrapper.getHBaseResultToString(res,
						CountsHbaseConstants.COLUMN_FAMILY,
						CountsHbaseConstants.UDID_COL_QUALIFIER);
				if (StringUtils.isNotBlank(udidString)) {
					udidsCount += Integer.parseInt(udidString);
				}
				
				String impString = HBaseWrapper.getHBaseResultToString(res,
						CountsHbaseConstants.COLUMN_FAMILY,
						CountsHbaseConstants.SOURCE_COL_QUALIFIER);
				if (StringUtils.isNotBlank(impString)) {
					impCount += Integer.parseInt(impString);
				}
			}
			
			else if(key.charAt(0) == '$' && personas.size() > 0){
				CountsModel countsModel = getCount(personas, false, res);
				if(countsModel == null){
					ErrorModel errorModel = new ErrorModel(HttpResponseStatus.BAD_REQUEST.getCode(), "Invalid persona value");
					String error = gson.toJson(errorModel);
					model = new ResponseModel(error,
							HttpResponseStatus.BAD_REQUEST,
							"application/json");
					return model;
				}
				udidsCount += countsModel.getUdids_count();
				impCount += countsModel.getImpressions_count();
				
			}
			
			else if(key.charAt(0) != '$' && personas.size() > 0){
				CountsModel countsModel = getCount(personas, true, res);
				if(countsModel == null){
					ErrorModel errorModel = new ErrorModel(HttpResponseStatus.BAD_REQUEST.getCode(), "Invalid persona value");
					String error = gson.toJson(errorModel);
					model = new ResponseModel(error,
							HttpResponseStatus.BAD_REQUEST,
							"application/json");
					return model;
				}
				udidsCount += countsModel.getUdids_count();
				impCount += countsModel.getImpressions_count();
			}

			/*if (personas == null || personas.size() == 0) {
				String udidString = HBaseWrapper.getHBaseResultToString(res,
						CountsHbaseConstants.COLUMN_FAMILY,
						CountsHbaseConstants.UDID_COL_QUALIFIER);
				if (StringUtils.isNotBlank(udidString)) {
					udidsCount += Integer.parseInt(udidString);
				}

			}

			else {
				for (String persona : personas) {
					int personaId = getPersonaId(persona);
					if (personaId == 0) {
						ErrorModel errorModel = new ErrorModel(HttpResponseStatus.BAD_REQUEST.getCode(), "Invalid persona value");
						String error = gson.toJson(errorModel);
						model = new ResponseModel(error,
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
					int personaUdids = calculateCounts((personaId % 10) - 1,
							personaValue);
					udidsCount += personaUdids;

				}
			}

			String sourceValue = HBaseWrapper.getHBaseResultToString(res,
					CountsHbaseConstants.COLUMN_FAMILY,
					CountsHbaseConstants.SOURCE_COL_QUALIFIER);
			for (String source : sources) {
				Source s = Source.fromString(source);
				int imps = calculateCounts(s.ordinal(), sourceValue);
				impCount += imps;
			}*/

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

	private String calculateCounts(int key, String value) {
		String[] parts = value.split(":");
		if (parts == null) {
			return "";
		}
		return parts[key];
	}
	
	private CountsModel getCount(List<String> personas, boolean isSource, Result res){
		int udidsCount = 0, impCount = 0;
		for(String p:personas){
			int personaId = getPersonaId(p);
			if (personaId == 0) {
				return null;
			}
			int segmentId = (int) Math.ceil(personaId / 10d);
			String personalColQualifier = CountsHbaseConstants.PERSONA_COL_QUALIFIER_PREFIX
					+ segmentId;
			String personaValue = HBaseWrapper.getHBaseResultToString(
					res, CountsHbaseConstants.COLUMN_FAMILY,
					personalColQualifier);
			String val = calculateCounts((personaId % 10) - 1,
					personaValue);
			String[] splits = val.split(",");
			impCount += Integer.parseInt(splits[1]);
			if(isSource){
				udidsCount += Integer.parseInt(splits[0]);
			}
			else{
				String sourceColQualifier = CountsHbaseConstants.DISTINCT_SOURCE_QUALIFIER_PREFIX
						+ segmentId;
				personaValue = HBaseWrapper.getHBaseResultToString(
						res, CountsHbaseConstants.COLUMN_FAMILY,
						sourceColQualifier);
				 val = calculateCounts((personaId % 10) - 1,
						personaValue);
				 udidsCount += Integer.parseInt(val);
			}
			
		}
		return new CountsModel(udidsCount, impCount);
	}

}
