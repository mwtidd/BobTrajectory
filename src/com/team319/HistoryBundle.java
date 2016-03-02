package com.team319;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team319.config.DriveConfig;
import com.team319.waypoint.WaypointList;

public class HistoryBundle {
	private WaypointList waypointList;
	private DriveConfig driveConfig;

	public HistoryBundle(){

	}

	public HistoryBundle(DriveConfig driveConfig, WaypointList waypointList){
		this.driveConfig = driveConfig;
		this.waypointList = waypointList;
	}

	public void setWaypointList(WaypointList waypointList) {
		this.waypointList = waypointList;
	}

	public WaypointList getWaypointList() {
		return waypointList;
	}

	public void setDriveConfig(DriveConfig driveConfig) {
		this.driveConfig = driveConfig;
	}

	public DriveConfig getDriveConfig() {
		return driveConfig;
	}

	public String toJsonString() throws JsonProcessingException{
		ObjectMapper mapper = new ObjectMapper();
		String combinedJson = mapper.writeValueAsString(this);
		return combinedJson;
	}
}
