package com.team319.web.pid.server;


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
import com.team319.pid.IPidChangeListener;
import com.team319.pid.Pid;
import com.team319.pid.PidManager;

public class PidServletSocket extends WebSocketAdapter implements IPidChangeListener{

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private static final String PATH_NAME = "Path";

    public PidServletSocket() {

    }

    @Override
    public void onWebSocketConnect(Session sess) {
    	super.onWebSocketConnect(sess);
    	PidManager.getInstance().registerListener(this);
    	logger.info("Connected");
    	//TODO: send current waypoint list
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
    	PidManager.getInstance().unregisterListener(this);
    	super.onWebSocketClose(statusCode, reason);
    }

    @Override
    public void onWebSocketError(Throwable cause) {
    	PidManager.getInstance().unregisterListener(this);
    	super.onWebSocketError(cause);
    }

    public void onWebSocketText(String message) {

    	if(message.equalsIgnoreCase("ping")){
    		//we received a ping from a client, we should pong back
    		try {
    			Thread.sleep(100);
				getRemote().sendStringByFuture("pong");
			} catch (InterruptedException e) {
				logger.error("Unable to sleep");
			}
    	}else{
    		//it wasn't a basic ping
    		ObjectMapper mapper = new ObjectMapper();

    		Pid pid = null;
        	try {
        		//check to see if it's a Waypoint List, if it is create a trajectory
        		pid = mapper.readValue(message, Pid.class);

        		//let any listeners know the waypoints have changes
        		PidManager.getInstance().setPid(pid);

        		logger.info("Got New Pid");

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
    public void onPidChange(Pid pid) {
    	ObjectMapper mapper = new ObjectMapper();
		try {
			String pidJson = mapper.writeValueAsString(pid);
			RemoteEndpoint endpoint = getRemote();
			if(endpoint != null){
				getRemote().sendStringByFuture(pidJson);
			}
		} catch (JsonProcessingException e) {
			logger.error("Unable to parse pid json.");
		}
    }


}
