package com.tapjoy.reach.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

public class CountryContinentMap {
	
	private static CountryContinentMap instance = new CountryContinentMap();
	private Logger logger = Logger.getLogger(CountryContinentMap.class);
	
	private Properties prop;
	
	private CountryContinentMap(){
		
		InputStream  input = null;
		 
		try { 
			URL url = CountryContinentMap.class.getClassLoader().getResource("country_to_continent.dat");
			String file = url.getPath();
			prop = new Properties();
			input = new FileInputStream(file);
			prop.load(input);
	 
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
	
	public static CountryContinentMap getInstance(){
		return instance;
	}
	
	public String getContinent(String country){
		return prop.getProperty(country);
	}
	
	public boolean hasContinent(String country){
		if(prop.getProperty(country) != null){
			return true;
		}
		return false;
	}
	
	public Set<Object> getKeys(){
		return prop.keySet();
	}
	
	

}
