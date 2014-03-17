package com.tapjoy.reach.params;
public enum Countries {
	// RW - rest of world
	AR, AU, BR, CA, CL, CN, CO, DE, ES, FR, GB, GR, HK, ID, IL, IN, IT, JP, KR, KW, MX, MY, NL, RU, SA, SE, SG, TH, TR, TW, US, RW;

	public static boolean hasValue(String country) {
		for (Countries c : Countries.values()) {
			if (c.name().equalsIgnoreCase(country)) {
				return true;
			}
		}
		return false;
	}
}
