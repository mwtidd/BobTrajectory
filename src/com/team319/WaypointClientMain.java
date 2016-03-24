package com.team319;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.team319.web.waypoint.client.WaypointClient;

/**
 * A basic way to fire up a trajectory client.
 *
 * It's meant mostly for testing purposes
 *
 * @author mwtidd
 *
 */
public class WaypointClientMain {

	private static Logger logger = LoggerFactory.getLogger(WaypointClientMain.class);

	public static void main(String[] args) {

		logger.info("Starting Waypoint Client");

		try {
			WaypointClient.start("localhost");
		} catch (Exception e) {
			logger.error("Error Starting Waypoint Client");
		}
	}

}
