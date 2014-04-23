package com.tapjoy.reach.params;
public enum Continents {
	SA("South America"), AS("Asia"), AF("Africa"), EU("Europe"), OC("Oceania"), NA("North America");
	
	private String keyword;
	
	private Continents(String k) {
		this.keyword = k;
	}

	public static Continents getEnum(String continent) {
		for (Continents c : Continents.values()) {
			if (c.name().equalsIgnoreCase(continent)) {
				return c;
			}
		}
		return null;
	}
	
	public String getKeyword(){
		return keyword;
	}

}
