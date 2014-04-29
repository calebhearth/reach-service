package com.tapjoy.reach.config;

public class OverallConfig {

	public static int REACH_PORT = 8888;
	public static long REACH_TIMEOUT_THRES = 190;

	public static int NETTY_EXEC_HANDLER_THREADPOOL_SIZE = 40; // more threads

	public static String REACH_HOME_DIR = OverallConfig.class
			.getProtectionDomain().getCodeSource().getLocation().getPath()
			.split("ReachService")[0]
			+ "../bin/";
	
	// The actual request will be used for health check ("http://reach:8888/health")
		public static String healthCheck = "/api/v1/?geoip_country=US&sources=offerwall&geoip_continent=NA";	
	
	

}
