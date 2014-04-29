package com.tapjoy.reach.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

public class DeviceOsVersionMap {
	
	private static DeviceOsVersionMap instance = new DeviceOsVersionMap();
	private Logger logger = Logger.getLogger(DeviceOsVersionMap.class);
	
	private Properties prop;
	
	private DeviceOsVersionMap(){
		
		BufferedReader  input = null;
		 
		try { 
			URL url = DeviceOsVersionMap.class.getClassLoader().getResource("device_os_versions.dat");
			String file = url.getPath();
			prop = new Properties();
			input = new BufferedReader(new FileReader(file));
			String line;
			while((line = input.readLine())!=null){
				String[] splits = line.split("=");
				prop.setProperty(splits[0].toUpperCase(), splits[1].toUpperCase());
			}
	 
		} catch (IOException ex) {
			logger.error(ex);
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					logger.error(e);
				}
			}
		}
		
	}
	
	public static DeviceOsVersionMap getInstance(){
		return instance;
	}
	
	public String getPlatforms(String version){
		return prop.getProperty(version);
	}

	public Set<Object> getKeys() {
		return prop.keySet();
	}
	

}
