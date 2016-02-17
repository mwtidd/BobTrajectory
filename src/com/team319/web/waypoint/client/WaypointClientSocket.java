package com.team319.web.waypoint.client;

import java.io.IOException;
import java.util.List;

import javax.imageio.ImageTranscoder;

import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team254.lib.trajectory.Path;
import com.team254.lib.trajectory.PathGenerator;
import com.team254.lib.trajectory.WaypointSequence;
import com.team319.trajectory.Waypoint;
import com.team319.trajectory.TrajectoryManager;
import com.team319.trajectory.WaypointList;
import com.team319.trajectory.CombinedSrxMotionProfile;
import com.team319.trajectory.DriveConfigManager;
import com.team319.trajectory.IWaypointChangeListener;
import com.team319.trajectory.WaypointManager;
import com.team319.trajectory.SRXTranslator;
import com.team319.trajectory.ITrajectoryChangeListener;

/**
 * This is meant to run on the Kangaroo or the Driver Station
 *
 * It is responsible to maintaining a socket connection with the robot.
 *
 * It will receive lists of waypoints and convert them to trajctories
 *
 * @author mwtidd
 *
 */
public class WaypointClientSocket extends WebSocketAdapter implements ITrajectoryChangeListener{

	private final double WHEELBASE_WIDTH = 23.25 / 12; //TODO: this should really be configurable

	private static final String PATH_NAME = "Path";

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	public WaypointClientSocket(){
		TrajectoryManager.getInstance().registerListener(this);
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

    	if(message.equalsIgnoreCase("pong")){
    		//we received a pong back from the robot, we should ping back
    		try {
				getRemote().sendString("ping");
				Thread.sleep(100);
			} catch (IOException e) {
				logger.error("Unable to send ping");
			} catch (InterruptedException e) {
				logger.error("Unable to sleep");
			}
    	}else {
    		//we received something other than the typical pong

    		logger.info("Generating Trajectory...");

        	ObjectMapper mapper = new ObjectMapper();

        	try {

        		logger.info("Received Message: " + message);

        		//lets try to build a path out of the message
    			WaypointList waypoints = mapper.readValue(message, WaypointList.class);
    			WaypointSequence sequence = waypoints.toWaypointSequence();

    			//looks good, let's generate a chezy path and trajectory

    			Path path = PathGenerator.makePath(sequence, DriveConfigManager.getInstance().getDriveConfig(), WHEELBASE_WIDTH, PATH_NAME);


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






    	//logger.info(message);
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

    @Override
    public void onWebSocketBinary(byte[] imageData, int offset, int len) {
       //not supported
    }


}
