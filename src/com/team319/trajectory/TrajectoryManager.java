package com.team319.trajectory;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is used both on the robot and the trajectory sever.
 * On the robot is is used to update a listener (in our case the TowerCamera)
 * On the server is is used to update any sockets listening for a new trajectory
 *
 * @author mwtidd
 *
 */
public class TrajectoryManager {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private static TrajectoryManager instance = null;

	private List<TrajectoryChangeListener> listeners;

	private CombinedSrxMotionProfile latestProfile;

	private TrajectoryManager(){
		listeners = new ArrayList<TrajectoryChangeListener>();
		latestProfile = null;
	}

	public static TrajectoryManager getInstance(){
		if(instance == null){
			instance = new TrajectoryManager();
		}
		return instance;
	}

	public void registerListener(TrajectoryChangeListener listener){
		this.listeners.add(listener);
	}

	public void unregisterListener(TrajectoryChangeListener listener){
		this.listeners.remove(listener);
	}

	public void setLatestProfile(CombinedSrxMotionProfile latestProfile){
		//the profile has changed, we should let everyone know
		this.latestProfile = latestProfile;
		for(TrajectoryChangeListener listener : listeners){
			listener.onTrajectoryChange(latestProfile);
		}
	}

}
