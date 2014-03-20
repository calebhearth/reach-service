package com.tapjoy.reach.params;

public enum Source {
	premium("premium"), direct_play("direct_play"), offerwall("offerwall"), 
	featured("featured"), display_ad("display_ad"), 
	video_carousel("video_carousel"), tj_games("tapjoy.com"), publisher_message("publisher_message");
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
