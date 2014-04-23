package com.tapjoy.reach.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.apache.log4j.Logger;

public class HBaseDataSource {
	
	private Configuration config;
	private Logger logger = Logger.getLogger(HBaseDataSource.class);
	
	private static HBaseDataSource instance = new HBaseDataSource();
	
	private HBaseDataSource() {
		config = HBaseConfiguration.create(); 
	}
	
	public static HBaseDataSource getInstance(){
		return instance;
	}
	
	public HConnection getConnection(){
		try {
			return HConnectionManager.createConnection(config);
		} catch (ZooKeeperConnectionException e) {
			logger.error(e);
			e.printStackTrace();
		}
		return null;
	}

}
