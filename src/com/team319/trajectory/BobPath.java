package com.team319.trajectory;

import java.util.ArrayList;
import java.util.List;

import com.team254.lib.trajectory.WaypointSequence;

public class BobPath {
	private List<Waypoint> waypoints;

	public BobPath(){
		this.waypoints = new ArrayList<Waypoint>();
	}

	public BobPath(List<Waypoint> waypoints){
		this.waypoints = waypoints;
	}

	public List<Waypoint> getWaypoints() {
		return waypoints;
	}

	public WaypointSequence toWaypointSequence(){
		WaypointSequence sequence = new WaypointSequence(waypoints.size());
		for(Waypoint waypoint : waypoints){
			sequence.addWaypoint(new com.team254.lib.trajectory.WaypointSequence.Waypoint(waypoint.getX(),waypoint.getY(),waypoint.getTheta()));
		}
		return sequence;

	}
}
