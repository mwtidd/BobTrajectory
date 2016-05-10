package com.team319;

import org.opencv.core.Core;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.team319.web.BobServer;

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
public class BobServerMain {

	private static Logger logger = LoggerFactory.getLogger(BobServerMain.class);

	public static void main(String[] args) {

		// load the native OpenCV library
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		logger.info("Starting Bobotics Server");

		try {
			BobServer.startServer();
		} catch (Exception e) {
			logger.error("Error Starting Server");
		}

	}

}
