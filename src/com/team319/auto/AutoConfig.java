package com.team319.auto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "__class")
@JsonIgnoreProperties(ignoreUnknown = true)
public class AutoConfig {
	private SelectedAuto selectedAuto;
	private String selectedAlliance;

	public String __class = AutoConfig.class.getName();


	public AutoConfig() {
		selectedAuto = new SelectedAuto();
		selectedAlliance = "red";
	}

	public SelectedAuto getSelectedAuto() {
		return selectedAuto;
	}

	public void setSelectedAuto(SelectedAuto selectedAuto) {
		this.selectedAuto = selectedAuto;
	}

	public String getSelectedAlliance() {
		return selectedAlliance;
	}

	public void setSelectedAlliance(String selectedAlliance) {
		this.selectedAlliance = selectedAlliance;
	}



}
