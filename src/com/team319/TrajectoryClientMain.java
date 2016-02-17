package com.team319;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.team319.waypoint.Waypoint;
import com.team319.waypoint.WaypointManager;
import com.team319.web.trajectory.client.TrajectoryClient;

/**
 * A basic way to fire up a trajectory client.
 *
 * It's meant mostly for testing purposes
 *
 * @author mwtidd
 *
 */
public class TrajectoryClientMain {

	private static Logger logger = LoggerFactory.getLogger(TrajectoryClientMain.class);

	public static void main(String[] args) {

		logger.info("Starting Trajectory Client");

		try {
			TrajectoryClient.start("localhost");

			Thread.sleep(2000);

			List<Waypoint> waypoints = new ArrayList<Waypoint>();
			waypoints.add(new Waypoint(0,0,0));
			waypoints.add(new Waypoint(15,0,0));

			WaypointManager.getInstance().setWaypoints(waypoints);
		} catch (Exception e) {
			logger.error("Error Starting Trajectory Client");
		}
	}

}
