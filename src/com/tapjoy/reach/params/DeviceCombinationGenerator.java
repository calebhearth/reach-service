package com.tapjoy.reach.params;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class DeviceCombinationGenerator {

	public Set<String> getDeviceCombinations(Map<String, List<String>> params) {
		Set<String> keys = new TreeSet<String>();
		
		Set<String> platforms = new TreeSet<String>();
		Set<String> appleProductLine = new TreeSet<String>();
		if (params.get(KeyEnum.getValue(KeyEnum.apple_product_line)) != null) {
			appleProductLine.addAll(params.get(KeyEnum
					.getValue(KeyEnum.apple_product_line)));
			platforms.add(Platform.IOS.toString());
		} else {
			appleProductLine.add("$");
		}
		
		if (params.get(KeyEnum.getValue(KeyEnum.platform)) != null) {
			platforms.addAll(params.get(KeyEnum.getValue(KeyEnum.platform)));
		}
		else{
			if(params.get(KeyEnum.getValue(KeyEnum.device_os_version)) == null && platforms.size() == 0){
				platforms.add("$");
			}
			else if(params.get(KeyEnum.getValue(KeyEnum.device_os_version)) != null && platforms.size() == 0) {
				platforms.add(Platform.IOS.toString());
				platforms.add(Platform.ANDROID.toString());
			}
		}
		
		for (String platform : platforms) {
			if (platform.equalsIgnoreCase(Platform.IOS.toString())) {
				Set<String> cleanVersions = new TreeSet<String>();
				if (params.get(KeyEnum.getValue(KeyEnum.device_os_version)) != null) {
					Set<String> versions = new TreeSet<String>(
							params.get(KeyEnum
									.getValue(KeyEnum.device_os_version)));
					if (versions.size() > 0) {
						for (String v : versions) {
							int version = (int) Double.parseDouble(v);
							cleanVersions.add(version + "");
						}
					}
				} else {
					cleanVersions.add("$");
				}

				for (String apl : appleProductLine) {
					for (String v : cleanVersions) {
						String key = apl + "-" + Platform.IOS.toString() + "-"
								+ v;
						keys.add(key);
					}
				}

			}

			else if (platform.equalsIgnoreCase(Platform.ANDROID.name())) {
				Set<String> versions = new TreeSet<String>();
				if (params.get(KeyEnum.getValue(KeyEnum.device_os_version)) != null) {
					versions.addAll(params.get(KeyEnum
							.getValue(KeyEnum.device_os_version)));
				} else {
					versions.add("$");
				}

				for (String v : versions) {
					String key = "$-" + Platform.ANDROID.toString() + "-" + v;
					keys.add(key);
				}
				

			}
			
			else{
				keys.add("$-$-$");
			}
		}

		return keys;
	}

}
