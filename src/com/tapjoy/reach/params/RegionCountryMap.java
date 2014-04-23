package com.tapjoy.reach.params;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

public class RegionCountryMap {
	
	private static RegionCountryMap instance = new RegionCountryMap();
	private Logger logger = Logger.getLogger(RegionCountryMap.class);
	
	private Properties prop;
	
	private RegionCountryMap(){
		
		InputStream  input = null;
		 
		try { 
			URL url = CountryContinentMap.class.getClassLoader().getResource("region_to_country.dat");
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
	
	public static RegionCountryMap getInstance(){
		return instance;
	}
	
	public String getCountry(String region){
		return prop.getProperty(region);
	}
	
	public boolean hasCountry(String region){
		if(prop.getProperty(region) != null){
			return true;
		}
		return false;
	}

	public Set<Object> getKeys() {
		return prop.keySet();
	}
	

}
