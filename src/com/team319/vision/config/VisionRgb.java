package com.team319.vision.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "__class")
@JsonIgnoreProperties(ignoreUnknown = true)
public class VisionRgb {
	private double redMin = 0;
	private double redMax = 255;
	private double blueMin = 0;
	private double blueMax = 255;
	private double greenMin = 0;
	private double greenMax = 255;

	public VisionRgb(){

	}


	public double getRedMin() {
		return redMin;
	}

	public void setRedMin(double redMin) {
		this.redMin = redMin;
	}

	public double getRedMax() {
		return redMax;
	}

	public void setRedMax(double redMax) {
		this.redMax = redMax;
	}

	public double getBlueMin() {
		return blueMin;
	}

	public void setBlueMin(double blueMin) {
		this.blueMin = blueMin;
	}

	public double getBlueMax() {
		return blueMax;
	}

	public void setBlueMax(double blueMax) {
		this.blueMax = blueMax;
	}

	public double getGreenMin() {
		return greenMin;
	}

	public void setGreenMin(double greenMin) {
		this.greenMin = greenMin;
	}

	public double getGreenMax() {
		return greenMax;
	}

	public void setGreenMax(double greenMax) {
		this.greenMax = greenMax;
	}
}
