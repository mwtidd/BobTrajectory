package com.team319.trajectory.progress;

import com.team319.robot.motion.OtfMotionProfile;

public class TrajectoryProgress {
	private double elapsedTime;
	private double targetVelocity;
	private double actualVelocity;
	private double targetDistance;
	private double actualDistance;

	public TrajectoryProgress(){

	}

	public TrajectoryProgress(OtfMotionProfile profile){
		this.elapsedTime = profile.getElapsedTime();
		this.targetVelocity = profile.getTargetVelocity();
		this.targetDistance = profile.getTargetDistance();
		this.actualVelocity = profile.getActualVelocity();
		this.actualDistance = profile.getActualDistance();
	}

	public double getElapsedTime() {
		return elapsedTime;
	}

	public void setElapsedTime(double elapsedTime) {
		this.elapsedTime = elapsedTime;
	}

	public double getTargetVelocity() {
		return targetVelocity;
	}

	public void setTargetVelocity(double targetVelocity) {
		this.targetVelocity = targetVelocity;
	}

	public double getActualVelocity() {
		return actualVelocity;
	}

	public void setActualVelocity(double actualVelocity) {
		this.actualVelocity = actualVelocity;
	}

	public double getTargetDistance() {
		return targetDistance;
	}

	public void setTargetDistance(double targetDistance) {
		this.targetDistance = targetDistance;
	}

	public double getActualDistance() {
		return actualDistance;
	}

	public void setActualDistance(double actualDistance) {
		this.actualDistance = actualDistance;
	}
}

