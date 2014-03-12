package com.tapjoy.reach.counts;

public enum KeyEnum {
	device_size("device size"),
	platform("platform"),
	device_os_version("os_versions"),
	device_manufacturer("manufacturers"),
	device_model("device_types"),
	language("languages"),
	persona_name("personas"),
	source("sources"),
	geoip_continent("continents"),
	geoip_country("countries"),
	geoip_region("regions");
	
	private final String keyword;
	
	private KeyEnum(String key){
		this.keyword = key;
	}
	
	public static KeyEnum fromString(String val){
		for(KeyEnum key: KeyEnum.values()){
			if(key.keyword.equalsIgnoreCase(val)){
				return key;
			}
		}
		return null;
	}
}
