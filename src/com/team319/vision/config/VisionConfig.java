package com.team319.vision.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "__class")
@JsonIgnoreProperties(ignoreUnknown = true)
public class VisionConfig {
	private VisionRgb redLight;
	private VisionRgb blueLight;
	private VisionCamera camera;

	public VisionConfig() {
		redLight = new VisionRgb();
		blueLight = new VisionRgb();
		camera = new VisionCamera();
	}

	public VisionRgb getRedLight() {
		return redLight;
	}

	public void setRedLight(VisionRgb redLight) {
		this.redLight = redLight;
	}

	public VisionRgb getBlueLight() {
		return blueLight;
	}

	public void setBlueLight(VisionRgb blueLight) {
		this.blueLight = blueLight;
	}

	public VisionCamera getCamera() {
		return camera;
	}

	public void setCamera(VisionCamera camera) {
		this.camera = camera;
	}




}
