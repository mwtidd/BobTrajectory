package com.team319.trajectory;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PathManager {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private static PathManager instance = null;

	private List<PathChangeListener> listeners;

	private List<Waypoint> waypoints;

	private PathManager(){
		listeners = new ArrayList<PathChangeListener>();
		waypoints = new ArrayList<Waypoint>();
	}

	public static PathManager getInstance(){
		if(instance == null){
			instance = new PathManager();
		}
		return instance;
	}

	public void registerListener(PathChangeListener listener){
		this.listeners.add(listener);
	}

	public void unregisterListener(PathChangeListener listener){
		this.listeners.remove(listener);
	}

	public void setWaypoints(List<Waypoint> waypoints){
		this.waypoints = waypoints;
		for(PathChangeListener listener : listeners){
			listener.onPathChange(new BobPath(waypoints));
		}
	}

}
