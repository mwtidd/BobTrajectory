package com.team319.trajectory.progress;

import java.util.ArrayList;
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
public class TrajectoryProgressManager {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private static TrajectoryProgressManager instance = null;

	private List<ITrajectoryProgressChangeListener> listeners;

	private CombinedTrajectoryProgress latestProgress;

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
