package com.tapjoy.reach.params;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import org.apache.log4j.Logger;

public class ModelManufacturerMap {
	
	private static ModelManufacturerMap instance = new ModelManufacturerMap();
	private Logger logger = Logger.getLogger(ModelManufacturerMap.class);
	
	private Properties prop;
	
	private ModelManufacturerMap(){
		
		BufferedReader  input = null;
		 
		try { 
			URL url = ModelManufacturerMap.class.getClassLoader().getResource("model_to_manufacturer.dat");
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
	
	public static ModelManufacturerMap getInstance(){
		return instance;
	}
	
	public String getManufacturer(String model){
		return prop.getProperty(model);
	}
	
	public boolean hasManfucturer(String model){
		if(prop.getProperty(model) != null){
			return true;
		}
		return false;
	}
	

}
