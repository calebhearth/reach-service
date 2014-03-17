package com.tapjoy.reach.params;
public enum Continents {
	SA, AS, AF, EU, OC, NA;

	public static boolean hasValue(String continent) {
		for (Continents c : Continents.values()) {
			if (c.name().equalsIgnoreCase(continent)) {
				return true;
			}
		}
		return false;
	}

}
