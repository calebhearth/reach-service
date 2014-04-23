package com.tapjoy.reach.params;

public enum DeviceSize {
	PHONE("phone"), 
	TABLET("tablet");
	
	private String keyword;
	
	private DeviceSize(String key) {
		this.keyword = key;
	}
	
	public static DeviceSize getEnum(String text){
		text = text.toLowerCase();
		for(DeviceSize a:DeviceSize.values()){
			if(text.equalsIgnoreCase(a.keyword)){
				return a;
			}
		}
		return null;
	}


}
