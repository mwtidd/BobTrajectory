package com.team319;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.team319.trajectory.TrajectoryManager;
import com.team319.web.trajectory.server.TrajectoryServer;
import com.team319.web.waypoint.client.WaypointClient;

/**
 * A basic way to fire up a trajectory server.
 *
 * This is expected on run on the driver station or a kangaroo
 *
 * The robot will need to know the ip in order to connect to it
 *
 * @author mwtidd
 *
 */
public class TrajectoryServerMain {

	private static Logger logger = LoggerFactory.getLogger(TrajectoryServerMain.class);

	public static void main(String[] args) {

		logger.info("Starting Trajectory Server");

		try {
			TrajectoryServer.startServer();
		} catch (Exception e) {
			logger.error("Error Starting Trajectory Client");
		}
	}

}
