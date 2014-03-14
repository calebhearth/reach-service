package com.tapjoy.reach.params;

public enum KeyEnum {
	//device_size("device size"),
	platform("platform"),
	device_os_version("os_versions"),
	//device_manufacturer("manufacturers"),
	apple_product_line("apple_product_line"),
	//device_model("device_types"),
	language("languages"),
	geoip_continent("continents"),
	geoip_country("countries"),
	geoip_region("regions"),
	//Always put personas and sources in the end
	persona_name("personas"),
	source("sources");
	
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
	
	public static String getValue(KeyEnum key){
		return key.keyword;
	}
}
