package com.tapjoy.reach.counts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import com.tapjoy.reach.params.KeyEnum;

public class KeyParser {

	private List<String> keyList;

	public KeyParser() {
		keyList = new ArrayList<String>();
	}

	public List<String> collectKeys(String key,
			List<Entry<String, List<String>>> entries,int presentPos) {
		/*if (entries.size() == 1) {
			Entry<String, List<String>> entry = entries.get(0);
			List<String> value = entry.getValue();
			int numKeys;
			
			if(key.length() == 0){
				numKeys = 0;
			}
			else{
				numKeys = key.split("-").length;
			}
			
			
			for (String v : value) {
				String newKey = key + (key.length() > 0 ? "-" + v : v);
				keyList.add(newKey);
			}
			return keyList;
		}*/

		Entry<String, List<String>> entry = entries.get(0);
		String param = entry.getKey();
		KeyEnum keyEnum = KeyEnum.fromString(param);
		int rank = keyEnum.ordinal();
		//int presentPos = 0;
		
	/*	if (key.length() > 0) {
			String[] keyParts = key.split("-");
			presentPos = keyParts.length;
		}*/
		
		if (rank > presentPos) {
			for (int i = 1; i <= rank - presentPos; i++) {
				key = (key.length() > 0 ? key+"-" : key) + "$";
			}
		}
		
		presentPos = rank;
		
		List<String> value = entry.getValue();
		for (String v : value) {
			if(keyEnum.equals(KeyEnum.device_os_version)){
				if(StringUtils.containsIgnoreCase(key, "ios")){
					Double d = Double.parseDouble(v);
					int osv = d.intValue();
					v = Integer.toString(osv);
				}
			}
			String newKey = (key.length() > 0 ? key+"-" : key) + v;
			List<Entry<String, List<String>>> subList = entries.subList(1,
					entries.size());
			if(subList.size() > 0){
				collectKeys(newKey, subList, presentPos+1);
			}
			else{
				int length = KeyEnum.values().length - 1;
				if(rank < length-1){ //subtract personas
					//append $
					int diff = (length-1) - rank;
					for(int i =1;i<= diff; i++ ){
						newKey = (newKey.length() > 0 ? newKey+"-" : newKey) + "$";
					}				
				}
				keyList.add(newKey);
			}
		}

		return keyList;
	}

}
