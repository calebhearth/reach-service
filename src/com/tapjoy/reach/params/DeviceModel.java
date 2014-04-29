package com.tapjoy.reach.params;

public enum DeviceModel {
	
	Galaxy_S("Galaxy S"),Galaxy_S_mini("Galaxy S mini"),Galaxy_S_Duos("Galaxy S Duos"),Galaxy_S2("Galaxy S2"),
	Galaxy_S2_mini("Galaxy S2 mini"),Galaxy_S3("Galaxy S3"),Galaxy_S3_Mini("Galaxy S3 Mini"),Galaxy_S4("Galaxy S4"),
	Galaxy_S4_mini("Galaxy S4 mini"),Galaxy_Ace("Galaxy Ace"),Galaxy_Ace_2("Galaxy Ace 2"),Galaxy_Core("Galaxy Core"),
	Galaxy_Grand("Galaxy Grand"),Galaxy_Nexus("Galaxy Nexus"),Galaxy_Win("Galaxy Win"),Galaxy_Y("Galaxy Y"),One("One"),
	One_X("One X"),EVO_4G("EVO 4G"),Optimus_G("Optimus G"),Motion("Motion"),Nexus_4("Nexus 4"),Xperia_S("Xperia S"),
	Xperia_U("Xperia U"),Xperia_Z("Xperia Z"),Xperia_Z1("Xperia Z1"),Droid_RAZR("Droid RAZR"),Droid("Droid"),
	Droid_X("Droid X"),Droid_Ultra("Droid Ultra"),Moto("Moto"),PadFone("PadFone"),PadFone_2("PadFone 2"),Nexus("Nexus"),
	Nexus_5("Nexus 5"),Galaxy_Tab("Galaxy Tab"),Galaxy_Tab_2("Galaxy Tab 2"),Galaxy_Tab_3("Galaxy Tab 3"),Galaxy_Note("Galaxy Note"),
	Galaxy_Note_2("Galaxy Note 2"),Galaxy_Note_3("Galaxy Note 3"),Nexus_10("Nexus 10"),Kindle_Fire("Kindle Fire"),
	Kindle_Fire_HD_7("Kindle Fire HD 7"),Kindle_Fire_XD("Kindle Fire XD"),Nexus_7("Nexus 7"),Nook("Nook"),iPad_1G("iPad 1G"),
	iPad_mini_1G("iPad mini 1G"), iPad_2("iPad 2"),iPad_3("iPad 3"),iPad_4("iPad 4"),iPad_Air("iPad Air"),iPad_Mini_2G("iPad Mini 2G"),
	iPhone_2G("iPhone 2G"),iPhone_3G("iPhone 3G"),iPhone_3GS("iPhone 3GS"),iPhone_4("iPhone 4"),iPhone_4S("iPhone 4S"),iPhone_5("iPhone 5"),
	iPhone_5C("iPhone 5C"),iPhone_5S("iPhone 5S"),iPod_touch_1G("iPod touch 1G"),iPod_touch_2G("iPod touch 2G"),iPod_touch_3G("iPod touch 3G"),
	iPod_touch_4G("iPod touch 4G"),iPod_touch_5G("iPod touch 5G"), MeMo("MeMo"), Transformer_Pad("Transformer Pad"),Memo_Pad("Memo Pad"),
	FonePad("FonePad"),Slate("Slate"),Slate_7("Slate 7"),Touchpad("Touchpad"),Iconia("Iconia"),Iconia_Tab("Iconia Tab"),XOOM("XOOM"),
	XOOM_2("XOOM 2"),IdeaTab("IdeaTab"),IdeaPad("IdeaPad"),MediaPad("MediaPad"),Xperia("Xperia"),Xperia_Tablet("Xperia Tablet"),
	Xperia_ZL("Xperia ZL"),Tablet_S("Tablet S");
	
	private String keyword;
	
	private DeviceModel(String key) {
		this.keyword = key;
	}
	
	public static DeviceModel getEnum(String text){
		text = text.toLowerCase();
		for(DeviceModel a:DeviceModel.values()){
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
