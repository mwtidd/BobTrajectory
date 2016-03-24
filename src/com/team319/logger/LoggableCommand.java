package com.team319.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.team319.robot.commands.StatefulCommand;

public class LoggableCommand {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private StatefulCommand command;

	public LoggableCommand(StatefulCommand command){
		this.command = command;
	}

	public String getName(){
		if(command == null){
			logger.error("Command not initialized");
			return null;
		}

		return command.getName();
	}

	public String getCurrentState() {
		if(command == null){
			logger.error("Command not initialized");
			return null;
		}

		return command.getCurrentState();
	}
}
