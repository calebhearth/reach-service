package com.tapjoy.reach.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import org.apache.log4j.Logger;

public class SourceUniqueRateMap {

	private static SourceUniqueRateMap instance = new SourceUniqueRateMap();
	private Logger logger = Logger.getLogger(SourceUniqueRateMap.class);

	private Properties prop;

	private SourceUniqueRateMap() {

		URL url = SourceUniqueRateMap.class.getClassLoader().getResource(
				"source_unique_rate.dat");
		String file = url.getPath();
		prop = new Properties();

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String line = "";
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split("\\s+");
				String key = parts[0].toUpperCase();
				String value = parts[1];
				prop.put(key, value);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			logger.error("Exception happened");
			logger.error(ex);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
					logger.error(e);
				}
			}
		}

	}

	public static SourceUniqueRateMap getInstance() {
		return instance;
	}

	public String getRate(String key) {
		return prop.getProperty(key);
	}

}
