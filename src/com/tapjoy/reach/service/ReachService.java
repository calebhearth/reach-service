package com.tapjoy.reach.service;

import org.apache.log4j.Logger;

import com.tapjoy.reach.hbase.HBaseWrapper;
import com.tapjoy.reach.utils.CountryContinentMap;
import com.tapjoy.reach.utils.ManufacturerSizeMap;
import com.tapjoy.reach.utils.ModelManufacturerMap;
import com.tapjoy.reach.utils.PersonaOverlapRateMap;
import com.tapjoy.reach.utils.RegionCountryMap;
import com.tapjoy.reach.utils.SourceUniqueRateMap;


public class ReachService {
	
	private static Logger logger = Logger.getLogger(ReachService.class);
	
	// Load the external data, initialize service engines
	private static void initializeAll(String [] args){
		
		
		//before HBase init, try ping the server 
		HBaseWrapper.init();  //changed by LJ  //temporarily commented out 09/20
		//HBaseConcurrentGet.init();
		CountryContinentMap.getInstance();
		ManufacturerSizeMap.getInstance();
		ModelManufacturerMap.getInstance();
		PersonaOverlapRateMap.getInstance();
		RegionCountryMap.getInstance();
		SourceUniqueRateMap.getInstance();
		
	}
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		logger.info("++++++++++++++++++++++++++");
		logger.info("Reach service started");
		logger.info("++++++++++++++++++++++++++");
		
		
		
		initializeAll(args);
		
		
		// Start Reach service to accept requests, register the shutdown hook
		final ServiceEngine engine = ServiceEngine.getInstance();
		Runtime.getRuntime().addShutdownHook(new Thread(){
			public void run(){
				engine.shutDown();
			}
		});
		
		//HBaseConn.shutdown();
	}


}
