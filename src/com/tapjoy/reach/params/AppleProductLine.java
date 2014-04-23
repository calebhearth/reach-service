package com.tapjoy.reach.params;
public enum AppleProductLine {
	IPAD("ipad"),
	IPHONE("iphone"),
	ITOUCH("itouch");
	
	private String keyword;
	
	private AppleProductLine(String key) {
		this.keyword = key;
	}
	
	public static AppleProductLine matchText(String text){
		text = text.toLowerCase();
		for(AppleProductLine a:AppleProductLine.values()){
			if(text.contains(a.keyword)){
				return a;
			}
		}
		return null;
	}
	
	public static AppleProductLine getEnum(String val){
		for(AppleProductLine key: AppleProductLine.values()){
			if(key.keyword.equalsIgnoreCase(val)){
				return key;
			}
		}
		return null;
	}

}