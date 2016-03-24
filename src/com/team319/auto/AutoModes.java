package com.team319.auto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "__class")
@JsonIgnoreProperties(ignoreUnknown = true)
public class AutoModes {
	private List<String> modes;
	private List<String> positions;
	private List<String> defenses;

	public AutoModes(){
		this.modes = new ArrayList<String>();
		this.positions = new ArrayList<String>();
		this.defenses = new ArrayList<String>();
	}

	public List<String> getModes() {
		return modes;
	}

	public void setModes(List<String> modes) {
		this.modes = modes;
	}

	public List<String> getPositions() {
		return positions;
	}

	public void setPositions(List<String> positions) {
		this.positions = positions;
	}

	public List<String> getDefenses() {
		return defenses;
	}

	public void setDefenses(List<String> defenses) {
		this.defenses = defenses;
	}
}
