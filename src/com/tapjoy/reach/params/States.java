package com.tapjoy.reach.params;
public enum States {
	// US states
	AL, AK, AZ, AR, CA, CO, CT, DE, FL, GA, HI, ID, IL, IN, IA, KS, KY, LA, ME, MD, MA, MI, MN, MS, MO, MT, NE, NV, NH, NJ, NM, NY, NC, ND, OH, OK, OR, PA, RI, SC, SD, TN, TX, UT, VT, VA, WA, WV, WI, WY,
	// Canada states
	NB, NU, NL, MB, YT, BC, PE, NT, QC, NS, AB, SK, ON;

	public static boolean hasValue(String state) {
		for (States c : States.values()) {
			if (c.name().equalsIgnoreCase(state)) {
				return true;
			}
		}
		return false;
	}
}
