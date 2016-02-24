package com.team319.waypoint;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.team319.web.waypoint.server.WaypointServletSocket;

public class WaypointManager {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private static WaypointManager instance = null;

	private List<IWaypointChangeListener> listeners;

	private WaypointList waypointList;

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
	}

}
