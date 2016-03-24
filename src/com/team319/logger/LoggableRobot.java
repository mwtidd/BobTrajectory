package com.team319.logger;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"time","properties","subsystems"})
public class LoggableRobot extends Loggable{

	long time = 0;

	public LoggableRobot(){
		time = System.currentTimeMillis();
	}

	public long getTime() {
		return time;
	}

	List<LoggableSubsystem> subsystems = new ArrayList<LoggableSubsystem>();

	public void addSubsystem(LoggableSubsystem subsystem){
		this.subsystems.add(subsystem);
	}

	public List<LoggableSubsystem> getSubsystems() {
		return subsystems;
	}
}
