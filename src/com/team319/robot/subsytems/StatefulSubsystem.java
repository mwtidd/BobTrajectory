package com.team319.robot.subsytems;

import java.util.List;
import java.util.Map;

import com.team319.logger.LoggableSensor;
import com.team319.logger.RobotLogger;

import edu.wpi.first.wpilibj.command.Subsystem;

public abstract class StatefulSubsystem extends Subsystem{

	public StatefulSubsystem(){
		super();
		RobotLogger.getInstance().registerSubsystem(this);
	}

	public StatefulSubsystem(String name){
		super(name);
		RobotLogger.getInstance().registerSubsystem(this);
	}

	public abstract Map<String,Object> getCustomProperties();

	public abstract List<LoggableSensor> getSensors();
}
