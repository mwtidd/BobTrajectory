package com.team319.waypoint;

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

public class WaypointManager {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private static WaypointManager instance = null;

	private List<IWaypointChangeListener> listeners;

	private WaypointList waypointList;

	private boolean isServer = false;

	private WaypointManager(){
		listeners = new ArrayList<IWaypointChangeListener>();
		waypointList = new WaypointList();
	}

	public static WaypointManager getInstance(){
		if(instance == null){
			instance = new WaypointManager();
		}
		return instance;
	}

	public void registerListener(IWaypointChangeListener listener){
		this.listeners.add(listener);
	}

	public void unregisterListener(IWaypointChangeListener listener){
		this.listeners.remove(listener);
	}

	public WaypointList getWaypointList() {
		return waypointList;
	}

	public void setWaypointList(WaypointList waypointList) {
		this.waypointList = waypointList;
		for(IWaypointChangeListener listener : listeners){
			listener.onWaypointChange(waypointList);
		}
		if(isServer){
			save();
		}

	}

	public void save() {

		try{
			File file = new File("config/waypoints.json");

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			ObjectMapper mapper = new ObjectMapper();
			try {
				String json = mapper.writeValueAsString(waypointList);
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
			BufferedReader br = new BufferedReader(new FileReader("config/waypoints.json"));
		    StringBuilder sb = new StringBuilder();
		    String line = br.readLine();

		    while (line != null) {
		        sb.append(line);
		        sb.append(System.lineSeparator());
		        line = br.readLine();
		    }
		    String everything = sb.toString();

			ObjectMapper mapper = new ObjectMapper();

			WaypointList waypointList = mapper.readValue(everything, WaypointList.class);

			this.setWaypointList(waypointList);
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
