package com.team319;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.team319.trajectory.PathManager;
import com.team319.trajectory.Waypoint;
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

			List<Waypoint> waypoints = new ArrayList<Waypoint>();
			waypoints.add(new Waypoint(0,0,0));
			waypoints.add(new Waypoint(9,3,0));

			PathManager.getInstance().setWaypoints(waypoints);
		} catch (Exception e) {
			logger.error("Error Starting Trajectory Client");
		}
	}

}
