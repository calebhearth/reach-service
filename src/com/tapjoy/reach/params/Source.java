package com.tapjoy.reach.params;

public enum Source {
	premium("premium"), secondary("secondary"), direct_play("direct play"), offerwall("offerwall"), 
	featured("featured"), display_ad("display ad"), 
	video_carousel("video carousel"), tj_games("tapjoy.com"), publisher_message("publisher message");
	private String keyword;

	private Source(String code) {
		this.keyword = code;
	}
	
	public static boolean hasValue(String source) {
		for (Source c : Source.values()) {
			if (c.name().equalsIgnoreCase(source)) {
				return true;
			}
		}
		return false;
	}
	
	public static Source fromString(String val){
		for(Source key: Source.values()){
			if(key.keyword.equalsIgnoreCase(val)){
				return key;
			}
		}
		return null;
	}
}
