package com.team319.config;

public class CanTalonConfig {

	private int encoderCodesPerRev = 1024;
	private boolean reverseSensor = true;
	private boolean reverseOutput = true;
	private double p = 0.0;
	private double i = 0.0;
	private double d = 0.0;
	private double f = 0.0;

	public CanTalonConfig(){

	}

	public int getEncoderCodesPerRev() {
		return encoderCodesPerRev;
	}

	public void setEncoderCodesPerRev(int encoderCodesPerRev) {
		this.encoderCodesPerRev = encoderCodesPerRev;
	}

	public boolean isReverseSensor() {
		return reverseSensor;
	}

	public void setReverseSensor(boolean reverseSensor) {
		this.reverseSensor = reverseSensor;
	}

	public boolean isReverseOutput() {
		return reverseOutput;
	}

	public void setReverseOutput(boolean reverseOutput) {
		this.reverseOutput = reverseOutput;
	}

	public double getP() {
		return p;
	}

	public void setP(double p) {
		this.p = p;
	}

	public double getI() {
		return i;
	}

	public void setI(double i) {
		this.i = i;
	}

	public double getD() {
		return d;
	}

	public void setD(double d) {
		this.d = d;
	}

	public double getF() {
		return f;
	}

	public void setF(double f) {
		this.f = f;
	}






}
