package com.team319;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.team319.auto.AutoManager;
import com.team319.auto.AutoModes;
import com.team319.auto.web.client.AutoClient;

/**
 * A basic way to fire up a auto client.
 *
 * It's meant mostly for testing purposes
 *
 * @author mwtidd
 *
 */
public class AutoClientMain {

	private static Logger logger = LoggerFactory.getLogger(AutoClientMain.class);

	public static void main(String[] args) {

		logger.info("Starting Waypoint Client");

		try {
			AutoClient.start("localhost");
			Thread.sleep(1000);
			List<String> modes = new ArrayList<String>();
			modes.add("Drive Straight");
			modes.add("Drive and Shoot");
			AutoModes autoModes = new AutoModes();
			autoModes.setModes(modes);

			List<String> positions = new ArrayList<String>();
			positions.add("Position 1");
			positions.add("Position 2");
			positions.add("Position 3");
			positions.add("Position 4");
			positions.add("Position 5");
			autoModes.setPositions(positions);

			AutoManager.getInstance().setModes(autoModes);
		} catch (Exception e) {
			logger.error("Error Starting Auto Client");
		}
	}

}
