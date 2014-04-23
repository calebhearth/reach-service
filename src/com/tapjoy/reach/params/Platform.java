package com.tapjoy.reach.params;

public enum Platform {
	ANDROID("android"),IOS("ios"),WINDOWS("windows");
	
	private String keyword;
	
	private Platform(String key) {
		this.keyword = key;
	}
	
	public static Platform matchText(String text){
		text = text.toLowerCase();
		for(Platform a:Platform.values()){
			if(text.contains(a.keyword)){
				return a;
			}
		}
		return null;
	}

	public static Platform getEnum(String val){
		for(Platform key: Platform.values()){
			if(key.keyword.equalsIgnoreCase(val)){
				return key;
			}
		}
		return null;
	}


}
