package com.team319.logger;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kauailabs.navx.frc.AHRS;

public class LoggableImu extends LoggableSensor{

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private AHRS ahrs;

	public LoggableImu(AHRS ahrs){
		super("IMU");
		this.ahrs = ahrs;
	}

	@Override
	public Map<String, Object> getProperties() {
		Map<String, Object> properties = new HashMap<String, Object>();
		if(ahrs == null){
			logger.error("Imu not initialized.");
			properties.put("connected", false);
			properties.put("compassHeading", 0d);
			properties.put("xAcc", 0d);
			properties.put("yAcc", 0d);
			properties.put("isMoving", false);
			properties.put("isRotating", false);
			properties.put("xVel", 0d);
			properties.put("yVel", 0d);
			properties.put("xDisp", 0d);
			properties.put("yDisp", 0d);
		}else{
			properties.put("connected", ahrs.isConnected());
			properties.put("compassHeading", ahrs.getCompassHeading());
			properties.put("xAcc", ahrs.getWorldLinearAccelX());
			properties.put("yAcc", ahrs.getWorldLinearAccelY());
			properties.put("zAcc", ahrs.getWorldLinearAccelZ());
			properties.put("isMoving", ahrs.isMoving());
			properties.put("isRotating", ahrs.isRotating());
			properties.put("xVel", ahrs.getVelocityX());
			properties.put("yVel", ahrs.getVelocityY());
			properties.put("xDisp", ahrs.getDisplacementX());
			properties.put("yDisp", ahrs.getDisplacementY());
		}
		return properties;
	}
}
