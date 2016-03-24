package com.team319.logger;

import java.util.ArrayList;
import java.util.List;

public class RobotLogManager {

	private LoggableRobot robot;

	private static RobotLogManager instance = null;

	private List<IRobotLogListener> listeners;

	private RobotLogManager(){
		listeners = new ArrayList<IRobotLogListener>();
	}

	public static RobotLogManager getInstance(){
		if(instance == null){
			instance = new RobotLogManager();

		}

		return instance;
	}

	public void registerListener(IRobotLogListener listener){
		this.listeners.add(listener);
	}

	public void unregisterListener(IRobotLogListener listener){
		this.listeners.remove(listener);
	}

	public void setRobot(LoggableRobot robot) {
		this.robot = robot;
		for(IRobotLogListener listener: listeners){
			listener.onChange(robot);
		}
	}

	public LoggableRobot getRobot() {
		return robot;
	}



}
