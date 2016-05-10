package com.team319.auto;

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

public class AutoManager {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private static AutoManager instance = null;

	private List<IAutoModesChangeListener> modeListeners;
	private List<IAutoConfigChangeListener> configChangeListers;

	private AutoModes modes;
	private AutoConfig config;

	private boolean isServer = false;

	private AutoManager(){
		modeListeners = new ArrayList<IAutoModesChangeListener>();
		configChangeListers = new ArrayList<IAutoConfigChangeListener>();
		config = new AutoConfig();
		modes = new AutoModes();
	}

	public static AutoManager getInstance(){
		if(instance == null){
			instance = new AutoManager();
		}
		return instance;
	}

	public void registerListener(IAutoConfigChangeListener listener){
		this.configChangeListers.add(listener);
	}

	public void unregisterListener(IAutoConfigChangeListener listener){
		this.configChangeListers.remove(listener);
	}

	public void registerListener(IAutoModesChangeListener listener){
		this.modeListeners.add(listener);
	}

	public void unregisterListener(IAutoModesChangeListener listener){
		this.modeListeners.remove(listener);
	}

	public AutoConfig getConfig() {
		return config;
	}

	public void setConfig(AutoConfig config) {
		this.config = config;
		for(IAutoConfigChangeListener listener: configChangeListers){
			listener.onChange(config);
		}
		if(isServer){
			saveConfig();
		}
	}


	public void setModes(AutoModes autoModes) {
		this.modes = autoModes;
		for(IAutoModesChangeListener listener: modeListeners){
			listener.onChange(autoModes);
		}
		if(isServer){
			saveModes();
		}
	}

	public AutoModes getModes() {
		return modes;
	}

	public boolean isOnRed(){
		return config.getSelectedAlliance().equalsIgnoreCase("red");
	}

	public void throwAutoConfigException(AutoConfigException exception){
		for(IAutoConfigChangeListener listener: configChangeListers){
			listener.onConfigException(exception);
		}
	}


	private void saveConfig() {

		try{
			File file = new File("config/autoConfig.json");

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


	private void saveModes() {

		try{
			File file = new File("config/autoModes.json");

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			ObjectMapper mapper = new ObjectMapper();
			try {
				String json = mapper.writeValueAsString(modes);
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
		this.isServer = true;
		try{
			BufferedReader br = new BufferedReader(new FileReader("config/autoConfig.json"));
		    StringBuilder sb = new StringBuilder();
		    String line = br.readLine();

		    while (line != null) {
		        sb.append(line);
		        sb.append(System.lineSeparator());
		        line = br.readLine();
		    }
		    String everything = sb.toString();

			ObjectMapper mapper = new ObjectMapper();

			AutoConfig config = mapper.readValue(everything, AutoConfig.class);

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
		} finally {

		}

		try{
			BufferedReader br = new BufferedReader(new FileReader("config/autoModes.json"));
		    StringBuilder sb = new StringBuilder();
		    String line = br.readLine();

		    while (line != null) {
		        sb.append(line);
		        sb.append(System.lineSeparator());
		        line = br.readLine();
		    }
		    String everything = sb.toString();

			ObjectMapper mapper = new ObjectMapper();

			AutoModes modes = mapper.readValue(everything, AutoModes.class);

			this.setModes(modes);

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
