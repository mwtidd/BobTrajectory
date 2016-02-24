package com.team319.web.waypoint.server;


import java.io.IOException;

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
import com.team254.lib.trajectory.TrajectoryGenerator;
import com.team254.lib.trajectory.WaypointSequence;
import com.team319.config.ConfigManager;
import com.team319.trajectory.CombinedSrxMotionProfile;
import com.team319.trajectory.SRXTranslator;
import com.team319.trajectory.ITrajectoryChangeListener;
import com.team319.trajectory.TrajectoryManager;
import com.team319.waypoint.IWaypointChangeListener;
import com.team319.waypoint.WaypointList;
import com.team319.waypoint.WaypointManager;

public class WaypointServletSocket extends WebSocketAdapter implements IWaypointChangeListener{

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private static final String PATH_NAME = "Path";

    public WaypointServletSocket() {
    	WaypointManager.getInstance().registerListener(this);
    }

    @Override
    public void onWebSocketConnect(Session sess) {
    	super.onWebSocketConnect(sess);
    	logger.info("Connected");
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
    	WaypointManager.getInstance().unregisterListener(this);
    	super.onWebSocketClose(statusCode, reason);
    }

    @Override
    public void onWebSocketError(Throwable cause) {
    	WaypointManager.getInstance().unregisterListener(this);
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
    		ObjectMapper mapper = new ObjectMapper();

    		WaypointList waypoints = null;
        	try {
        		//check to see if it's a Waypoint List, if it is create a trajectory
        		waypoints = mapper.readValue(message, WaypointList.class);

        		WaypointManager.getInstance().setWaypointList(waypoints);

        		TrajectoryManager.getInstance().generateTrajectory();

        	} catch (JsonParseException e) {
    			logger.error("Unable to Parse Json");
    		} catch (JsonMappingException e) {
    			logger.error("The object is not a WaypointList");
    		} catch (IOException e) {
    			logger.error("Unable to Write Object");
    		}

    		/**

    		ObjectMapper mapper = new ObjectMapper();

    		//the waypoint server will accept a trajectory response or a set of waypoints from another source


    		CombinedSrxMotionProfile profile = null;
        	try {
        		//check to see if it's an SRX Profile, if it is it and try to pass it on
        		profile = mapper.readValue(message, CombinedSrxMotionProfile.class);
    			TrajectoryManager.getInstance().setLatestProfile(profile);
        	} catch (JsonParseException e) {
    			logger.error("Unable to Parse Json");
    		} catch (JsonMappingException e) {
    			logger.debug("The object is not a CombinedSrxMotionProfile");
    		} catch (IOException e) {
    			logger.error("Unable to Write Object");
    		}

        	WaypointList waypoints = null;
        	try {
        		//check to see if it's a Waypoint List, if it is it and try to pass it on
        		waypoints = mapper.readValue(message, WaypointList.class);
        		WaypointManager.getInstance().setWaypoints(waypoints);
        	} catch (JsonParseException e) {
    			logger.error("Unable to Parse Json");
    		} catch (JsonMappingException e) {
    			logger.debug("The object is not a WaypointList");
    		} catch (IOException e) {
    			logger.error("Unable to Write Object");
    		}

			**/
    	}


    }

    @Override
    public void onWaypointChange(WaypointList path) {
    	ObjectMapper mapper = new ObjectMapper();
		try {
			String pathJson = mapper.writeValueAsString(path);
			RemoteEndpoint endpoint = getRemote();
			if(endpoint != null){
				getRemote().sendString(pathJson);
			}
		} catch (JsonProcessingException e) {
			logger.error("Unable to parse path json.");
		} catch (IOException e) {
			logger.error("Unable to send json.");
		}

    }

}
