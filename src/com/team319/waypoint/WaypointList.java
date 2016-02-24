package com.team319.waypoint;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team254.lib.trajectory.WaypointSequence;
import com.team319.trajectory.TrajectoryManager;

public class WaypointList {

	private boolean cachable = false;

	private List<Waypoint> waypoints;

	public WaypointList(){
		this.waypoints = new ArrayList<Waypoint>();
	}

	public WaypointList(List<Waypoint> waypoints){
		this.waypoints = waypoints;
	}

	public List<Waypoint> getWaypoints() {
		return waypoints;
	}

	public void setCachable(boolean cachable) {
		this.cachable = cachable;
	}

	public boolean isCachable() {
		return cachable;
	}

	public WaypointSequence toWaypointSequence(){
		WaypointSequence sequence = new WaypointSequence(waypoints.size());
		for(Waypoint waypoint : waypoints){
			//convert theta to radians
			double theta = ((Math.PI * waypoint.getTheta())/180);
			sequence.addWaypoint(new com.team254.lib.trajectory.WaypointSequence.Waypoint(waypoint.getX(),waypoint.getY(),theta));
		}
		return sequence;

	}

}
