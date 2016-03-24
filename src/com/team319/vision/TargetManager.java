package com.team319.vision;

import java.util.ArrayList;
import java.util.List;

public class TargetManager {

	private Target target;

	private static TargetManager instance = null;

	private List<ITargetListener> listeners;

	private TargetManager(){
		listeners = new ArrayList<ITargetListener>();
	}

	public static TargetManager getInstance(){
		if(instance == null){
			instance = new TargetManager();

		}

		return instance;
	}

	public void registerListener(ITargetListener listener){
		this.listeners.add(listener);
	}

	public void unregisterListener(ITargetListener listener){
		this.listeners.remove(listener);
	}

	public void setTarget(Target target) {
		this.target = target;
		for(ITargetListener listener: listeners){
			listener.onTargetChange(target);
		}
	}

	public Target getTarget() {
		return target;
	}



}
