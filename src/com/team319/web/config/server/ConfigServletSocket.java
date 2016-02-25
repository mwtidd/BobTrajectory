package com.team319.web.config.server;


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
import com.team319.config.DriveConfig;
import com.team319.config.IConfigChangeListener;
import com.team319.trajectory.CombinedSrxMotionProfile;
import com.team319.trajectory.SRXTranslator;
import com.team319.trajectory.ITrajectoryChangeListener;
import com.team319.trajectory.TrajectoryManager;
import com.team319.waypoint.IWaypointChangeListener;
import com.team319.waypoint.WaypointList;
import com.team319.waypoint.WaypointManager;

public class ConfigServletSocket extends WebSocketAdapter implements IConfigChangeListener{

	private Logger logger = LoggerFactory.getLogger(this.getClass());


    public ConfigServletSocket() {

    }

    @Override
    public void onWebSocketConnect(Session sess) {
    	super.onWebSocketConnect(sess);
    	logger.info("Connected");
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
    	super.onWebSocketClose(statusCode, reason);
    }

    @Override
    public void onWebSocketError(Throwable cause) {
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

    		DriveConfig config;
    		try {
        		//check to see if it's a Drive Config, if it is it and try to pass it on
    			config = mapper.readValue(message, DriveConfig.class);

    			//let any other config interfaces know the config has changed
    			ConfigManager.getInstance().setConfig(config, this);

    			//generate a trajectory with the new config
    			TrajectoryManager.getInstance().generateTrajectory();
        	} catch (JsonParseException e) {
    			logger.error("Unable to Parse Json");
    		} catch (JsonMappingException e) {
    			logger.error("The object is not a WaypointList");
    		} catch (IOException e) {
    			logger.error("Unable to Write Object");
    		}

    	}


    }

    @Override
    public void onConfigChange(DriveConfig config) {
    	ObjectMapper mapper = new ObjectMapper();
		try {
			String configJson = mapper.writeValueAsString(config);
			RemoteEndpoint endpoint = getRemote();
			if(endpoint != null){
				getRemote().sendString(configJson);
			}
		} catch (JsonProcessingException e) {
			logger.error("Unable to parse config json.");
		} catch (IOException e) {
			logger.error("Unable to send json.");
		}
    }

}
