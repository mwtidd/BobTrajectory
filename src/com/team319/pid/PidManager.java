package com.team319.pid;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PidManager {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private static PidManager instance = null;

	private List<IPidChangeListener> listeners;

	private Pid pid = null;

	private PidManager(){
		listeners = new ArrayList<IPidChangeListener>();

	}

	public static PidManager getInstance(){
		if(instance == null){
			instance = new PidManager();
		}
		return instance;
	}

	public void registerListener(IPidChangeListener listener){
		this.listeners.add(listener);
	}

	public void unregisterListener(IPidChangeListener listener){
		this.listeners.remove(listener);
	}

	public void setPid(Pid pid){
		this.pid = pid;
		for(IPidChangeListener listener : listeners){
			listener.onPidChange(pid);
		}
	}

	public Pid getPid() {
		return pid;
	}

}
