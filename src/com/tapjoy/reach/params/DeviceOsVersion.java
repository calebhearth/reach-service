package com.tapjoy.reach.params;

public enum DeviceOsVersion {
	
	v2_0("2.0"),v2_1("2.1"),v2_2("2.2"),v2_3("2.3"),v3_0("3.0"),v3_1("3.1"),v3_2("3.2"),v4_0("4.0"),v4_1("4.1"),
	v4_2("4.2"),v4_3("4.3"),v4_4("4.4"),v5_0("5.0"),v5_1("5.1"),v6_0("6.0"),v6_1("6.1"),v7_0("7.0"),v7_1("7.1");
	
	private String keyword;
	
	private DeviceOsVersion(String key) {
		this.keyword = key;
	}
	
	public static DeviceOsVersion getEnum(String text){
		text = text.toLowerCase();
		for(DeviceOsVersion a:DeviceOsVersion.values()){
			if(text.equalsIgnoreCase(a.keyword)){
				return a;
			}
		}
		return null;
	}
	
	public String getKeyword(){
		return keyword;
	}

}
