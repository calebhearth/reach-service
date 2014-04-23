package com.tapjoy.reach.params;
public enum Countries {
	// RW - rest of world
	AR("Argentina"), AU("Australia"), BR("Brazil"), CA("Canada"), CL("Chile"), CN("China"), 
	CO("Colombia"), DE("Germany"), ES("Spain"), FR("France"), GB("United Kingdom"), GR("Greece"), 
	HK("Hong Kong"), ID("Indonesia"), IL("Israel"), IN("India"), IT("Italy"), JP("Japan"), KR("Korea"),
	KW("Kuwait"), MX("Mexico"), MY("Malaysia"), NL("Netherlands"), RU("Russia"), SA("Saudi Arabia"), 
	SE("Sweden"), SG("Singapore"), TH("Thailand"), TR("Turkey"), TW("Taiwan"), US("United States");

	private String keyword;
	
	private Countries(String k) {
		this.keyword = k;
	}
	
	public static Countries getEnum (String country) {
		for (Countries c : Countries.values()) {
			if (c.name().equalsIgnoreCase(country)) {
				return c;
			}
		}
		return null;
	}
	
	public String getKeyword(){
		return keyword;
	}
}
