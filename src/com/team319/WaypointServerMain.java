package com.team319;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.team319.web.waypoint.server.WaypointServer;

/**
 * A basic way to fire up a waypoint server.
 *
 * It is mostly meant for testing purposes
 *
 * @author mwtidd
 *
 */
public class WaypointServerMain {

	private static Logger logger = LoggerFactory.getLogger(WaypointServerMain.class);

	public static void main(String[] args) {

		logger.info("Starting the Waypoint Server");

		WaypointServer.startServer();
	}

}
