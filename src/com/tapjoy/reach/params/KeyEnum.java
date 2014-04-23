package com.tapjoy.reach.params;

public enum KeyEnum {
	//device_size("device size"),
	source("sources"),
	language("language"),
	apple_product_line("apple_product_line"),
	platform("platform"),
	device_os_version("device_os_version"),
	device_manufacturer("device_manufacturer"),	
	device_type("device_type"),
	device_size("device_size"),
	geoip_continent("geoip_continent"),
	geoip_country("geoip_country"),
	geoip_region("regions"),
	//Always put personas in the end
	persona_name("personas");
	
	private final String keyword;
	
	private KeyEnum(String key){
		this.keyword = key;
	}
	
	public static KeyEnum getEnum(String val){
		for(KeyEnum key: KeyEnum.values()){
			if(key.keyword.equalsIgnoreCase(val)){
				return key;
			}
		}
		return null;
	}
	
	public static String getValue(KeyEnum key){
		return key.keyword;
	}
}
