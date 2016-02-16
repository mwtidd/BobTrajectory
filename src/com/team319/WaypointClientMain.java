package com.team319;


import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.team319.trajectory.PathManager;
import com.team319.trajectory.Waypoint;
import com.team319.web.trajectory.client.TrajectoryClient;

/**
 * A basic way to fire up a waypoint client.
 *
 * It is meant to be run on a Kangraoo or the Driver Station
 *
 * @author mwtidd
 *
 */
public class WaypointClientMain {

	private static Logger logger = LoggerFactory.getLogger(WaypointClientMain.class);

	public static void main(String[] args) {

		logger.info("Starting Trajectory Client");

		try {
			TrajectoryClient.start("319");

			Thread.sleep(2000);

			List<Waypoint> waypoints = new ArrayList<Waypoint>();
			waypoints.add(new Waypoint(0,0,0));
			waypoints.add(new Waypoint(15,0,0));

			PathManager.getInstance().setWaypoints(waypoints);
		} catch (Exception e) {
			logger.error("Error Starting Trajectory Client");
		}
	}

}
