package com.team319.waypoint;

import java.util.List;

import com.team319.web.waypoint.server.WaypointServletSocket;


public interface IWaypointChangeListener {
	public void onWaypointChange(WaypointList waypoints);
}
