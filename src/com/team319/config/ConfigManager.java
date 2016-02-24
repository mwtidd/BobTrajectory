package com.team319.config;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.team319.waypoint.IWaypointChangeListener;
import com.team319.waypoint.WaypointList;
import com.team319.web.config.server.ConfigServletSocket;

public class ConfigManager {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private static ConfigManager instance = null;

	private List<IConfigChangeListener> listeners;

	private DriveConfig config;

	private ConfigManager(){
		listeners = new ArrayList<IConfigChangeListener>();
		config = new DriveConfig();
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

	public void setConfig(DriveConfig config, ConfigServletSocket source) {
		this.config = config;
		for(IConfigChangeListener listener : listeners){
			listener.onConfigChange(config, source);
		}
	}

}
