package com.tapjoy.reach.params;

public enum DeviceManufacturer {
	
	Samsung, HTC, LG, Sony, Motorola, Xiaomi, Huawei, ASUS, Google, Lenovo, Kyocera, Apple, Amazon, 
	Barnes_Noble {
		@Override
		public String toString() {
			return "Barnes & Noble";
		}
	}, 
	HP, Acer;
	
	public static DeviceManufacturer getEnum(String text){
		text = text.toLowerCase();
		for(DeviceManufacturer a:DeviceManufacturer.values()){
			if(text.equalsIgnoreCase(a.toString())){
				return a;
			}
		}
		return null;
	}

}
