package com.tapjoy.reach.params;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class Personas {

	public static Personas instance = new Personas();
	public Map<String, Integer> personaMap = new HashMap<String, Integer>();
	private final String personaFile = "personas.txt";
	private Logger logger = Logger.getLogger(Personas.class);

	private Personas() {
		loadPersonas();
	}

	public static Personas getInstance() {
		return instance;
	}

	private void loadPersonas() {
		URL url = Personas.class.getClassLoader().getResource(personaFile);
		String file = url.getPath();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String persona = "";
			int index = 1;
			while ((persona = reader.readLine()) != null) {
				personaMap.put(persona.toLowerCase(), index++);
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

	public int getPersonaId(String persona) {
		Integer id = personaMap.get(persona.toLowerCase());
		if (id == null) {
			return 0;
		}
		return id;
	}

}
