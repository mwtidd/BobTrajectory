package com.team319.web.trajectory.server;


import java.io.IOException;

import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team254.lib.trajectory.Path;
import com.team254.lib.trajectory.PathGenerator;
import com.team254.lib.trajectory.TrajectoryGenerator;
import com.team254.lib.trajectory.WaypointSequence;
import com.team319.trajectory.BobPath;
import com.team319.trajectory.CombinedSrxMotionProfile;
import com.team319.trajectory.SRXTranslator;
import com.team319.trajectory.TrajectoryChangeListener;
import com.team319.trajectory.TrajectoryManager;

public class TrajectoryServletSocket extends WebSocketAdapter implements TrajectoryChangeListener{

	private Logger logger = LoggerFactory.getLogger(this.getClass());


	private final double kWheelbaseWidth = 23.25 / 12;
	private TrajectoryGenerator.Config config;

	private static final String PATH_NAME = "Path";

    public TrajectoryServletSocket() {
    	TrajectoryManager.getInstance().registerListener(this);

        config = new TrajectoryGenerator.Config();
    	config.dt = .01;
    	config.max_acc = 3;
    	config.max_jerk = 6;
    	config.max_vel = 15;
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
    	TrajectoryManager.getInstance().unregisterListener(this);
    	super.onWebSocketClose(statusCode, reason);
    }

    @Override
    public void onWebSocketError(Throwable cause) {
    	TrajectoryManager.getInstance().unregisterListener(this);
    	super.onWebSocketError(cause);
    }

    public void onWebSocketText(String message) {

    	if(message.equalsIgnoreCase("ping")){
    		//we received a ping from the robot, we should pong back
    		try {
				getRemote().sendString("pong");
			} catch (IOException e) {
				logger.error("Unable to send pong");
			}
    	}else{
    		//it wasn't a basic ping

        	logger.info("Generating Trajectory...");

        	ObjectMapper mapper = new ObjectMapper();

        	try {

        		logger.info("Received Message: " + message);

        		//lets try to build a path out of the message
    			BobPath waypoints = mapper.readValue(message, BobPath.class);
    			WaypointSequence sequence = waypoints.toWaypointSequence();

    			//looks good, let's generate a chezy path and trajectory

    			Path path = PathGenerator.makePath(sequence, config, kWheelbaseWidth, PATH_NAME);


    			SRXTranslator srxt = new SRXTranslator();
    			CombinedSrxMotionProfile combined = srxt.getSrxProfileFromChezyPath(path, 5.875, 1.57);//2.778);

    			//the trajectory looks good, lets pass it back
    			TrajectoryManager.getInstance().setLatestProfile(combined);

    			logger.info("Trajectory Sent...");

    		} catch (JsonParseException e) {
    			logger.error("Unable to Parse Json");
    		} catch (JsonMappingException e) {
    			logger.error("Unable to Map Json");
    		} catch (IOException e) {
    			logger.error("Unable to Write Object");
    		}
    	}


    }

	@Override
	public void onTrajectoryChange(CombinedSrxMotionProfile combined) {
		ObjectMapper mapper = new ObjectMapper();

		try {


			String combinedJson = mapper.writeValueAsString(combined);

			logger.info(combinedJson);

			RemoteEndpoint remote = getRemote();

			if(remote != null){
				remote.sendString(combinedJson);
			}else{
				logger.error("The client has disconnected");
			}

		} catch (IOException e) {
			logger.error("Unable to Write Object");
		} catch (Exception e){
			logger.error("Unable to Send Json");
		}
	}

}
