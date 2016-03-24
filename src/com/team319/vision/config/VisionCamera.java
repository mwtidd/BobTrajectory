package com.team319.vision.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "__class")
@JsonIgnoreProperties(ignoreUnknown = true)
public class VisionCamera {
	private int number = 0;
	private int brightness = 128;
	private int contrast = 128;
	private int saturation = 128;
	private int gain = 0;
	private int sharpness = 128;

	private int autoExposure = 1;
	private int exposure = 250;

	private boolean autoFocus = true;
	private int focus = 0;

	private boolean filterImage = false;

	public VisionCamera(){

	}

	public void setExposure(int exposure) {
		this.exposure = exposure;
	}

	public int getExposure() {
		return exposure;
	}

	public void setBrightness(int brightness) {
		this.brightness = brightness;
	}

	public int getBrightness() {
		return brightness;
	}

	public int getContrast() {
		return contrast;
	}

	public void setContrast(int contrast) {
		this.contrast = contrast;
	}

	public int getGain() {
		return gain;
	}
	public void setGain(int gain) {
		this.gain = gain;
	}

	public int getSaturation() {
		return saturation;
	}

	public void setSaturation(int saturation) {
		this.saturation = saturation;
	}

	public int getSharpness() {
		return sharpness;
	}

	public void setSharpness(int sharpness) {
		this.sharpness = sharpness;
	}

	public int getAutoExposure() {
		return autoExposure;
	}

	public void setAutoExposure(int autoExposure) {
		this.autoExposure = autoExposure;
	}

	public boolean isAutoFocus() {
		return autoFocus;
	}

	public void setAutoFocus(boolean autoFocus) {
		this.autoFocus = autoFocus;
	}


	public int getFocus() {
		return focus;
	}

	public void setFocus(int focus) {
		this.focus = focus;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public void setFilterImage(boolean filterImage) {
		this.filterImage = filterImage;
	}

	public boolean isFilterImage() {
		return filterImage;
	}


}
