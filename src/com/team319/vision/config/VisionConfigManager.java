package com.team319.vision.config;

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

public class VisionConfigManager {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private VisionConfig config;

	private static VisionConfigManager instance = null;

	private List<IVisionConfigChangeListener> listeners;

	private VisionConfigManager(){
		listeners = new ArrayList<IVisionConfigChangeListener>();
		config = new VisionConfig();
		recall();
	}

	public static VisionConfigManager getInstance(){
		if(instance == null){
			instance = new VisionConfigManager();

		}

		return instance;
	}

	public void registerListener(IVisionConfigChangeListener listener){
		this.listeners.add(listener);
	}

	public void unregisterListener(IVisionConfigChangeListener listener){
		this.listeners.remove(listener);
	}

	public void setConfig(VisionConfig config) {
		this.config = config;
		for(IVisionConfigChangeListener listener: listeners){
			listener.onChange(config);
		}
		save();
	}

	public VisionConfig getConfig() {
		return config;
	}

	private void save() {

		try{
			File file = new File("config/visionConfig.json");

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
			BufferedReader br = new BufferedReader(new FileReader("config/visionConfig.json"));
		    StringBuilder sb = new StringBuilder();
		    String line = br.readLine();

		    while (line != null) {
		        sb.append(line);
		        sb.append(System.lineSeparator());
		        line = br.readLine();
		    }
		    String everything = sb.toString();

			ObjectMapper mapper = new ObjectMapper();

			VisionConfig config = mapper.readValue(everything, VisionConfig.class);

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
