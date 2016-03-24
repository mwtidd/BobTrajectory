package com.team319.logger;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.wpi.first.wpilibj.Encoder;

public class LoggableEncoder extends LoggableSensor{

	private Logger logger = LoggerFactory.getLogger(this.getClass());



	private Encoder encoder;

	public LoggableEncoder(String name, Encoder encoder){
		super(name);
		this.encoder = encoder;
	}

	@Override
	public Map<String, Object> getProperties() {
		Map<String, Object> properties = new HashMap<String, Object>();
		if(encoder == null){
			logger.error("Encoder not initialized.");
			properties.put("position", 0d);
			properties.put("speed", 0d);
		}else{
			properties.put("position", encoder.getDistance());
			properties.put("speed", encoder.getRate());
		}
		return properties;
	}
}
