package com.tapjoy.reach.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

public class ManufacturerSizeMap {
	
	private static ManufacturerSizeMap instance = new ManufacturerSizeMap();
	private Logger logger = Logger.getLogger(ManufacturerSizeMap.class);
	
	private Properties prop;
	
	private ManufacturerSizeMap(){
		
		BufferedReader  input = null;
		 
		try { 
			URL url = CountryContinentMap.class.getClassLoader().getResource("manufacturer_to_size.dat");
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
	
	public static ManufacturerSizeMap getInstance(){
		return instance;
	}
	
	public String getSize(String manufacturer){
		return prop.getProperty(manufacturer);
	}
	
	public boolean hasSize(String manufacturer){
		if(prop.getProperty(manufacturer) != null){
			return true;
		}
		return false;
	}
	
	public Set<Object> getKeys() {
		return prop.keySet();
	}
	

}
