package com.team319.logger;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.team319.robot.commands.StatefulCommand;
import com.team319.robot.subsytems.StatefulSubsystem;

import edu.wpi.first.wpilibj.command.Command;

@JsonPropertyOrder({"name", "command", "sensors","properties"})
public class LoggableSubsystem extends Loggable{

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private List<LoggableSensor> sensors = new ArrayList<LoggableSensor>();

	private LoggableCommand command;

	private String name = null;

	public LoggableSubsystem(StatefulSubsystem subsystem) {
		if(subsystem == null){
			logger.error("Subsystem was not initialized.");
		}else{
			this.name = subsystem.getName();
			this.sensors = subsystem.getSensors();
			this.setProperties(subsystem.getCustomProperties());
			Command command = subsystem.getCurrentCommand();
			if(command == null){
				logger.error("Current command is null.");
			}else if(command instanceof StatefulCommand){
				this.command = new LoggableCommand((StatefulCommand) command);
			}else{
				logger.error("Current command is not stateful.");
			}

		}
	}

	public String getName() {
		return name;
	}

	public List<LoggableSensor> getSensors() {
		return sensors;
	}

	public LoggableCommand getCommand() {
		return command;
	}

}
