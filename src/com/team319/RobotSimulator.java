package com.team319;

import java.util.ArrayList;
import java.util.List;

import javax.security.auth.login.Configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team319.config.ConfigManager;
import com.team319.config.DriveConfig;
import com.team319.pid.PidManager;
import com.team319.trajectory.CombinedSrxMotionProfile;
import com.team319.trajectory.ITrajectoryChangeListener;
import com.team319.trajectory.TrajectoryManager;
import com.team319.trajectory.progress.CombinedTrajectoryProgress;
import com.team319.trajectory.progress.ITrajectoryProgressChangeListener;
import com.team319.trajectory.progress.TrajectoryProgress;
import com.team319.trajectory.progress.TrajectoryProgressManager;
import com.team319.waypoint.Waypoint;
import com.team319.waypoint.WaypointList;
import com.team319.waypoint.WaypointManager;
import com.team319.web.config.client.ConfigClient;
import com.team319.web.pid.client.PidClient;
import com.team319.web.pid.status.client.PidStatusClient;
import com.team319.web.trajectory.client.TrajectoryClient;
import com.team319.web.trajectory.progress.client.TrajectoryProgressClient;

/**
 * A basic way to fire up a trajectory client.
 *
 * It's meant mostly for testing purposes
 *
 * @author mwtidd
 *
 */
public class RobotSimulator{

	private static Logger logger = LoggerFactory.getLogger(RobotSimulator.class);

	private static boolean running = true;

	private static Simulator simulator;

	public static void main(String[] args) throws InterruptedException {

		logger.info("Starting Trajectory Client");

		try {
			ConfigClient.start("localhost");
			TrajectoryClient.start("localhost");
			TrajectoryProgressClient.start("localhost");
			PidClient.start("localhost");
			PidStatusClient.start("localhost");
		} catch (Exception e) {
			logger.error("Error Starting Trajectory Client");
		}

		Thread.sleep(1000);

		simulator = new Simulator();

		TrajectoryManager.getInstance().registerListener(simulator);

		PidManager.getInstance().registerListener(simulator);

		while(running){

			Thread.sleep(100);

		}
	}

}
