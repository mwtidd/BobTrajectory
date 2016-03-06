package com.team319.web.trajectory.client;

import java.io.IOException;
import java.util.List;

import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team319.trajectory.TrajectoryManager;
import com.team319.waypoint.IWaypointChangeListener;
import com.team319.waypoint.Waypoint;
import com.team319.waypoint.WaypointList;
import com.team319.waypoint.WaypointManager;
import com.team319.web.trajectory.server.TrajectoryServletSocket;
import com.team319.trajectory.CombinedSrxMotionProfile;
import com.team319.trajectory.ITrajectoryChangeListener;

/**
 * This is meant to run locally on the robot
 *
 * It is responsible to maintaining a socket connection between the trajectory server and the robot
 *
 * @author mwtidd
 *
 */
public class TrajectoryClientSocket extends WebSocketAdapter implements ITrajectoryChangeListener{

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	public TrajectoryClientSocket(){
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
    		//we received a pong back from the server, we should ping back
    		try {
				getRemote().sendStringByFuture("ping");
				Thread.sleep(100);
			} catch (InterruptedException e) {
				logger.error("Unable to sleep");
			}
    	}else {
    		//we received something other than the typical pong
    		ObjectMapper mapper = new ObjectMapper();
        	try {
        		//hopefully it's an SRX Profile, let's deserialize it and try to pass it on
        		CombinedSrxMotionProfile profile = mapper.readValue(message, CombinedSrxMotionProfile.class);
        		logger.info("Got New Profile");
    			TrajectoryManager.getInstance().setLatestProfile(profile);
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
    public void onWebSocketBinary(byte[] imageData, int offset, int len) {
       //not supported
    }

    @Override
    public void onTrajectoryChange(CombinedSrxMotionProfile latestProfile) {
    	// TODO Auto-generated method stub
    	//TrajectoryManager.getInstance().setLatestProfile(latestProfile);
    	latestProfile.toString();
    }

}
