package com.tapjoy.reach.params;

public enum Platform {
	ANDROID("android"),WINDOWS("windows"),IOS("ios"), UNKNOWN("_");
	
	private String keyword;
	
	private Platform(String key) {
		this.keyword = key;
	}
	
	public static Platform getEnum(String text){
		text = text.toLowerCase();
		for(Platform a:Platform.values()){
			if(text.contains(a.keyword)){
				return a;
			}
		}
		return Platform.UNKNOWN;
	}

	public static Platform fromString(String val){
		for(Platform key: Platform.values()){
			if(key.keyword.equalsIgnoreCase(val)){
				return key;
			}
		}
		return null;
	}


}
