package com.tapjoy.reach.params;
public enum States {
	// US states
	AL("Alabama"), AK("Alaska"), AZ("Arizona"), AR("Arkansas"), CA("California"), CO("Colorado"),
	CT("Connecticut"), DE("Delaware"), FL("Florida"), GA("Georgia"), HI("Hawaii"), ID("Idaho"), 
	IL("Illinois"), IN("Indiana"), IA("Iowa"), KS("Kansas"), KY("Kentucky"), LA("Louisiana"),
	ME("Maine"), MD("Maryland"), MA("Massachusetts"), MI("Michigan"), MN("Minnesota"), MS("Mississippi"), 
	MO("Missouri"), MT("Montana"), NE("Nebraska"), NV("Nevada"), NH("New Hampshire"), NJ("New Jersey"),
	NM("New Mexico"), NY("New York"), NC("North Carolina"), ND("North Dakota"), OH("Ohio"), OK("Oklahoma"),
	OR("Oregon"), PA("Pennsylvania"), RI("Rhode Island"), SC("South Carolina"), SD("South Dakota"), 
	TN("Tennessee"), TX("Texas"), UT("Utah"), VT("Vermont"), VA("Virginia"), WA("Washington"), 
	WV("West Virginia"), WI("Wisconsin"), WY("Wyoming"), DC("District of Columbia"), AS("American Samoa"), 
	GU("Guam"), MP("Northern Mariana Islands"), PR("Puerto Rico"), VI("U.S. Virgin Islands"),
	// Canada states
	NB("New Brunswick"), NU("Nunavut"), NL("Newfoundland and Labrador"), MB("Manitoba"), YT("Yukon"),
	BC("British Columbia"), PE("Prince Edward Island"), NT("Northwest Territories"), QC("Quebec"), 
	NS("Nova Scotia"), AB("Alberta"), SK("Saskatchewan"), ON("Ontario");
	
	private String keyword;
	
	private States(String k) {
		this.keyword = k;
	}
	
	public static States getEnum(String state) {
		for (States c : States.values()) {
			if (c.name().equalsIgnoreCase(state)) {
				return c;
			}
		}
		return null;
	}
	
	public String getKeyword(){
		return keyword;
	}

}
