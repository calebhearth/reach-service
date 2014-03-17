package com.tapjoy.reach.params;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class PlatformDeviceOsvDependency {

	public static PlatformDeviceOsvDependency instance = new PlatformDeviceOsvDependency();
	public Map<String, Integer> dependencyMap = new HashMap<String, Integer>();
	private final String dataFile = "platform_device_os_version.csv";
	private Logger logger = Logger.getLogger(PlatformDeviceOsvDependency.class);

	private PlatformDeviceOsvDependency() {
		//loadData();
	}

	public static PlatformDeviceOsvDependency getInstance() {
		return instance;
	}

	private void loadData() {
		URL url = PlatformDeviceOsvDependency.class.getClassLoader().getResource(dataFile);
		String file = url.getPath();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String line = "";
			int index = 1;
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split(",");
				//dependencyMap.put();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			logger.error(ex);
		} finally{
			if(reader != null){
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
					logger.error(e);
				}
			}
		}


	}
}
