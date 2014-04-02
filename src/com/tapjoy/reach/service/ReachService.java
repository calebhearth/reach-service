package com.tapjoy.reach.service;

import org.apache.log4j.Logger;

import com.tapjoy.reach.hbase.HBaseWrapper;


public class ReachService {
	
	private static Logger logger = Logger.getLogger(ReachService.class);
	
	// Load the external data, initialize service engines
	private static void initializeAll(String [] args){
		
		
		//before HBase init, try ping the server 
		HBaseWrapper.init();  //changed by LJ  //temporarily commented out 09/20
		//HBaseConcurrentGet.init();
		
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
