package com.team319;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.team319.vision.TargetManager;
import com.team319.vision.web.client.TargetClient;

/**
 * A basic way to fire up a auto client.
 *
 * It's meant mostly for testing purposes
 *
 * @author mwtidd
 *
 */
public class VisionClientMain{

	private static Logger logger = LoggerFactory.getLogger(VisionClientMain.class);

	private static VisionListener listener = new VisionListener();

	public static void main(String[] args) {

		logger.info("Starting Target Client");

		try {
			TargetManager.getInstance().registerListener(listener);
			TargetClient.start("localhost");
		} catch (Exception e) {
			logger.error("Error Starting Target Client");
		}
	}

}
