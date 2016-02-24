package com.team319.trajectory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.team254.lib.trajectory.Path;
import com.team254.lib.trajectory.PathGenerator;
import com.team254.lib.trajectory.WaypointSequence;
import com.team319.config.ConfigManager;
import com.team319.waypoint.WaypointManager;
import com.team319.web.trajectory.server.TrajectoryServletSocket;

/**
 * This is used both on the robot and the trajectory sever.
 * On the robot is is used to update a listener (in our case the TowerCamera)
 * On the server is is used to update any sockets listening for a new trajectory
 *
 * @author mwtidd
 *
 */
public class TrajectoryManager {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private static TrajectoryManager instance = null;

	private List<ITrajectoryChangeListener> listeners;

	private CombinedSrxMotionProfile latestProfile;

	private static final String PATH_NAME = "Path";

	private TrajectoryManager(){
		listeners = new ArrayList<ITrajectoryChangeListener>();
		latestProfile = null;
	}

	public static TrajectoryManager getInstance(){
		if(instance == null){
			instance = new TrajectoryManager();
		}
		return instance;
	}

	public void registerListener(ITrajectoryChangeListener listener){
		this.listeners.add(listener);
	}

	public void unregisterListener(ITrajectoryChangeListener listener){
		this.listeners.remove(listener);
	}

	public void setLatestProfile(CombinedSrxMotionProfile latestProfile){
		//the profile has changed, we should let everyone know
		this.latestProfile = latestProfile;
		for(ITrajectoryChangeListener listener : listeners){
			listener.onTrajectoryChange(latestProfile);
		}
	}

	public CombinedSrxMotionProfile getLatestProfile() {
		return latestProfile;
	}

	public void generateTrajectory(){
		long startTime = System.currentTimeMillis();

		WaypointSequence sequence = WaypointManager.getInstance().getWaypointList().toWaypointSequence();

		//looks good, let's generate a chezy path and trajectory

		Path path = PathGenerator.makePath(sequence, ConfigManager.getInstance().getConfig().toChezyConfig(), ConfigManager.getInstance().getConfig().getWidth(), PATH_NAME);

		logger.info("Path Gen Took " + (System.currentTimeMillis() - startTime) + "ms");

		SRXTranslator srxt = new SRXTranslator();
		CombinedSrxMotionProfile combined = srxt.getSrxProfileFromChezyPath(path, 5.875, 1.57);//2.778);

		logger.info("SRXing Took " + (System.currentTimeMillis() - startTime) + "ms");

		//the trajectory looks good, lets pass it back
		setLatestProfile(combined);

		logger.info("Total Gen Took " + (System.currentTimeMillis() - startTime) + "ms");

		//sendTrajectory();
	}

	private static boolean writeFile(String filePath, String data) {
		try {
			File file = new File(filePath);

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(data);
			bw.close();
		} catch (IOException e) {
			return false;
		}

		return true;
	}

}
