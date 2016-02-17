package com.team319.trajectory;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WaypointManager {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private static WaypointManager instance = null;

	private List<IWaypointChangeListener> listeners;

	private List<Waypoint> waypoints;

	private WaypointManager(){
		listeners = new ArrayList<IWaypointChangeListener>();
		waypoints = new ArrayList<Waypoint>();
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

	public void setWaypoints(List<Waypoint> waypoints){
		this.waypoints = waypoints;
		for(IWaypointChangeListener listener : listeners){
			listener.onWaypointChange(new WaypointList(waypoints));
		}
	}

	public void setWaypoints(WaypointList waypointList){
		this.waypoints = waypointList.getWaypoints();
		for(IWaypointChangeListener listener : listeners){
			listener.onWaypointChange(waypointList);
		}
	}

}
