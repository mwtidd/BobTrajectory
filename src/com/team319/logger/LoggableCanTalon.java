package com.team319.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.wpi.first.wpilibj.CANTalon;

public class LoggableCanTalon extends LoggableSensor{

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	public LoggableCanTalon(String name, CANTalon talon){
		super(name);

		if(talon == null){
			logger.error("Talon not initialized.");
		}else{
			this.putProperty("position", talon.getPosition());
			this.putProperty("speed", talon.getSpeed());
		}

	}
}
