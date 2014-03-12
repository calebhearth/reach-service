package com.tapjoy.reach.counts;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tapjoy.reach.config.OverallConfig;
import com.tapjoy.reach.hbase.HBaseWrapper;
import com.tapjoy.reach.helper.Helper;

public class CountsHelper implements Helper<String> {
	
	private static Gson gson = new GsonBuilder().create();

	@Override
	public String getResult(Map<String, List<String>> p) {
		Map<String, List<String>> params = new HashMap<String, List<String>>(p);
		Set<Entry<String, List<String>>> entries = params.entrySet();
		List<Entry<String, List<String>>> entriesList = new ArrayList<Map.Entry<String,List<String>>>(entries);
		Collections.sort(entriesList, new KeyComparator());
		List<String> keyList = new KeyParser().collectKeys("", entriesList);
		String countsJson = getHBaseResults(keyList);
		return countsJson;
	}


	private String getHBaseResults(List<String> keyList) {
		int udidsCount = 0;
		int impCount = 0;
		for(String key:keyList){
			Result res = null;
			try {
				res = HBaseWrapper.getOneRecordInTable(key, OverallConfig.COUNTS_TABLE, 0);
			} catch (ClassNotFoundException | SQLException | InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if (res == null) {
				System.out.println("error");
			}
			List<String> list = HBaseWrapper.getHBaseResultToArray(res, "v", "val", 10);
			System.out.println(list);
			for (KeyValue rawVal : res.raw()) {
				byte[] bytes = rawVal.getValue();
				String val = Bytes.toString(bytes);
				if (val == null || val.equals("null")) {
					continue;
				}

				System.out.println(val);
				String[] counts=val.split(":");
				udidsCount += Integer.parseInt(counts[0]);
				impCount += Integer.parseInt(counts[1]);
			}
		} 
		CountsModel countsModel = new CountsModel(udidsCount, impCount);
		String json = gson.toJson(countsModel);
		return json;
	}
	

}
