package com.tapjoy.reach.hbase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.hadoop.hbase.client.Result;
import org.apache.log4j.Logger;

public class HBaseThreadPool {
	
	private ExecutorService service = Executors.newFixedThreadPool(10);
	
	private static HBaseThreadPool instance = new HBaseThreadPool();
	private static Logger logger = Logger.getLogger(HBaseThreadPool.class);
	
	private HBaseThreadPool(){
		
	}
	
	public static HBaseThreadPool getInstance(){
		return instance;
	}
	
	public List<Future<Result>> submitHBaseTaskList(List<HBaseTask> tasks){
		try {
			List<Future<Result>> results = service.invokeAll(tasks);
			return results;
		} catch (InterruptedException e) {
			logger.error(e);
			e.printStackTrace();
		}
		return null;
	}

}
