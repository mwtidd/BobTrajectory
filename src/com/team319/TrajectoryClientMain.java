package com.team319;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team319.trajectory.CombinedSrxMotionProfile;
import com.team319.trajectory.ITrajectoryChangeListener;
import com.team319.trajectory.TrajectoryManager;
import com.team319.waypoint.Waypoint;
import com.team319.waypoint.WaypointList;
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
public class TrajectoryClientMain{

	private static Logger logger = LoggerFactory.getLogger(TrajectoryClientMain.class);

	public static void main(String[] args) {

		logger.info("Starting Trajectory Client");

		try {
			TrajectoryClient.start("localhost");

		} catch (Exception e) {
			logger.error("Error Starting Trajectory Client");
		}
	}

}
