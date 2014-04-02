package com.tapjoy.reach.counts;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;

import com.tapjoy.reach.params.AppleProductLine;
import com.tapjoy.reach.params.KeyEnum;
import com.tapjoy.reach.params.Platform;

public class KeyParser {

	private Set<String> keyList;
	
	public KeyParser() {
		keyList = new TreeSet<String>();
	}

	public Set<String> collectKeys(String key,
			List<Entry<String, List<String>>> entries,int presentPos) {

		Entry<String, List<String>> entry = entries.get(0);
		String param = entry.getKey();
		KeyEnum keyEnum = KeyEnum.fromString(param);
		int rank = keyEnum.ordinal();
		
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
			if(keyEnum.equals(KeyEnum.platform)){
				if(v.equalsIgnoreCase(Platform.ANDROID.name()) || v.equalsIgnoreCase(Platform.WINDOWS.name())){
					newKey = removeAppleProductLine(newKey);
				}
			}
			newKey = newKey.toUpperCase();
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

	private String removeAppleProductLine(String newKey) {
		newKey = newKey.toUpperCase();
		for(AppleProductLine a : AppleProductLine.values()){
			newKey = StringUtils.replace(newKey, a.name().toUpperCase(), "$");
		}
		
		return newKey;
		
	}

}
