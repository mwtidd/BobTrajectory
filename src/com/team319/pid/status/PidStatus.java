package com.team319.pid.status;

public class PidStatus {
	private long time;
	private double error;
	private double output;
	private double setpoint;
	private double position;

	public PidStatus(){

	}

	public void setTime(long time) {
		this.time = time;
	}

	public long getTime() {
		return time;
	}

	public void setError(double error) {
		this.error = error;
	}

	public void setOutput(double output) {
		this.output = output;
	}

	public void setPosition(double position) {
		this.position = position;
	}

	public void setSetpoint(double setpoint) {
		this.setpoint = setpoint;
	}

	public double getError() {
		return error;
	}

	public double getOutput() {
		return output;
	}

	public double getSetpoint() {
		return setpoint;
	}

	public double getPosition() {
		return position;
	}
}
