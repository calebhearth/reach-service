package com.tapjoy.reach.counts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

public class KeyParser {

	private List<String> keyList;

	public KeyParser() {
		keyList = new ArrayList<String>();
	}

	public List<String> collectKeys(String key,
			List<Entry<String, List<String>>> entries) {
		if (entries.size() == 1) {
			Entry<String, List<String>> entry = entries.get(0);
			List<String> value = entry.getValue();
			for (String v : value) {
				String newKey = key + (key.length() > 0 ? "-" + v : v);
				keyList.add(newKey);
			}
			return keyList;
		}

		Entry<String, List<String>> entry = entries.get(0);
		List<String> value = entry.getValue();
		for (String v : value) {
			String newKey = key + (key.length() > 0 ? "-" + v : v);
			List<Entry<String, List<String>>> subList = entries.subList(1,
					entries.size());
			collectKeys(newKey, subList);
		}

		return keyList;
	}

}
