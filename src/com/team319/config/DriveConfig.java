package com.team319.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.team254.lib.trajectory.TrajectoryGenerator;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "__class")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DriveConfig {

	private double dt = 0.02;
	private double acc = 4.0;
	private double jerk = 4.0;
	private double vel = 4.0;
	private double width = 23.25 / 12;

	private CanTalonConfig leftConfig = new CanTalonConfig();

	private CanTalonConfig rightConfig = new CanTalonConfig();

	public DriveConfig(){

	}

	public void setDt(double dt) {
		this.dt = dt;
	}

	public double getDt() {
		return dt;
	}

	public void setAcc(double acc) {
		this.acc = acc;
	}

	 public double getAcc() {
		return acc;
	}

	public void setJerk(double jerk) {
		this.jerk = jerk;
	}

	public double getJerk() {
		return jerk;
	}

	public void setVel(double vel) {
		this.vel = vel;
	}

	public double getVel() {
		return vel;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double getWidth() {
		return width;
	}

	public TrajectoryGenerator.Config toChezyConfig(){
		TrajectoryGenerator.Config config = new TrajectoryGenerator.Config();
    	config.dt = dt;
    	config.max_acc = acc;
    	config.max_jerk = jerk;
    	config.max_vel = vel;

    	return config;
	}

	public String toJsonString() {
		// TODO Auto-generated method stub
		return null;
	}

	public CanTalonConfig getLeftConfig() {
		return leftConfig;
	}

	public void setLeftConfig(CanTalonConfig leftConfig) {
		this.leftConfig = leftConfig;
	}

	public CanTalonConfig getRightConfig() {
		return rightConfig;
	}

	public void setRightConfig(CanTalonConfig rightConfig) {
		this.rightConfig = rightConfig;
	}



}
