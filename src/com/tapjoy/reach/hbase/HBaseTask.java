package com.tapjoy.reach.hbase;

import java.io.IOException;
import java.util.concurrent.Callable;

import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.HTablePool;
import org.apache.hadoop.hbase.client.Result;
import org.apache.log4j.Logger;

public class HBaseTask implements Callable<Result> {

	private String key;
	private String table;
	private int token;

	private static Logger logger = Logger.getLogger(HBaseTask.class);

	public HBaseTask(String key, String table, int token) {
		this.key = key;
		this.table = table;
		this.token = token;
	}

	@Override
	public Result call() throws IOException {
		byte[] rowKey = null;
		HTablePool htpool = HBaseWrapper.getHPool();
		HTableInterface hTable = null;
		try {
			hTable = htpool.getTable(table);
			rowKey = HBaseUtil.constructKey(token, key);
			Get get = new Get(rowKey);
			Result rs = hTable.get(get);
			return rs;
		} finally {
			if (hTable != null) {
				try {
					hTable.close();
				} catch (IOException e) {
					logger.error(e);
					e.printStackTrace();
				}
			}
		}
	}

}
