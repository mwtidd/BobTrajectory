package com.team319.config;

import com.team254.lib.trajectory.TrajectoryGenerator;

public class DriveConfig {

	private double dt = 0.02;
	private double acc = 4.0;
	private double jerk = 4.0;
	private double vel = 4.0;
	private double width = 23.25 / 12;

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

}
