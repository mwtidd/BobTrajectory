package com.team319.web.waypoint.server;


import java.io.IOException;

import org.eclipse.jetty.websocket.api.RemoteEndpoint;
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
import com.team319.trajectory.BobPath;
import com.team319.trajectory.CombinedSrxMotionProfile;
import com.team319.trajectory.PathChangeListener;
import com.team319.trajectory.PathManager;
import com.team319.trajectory.SRXTranslator;
import com.team319.trajectory.TrajectoryChangeListener;
import com.team319.trajectory.TrajectoryManager;

public class WaypointServletSocket extends WebSocketAdapter implements PathChangeListener{

	private Logger logger = LoggerFactory.getLogger(this.getClass());


    public WaypointServletSocket() {
    	PathManager.getInstance().registerListener(this);
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
    	PathManager.getInstance().unregisterListener(this);
    	super.onWebSocketClose(statusCode, reason);
    }

    @Override
    public void onWebSocketError(Throwable cause) {
    	PathManager.getInstance().unregisterListener(this);
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
        	try {
        		//hopefully it's an SRX Profile, let's deserialize it and try to pass it on
        		CombinedSrxMotionProfile profile = mapper.readValue(message, CombinedSrxMotionProfile.class);
    			TrajectoryManager.getInstance().setLatestProfile(profile);
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
    public void onPathChange(BobPath path) {
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
