package com.team319.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ConfigManager {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private static ConfigManager instance = null;

	private List<IConfigChangeListener> listeners;

	private DriveConfig config;

	private ConfigManager(){
		listeners = new ArrayList<IConfigChangeListener>();
		config = new DriveConfig();
		recall();
	}

	public static ConfigManager getInstance(){
		if(instance == null){
			instance = new ConfigManager();
		}
		return instance;
	}

	public void registerListener(IConfigChangeListener listener){
		this.listeners.add(listener);
	}

	public void unregisterListener(IConfigChangeListener listener){
		this.listeners.remove(listener);
	}

	public DriveConfig getConfig() {
		return config;
	}

	public void setConfig(DriveConfig config) {
		this.config = config;
		for(IConfigChangeListener listener : listeners){
			listener.onConfigChange(config);
		}
		save();
	}

	private void save() {

		try{
			File file = new File("config/driveConfig.json");

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			ObjectMapper mapper = new ObjectMapper();
			try {
				String json = mapper.writeValueAsString(config);
				bw.write(json);
			} catch (JsonProcessingException e) {
				logger.error("Parse Error");
			}

			bw.close();
		}catch (IOException e) {
			logger.error("IO Error");
		}


	}

	public void recall(){
		try{
			BufferedReader br = new BufferedReader(new FileReader("config/driveConfig.json"));
		    StringBuilder sb = new StringBuilder();
		    String line = br.readLine();

		    while (line != null) {
		        sb.append(line);
		        sb.append(System.lineSeparator());
		        line = br.readLine();
		    }
		    String everything = sb.toString();

			ObjectMapper mapper = new ObjectMapper();

			DriveConfig config = mapper.readValue(everything, DriveConfig.class);

			this.setConfig(config);

			br.close();

		}catch (FileNotFoundException e) {
			logger.error("File Error");
		} catch (JsonParseException e) {
			logger.error("Parse Error");
		} catch (JsonMappingException e) {
			logger.error("Mapping Error");
		} catch (IOException e) {
			logger.error("IO Error");
		}
	}

}
