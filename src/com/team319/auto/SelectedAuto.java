package com.team319.auto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "__class")
@JsonIgnoreProperties(ignoreUnknown = true)
public class SelectedAuto {
	private String selectedMode = null;
	private String selectedPosition = null;
	private String selectedDefense = null;

	public String __class = SelectedAuto.class.getName();


	public SelectedAuto(){

	}

	public SelectedAuto(String selectedPosition, String selectedDefense, String selectedMode){
		this.selectedPosition = selectedPosition;
		this.selectedMode = selectedMode;
		this.selectedDefense = selectedDefense;
	}

	public void setSelectedMode(String selectedMode) {
		this.selectedMode = selectedMode;
	}

	public void setSelectedPosition(String selectedPosition) {
		this.selectedPosition = selectedPosition;
	}

	public String getSelectedMode() {
		return selectedMode;
	}

	public String getSelectedPosition() {
		return selectedPosition;
	}

	public String getSelectedDefense() {
		return selectedDefense;
	}

	public void setSelectedDefense(String selectedDefense) {
		this.selectedDefense = selectedDefense;
	}

	public boolean isMode(String mode){
		return selectedMode.equalsIgnoreCase(mode);
	}

	public boolean isPosition(String position){
		return selectedPosition.equalsIgnoreCase(position);
	}

	public boolean isDefense(String defense){
		return selectedDefense.equalsIgnoreCase(defense);
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof SelectedAuto){
			SelectedAuto selectedAuto = (SelectedAuto) obj;
			if(isPosition(selectedAuto.getSelectedPosition()) &&
					isMode(selectedAuto.getSelectedMode()) &&
						isDefense(selectedAuto.getSelectedDefense())){
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return hashString(selectedPosition + selectedDefense + selectedMode);
	}

	private int hashString(String string){
		int hash = 7;
		for (int i = 0; i < string.length(); i++) {
		    hash = hash*31 + string.charAt(i);
		}
		return hash;
	}
}
