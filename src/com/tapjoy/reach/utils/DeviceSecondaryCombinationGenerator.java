package com.tapjoy.reach.utils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.tapjoy.reach.params.KeyEnum;

public class DeviceSecondaryCombinationGenerator {
	
	public Set<String> getDeviceSecondaryCombinations(Map<String, List<String>> params) {
		Set<String> keys = new HashSet<String>();
		Set<String> sizes = new HashSet<String>();
		if (params.get(KeyEnum.getValue(KeyEnum.device_size)) != null) {
			sizes.addAll(params.get(KeyEnum
					.getValue(KeyEnum.device_size)));
		}

		Set<String> manufacturers = new HashSet<String>();
		if (params.get(KeyEnum.getValue(KeyEnum.device_manufacturer)) != null) {
			manufacturers
					.addAll(params.get(KeyEnum.getValue(KeyEnum.device_manufacturer)));
		}

		Set<String> models = new HashSet<String>();
		if (params.get(KeyEnum.getValue(KeyEnum.device_type)) != null) {
			models.addAll(params.get(KeyEnum.getValue(KeyEnum.device_type)));
		}

		for (String model : models) {
			String value = ModelManufacturerMap.getInstance().getManufacturer(model);
			String[] splits = value.split(",");
			String manufacturer = splits[0].toUpperCase();
			String size = splits[1].toUpperCase();
			String key = size + "-" + manufacturer + "-" + model;
			keys.add(key);
			sizes.remove(size);
			manufacturers.remove(manufacturer);
		}

		for (String manufacturer : manufacturers) {
			String value = ManufacturerSizeMap.getInstance().getSize(
					manufacturer);
			String[] size = value.split(",");
			for(String s : size){
				if(!StringUtils.isBlank(s)){
					s = s.toUpperCase();
					String key = s + "-" + manufacturer + "-$";
					keys.add(key);
					sizes.remove(s);
				}
			}
		}

		for (String size : sizes) {
			String key = size + "-$-$";
			keys.add(key);
		}

		if (keys.size() == 0) {
			keys.add("$-$-$");
		}

		return keys;
	}

}
