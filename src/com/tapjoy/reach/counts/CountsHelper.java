package com.tapjoy.reach.counts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.client.Result;
import org.apache.log4j.Logger;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tapjoy.reach.hbase.HBaseTask;
import com.tapjoy.reach.hbase.HBaseThreadPool;
import com.tapjoy.reach.hbase.HBaseWrapper;
import com.tapjoy.reach.helper.Helper;
import com.tapjoy.reach.params.KeyEnum;
import com.tapjoy.reach.params.Personas;
import com.tapjoy.reach.params.Source;
import com.tapjoy.reach.service.ErrorModel;
import com.tapjoy.reach.service.ResponseModel;
import com.tapjoy.reach.utils.PersonaOverlapRateMap;
import com.tapjoy.reach.utils.SourceUniqueRateMap;

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
			
			List<String> sources = params.get(KeyEnum.getValue(KeyEnum.source));

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

	private ResponseModel getHBaseResults(Set<String> keySet,
			List<String> personas, List<String> sources) {
		long reachAudienceCount = 0;
		long impCount = 0;
		long uniqueViewersCount = 0;
		ResponseModel model = null;
		List<String> keyList = new ArrayList<String>(keySet);
		List<HBaseTask> tasks = new ArrayList<HBaseTask>();
		for(String key:keyList){
			key = key.toUpperCase();
			HBaseTask task = new HBaseTask(key, CountsHbaseConstants.COUNTS_TABLE,
						CountsHbaseConstants.TOKEN);
			tasks.add(task);
		}
		
		List<Future<Result>> results = HBaseThreadPool.getInstance().submitHBaseTaskList(tasks);
		if(results == null || results.size() < keyList.size()){
			ErrorModel errorModel = new ErrorModel(HttpResponseStatus.INTERNAL_SERVER_ERROR.getCode(), "HBase error");
			String error = gson.toJson(errorModel);
			model = new ResponseModel(error,
					HttpResponseStatus.INTERNAL_SERVER_ERROR,
					"application/json");
			return model;
		}
		
		for(int i = 0;i < keyList.size();i++){
			try {
				Result res = results.get(i).get();
				if (res == null || res.list() == null) {
					logger.error("No targeting found for key:" + keyList.get(i));
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
						reachAudienceCount += Long.parseLong(splits[0]);
						uniqueViewersCount += Long.parseLong(splits[1]);
						impCount += Long.parseLong(splits[2]);					
					}
					
				}
				
				else{
					String reachAudienceColQualifier = "ra";
					String reachAudienceValue = HBaseWrapper.getHBaseResultToString(
							res, CountsHbaseConstants.COLUMN_FAMILY,
							reachAudienceColQualifier);
					if (StringUtils.isNotBlank(reachAudienceValue)) {
						reachAudienceCount += Long.parseLong(reachAudienceValue);
					}
					
					String impColQualifier = "im";
					String impValue = HBaseWrapper.getHBaseResultToString(
							res, CountsHbaseConstants.COLUMN_FAMILY,
							impColQualifier);
					if (StringUtils.isNotBlank(impValue)) {
						impCount += Long.parseLong(impValue);
					}
					
					String uniqueViewersColQualifier = "uv";
					String uniqueViewersValue = HBaseWrapper.getHBaseResultToString(
							res, CountsHbaseConstants.COLUMN_FAMILY,
							uniqueViewersColQualifier);
					if (StringUtils.isNotBlank(uniqueViewersValue)) {
						uniqueViewersCount += Long.parseLong(uniqueViewersValue);
					}
				
				}
				
			} catch (InterruptedException | ExecutionException e) {
				logger.error("error",e);
				ErrorModel errorModel = new ErrorModel(HttpResponseStatus.INTERNAL_SERVER_ERROR.getCode(), "HBase error");
				String error = gson.toJson(errorModel);
				model = new ResponseModel(error,
						HttpResponseStatus.INTERNAL_SERVER_ERROR,
						"application/json");
				return model;
			}
		}
	
		CountsModel countsModel = new CountsModel(reachAudienceCount, impCount, uniqueViewersCount);
		
		if(personas != null && personas.size() > 1){
			applyPersonaOverlapRate(countsModel, personas);
		}
		
		if(sources == null){
			sources = new ArrayList<String>();
			sources.add("$");
		}
		applySourceOverlapRate(countsModel, sources);
		
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
	
	private List<String> generateCombinations(List<String> keys) {
		
		List<String> combinations = new ArrayList<String>();
		ICombinatoricsVector<String> initialVector = Factory
				.createVector(keys);
		
		Generator<String> gen = Factory.createSimpleCombinationGenerator(
				initialVector, 2);
		
		for (ICombinatoricsVector<String> combination : gen) {
		      List<String> elements = combination.getVector();
		      Collections.sort(elements);
		      String c = StringUtils.join(elements, ":");
		      combinations.add(c);
		   }
		
		
		return combinations;
	}
	
	private void applyPersonaOverlapRate(CountsModel model, List<String> personas){
		List<String> personaIds = new ArrayList<String>();
		for(String p : personas){
			Integer id = Personas.getInstance().getPersonaId(p);
			personaIds.add(id.toString());
		}
		List<String> combinations = generateCombinations(personaIds);
		double rate = 0.0;
		for(String c : combinations){
			String val = PersonaOverlapRateMap.getInstance().getRate(c);
			if(val == null){
				continue;
			}
			 rate += Double.parseDouble(val);
		}
		
		rate = 1 - rate;
		
		long udidsCount = model.getUdids_count();
		long uniqueViewers = model.getUnique_viewers_count();
		
		udidsCount = (long) (udidsCount * rate);
		uniqueViewers = (long) (uniqueViewers * rate);
		
		model.setUdids_count(udidsCount);
		model.setUnique_viewers_count(uniqueViewers);
		
	}
	
	private void applySourceOverlapRate(CountsModel model, List<String> sources){
		if(sources == null || sources.size() == 0){
			return;
		}
		long udidsCount = model.getUdids_count();
		long uniqueViewers = model.getUnique_viewers_count();
		
		double rate = 1.0;
		
		if(sources.size() == 1 && sources.get(0).contentEquals("$")){
			List<String> sourceNames = new ArrayList<String>();
			for(Source s : Source.values()){
				sourceNames.add(s.name());
			}
			Collections.sort(sourceNames);
			String sourceString = StringUtils.join(sourceNames, ":");
			String val = SourceUniqueRateMap.getInstance().getRate(sourceString);
			if(val == null){
				return;
			}
			rate = Double.parseDouble(val);
			uniqueViewers = (long) (uniqueViewers * rate);
			model.setUnique_viewers_count(uniqueViewers);
			return;
		}
		
		else if(sources.size() > 1) {
			
			String sourceString = StringUtils.join(sources, ":");
			String val = SourceUniqueRateMap.getInstance().getRate(sourceString);
			if(val == null){
				return;
			}
			rate = Double.parseDouble(val);
			udidsCount = (long) (udidsCount * rate);
			uniqueViewers = (long) (uniqueViewers * rate);
			
			model.setUdids_count(udidsCount);
			model.setUnique_viewers_count(uniqueViewers);
			return;
		}
	}

}
