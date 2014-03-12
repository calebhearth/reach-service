package com.tapjoy.reach.hbase;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.hadoop.hbase.util.Bytes;


//written by Robin Li for HBase key optimization

public class HBaseUtil {
	
	public static byte[] constructKey (int token_i, String udid_s){
		byte[] udid = udid_s.getBytes();
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			udid = md.digest(udid); 
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		int salt = ((int) udid[0])<<29 | (token_i);

		byte[] key = Bytes.add(Bytes.toBytes(salt), udid);
		return key;
	}
	
}
