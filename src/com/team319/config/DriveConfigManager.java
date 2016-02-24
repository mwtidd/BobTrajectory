package com.team319.config;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.team254.lib.trajectory.TrajectoryGenerator;

public class DriveConfigManager {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private static DriveConfigManager instance = null;

	private List<DriveConfigChangeListener> listeners;

	private TrajectoryGenerator.Config config;


	private DriveConfigManager(){
		listeners = new ArrayList<DriveConfigChangeListener>();

		config = new TrajectoryGenerator.Config();
    	config.dt = .01;
    	config.max_acc = 3;
    	config.max_jerk = 6;
    	config.max_vel = 5;
	}

	public static DriveConfigManager getInstance(){
		if(instance == null){
			instance = new DriveConfigManager();
		}
		return instance;
	}

	public void registerListener(DriveConfigChangeListener listener){
		this.listeners.add(listener);
	}

	public void unregisterListener(DriveConfigChangeListener listener){
		this.listeners.remove(listener);
	}

	public void setDriveConfig(TrajectoryGenerator.Config config){
		this.config = config;
		for(DriveConfigChangeListener listener : listeners){
			listener.onDriveConfigChange(config);
		}
	}

	public TrajectoryGenerator.Config getDriveConfig() {
		return config;
	}

}
