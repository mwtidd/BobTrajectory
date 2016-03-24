package com.team319;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.team319.pid.PidManager;
import com.team319.trajectory.TrajectoryManager;
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
