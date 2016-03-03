package com.team319.trajectory.progress;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team254.lib.trajectory.PathGenerator;
import com.team254.lib.trajectory.WaypointSequence;
import com.team319.HistoryBundle;
import com.team319.config.ConfigManager;
import com.team319.config.DriveConfig;
import com.team319.trajectory.CombinedSrxMotionProfile;


/**
 * This is used both on the robot and the trajectory sever.
 * On the robot is is used to update a listener (in our case the TowerCamera)
 * On the server is is used to update any sockets listening for a new trajectory
 *
 * @author mwtidd
 *
 */
public class TrajectoryProgressManager {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private static TrajectoryProgressManager instance = null;

	private List<ITrajectoryProgressChangeListener> listeners;

	private CombinedTrajectoryProgress latestProgress;

	private static final String PATH_NAME = "Path";

	private TrajectoryProgressManager(){
		listeners = new ArrayList<ITrajectoryProgressChangeListener>();
		latestProgress = null;
	}

	public static TrajectoryProgressManager getInstance(){
		if(instance == null){
			instance = new TrajectoryProgressManager();
		}
		return instance;
	}

	public void registerListener(ITrajectoryProgressChangeListener listener){
		this.listeners.add(listener);
	}

	public void unregisterListener(ITrajectoryProgressChangeListener listener){
		this.listeners.remove(listener);
	}

	public synchronized List<ITrajectoryProgressChangeListener> getListeners() {
		return listeners;
	}

	public synchronized void setProgress(CombinedTrajectoryProgress progress){
		//the progress has changed, we should let everyone know
		this.latestProgress = progress;
		for(ITrajectoryProgressChangeListener listener : getListeners()){
			listener.onProgressChange(latestProgress);
		}
	}

	public CombinedTrajectoryProgress getLatestProgress() {
		return latestProgress;
	}

}
