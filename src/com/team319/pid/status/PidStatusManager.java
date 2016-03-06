package com.team319.pid.status;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.team254.lib.trajectory.TrajectoryGenerator;

public class PidStatusManager {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private static PidStatusManager instance = null;

	private List<IPidStatusChangeListener> listeners;

	private PidStatusManager(){
		listeners = new ArrayList<IPidStatusChangeListener>();

	}

	public static PidStatusManager getInstance(){
		if(instance == null){
			instance = new PidStatusManager();
		}
		return instance;
	}

	public void registerListener(IPidStatusChangeListener listener){
		this.listeners.add(listener);
	}

	public void unregisterListener(IPidStatusChangeListener listener){
		this.listeners.remove(listener);
	}

	public void setStatus(PidStatus status){
		for(IPidStatusChangeListener listener : listeners){
			listener.onPidStatusChange(status);
		}
	}

}
