package com.team319.web.pid.status.server;


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
import com.team319.pid.status.IPidStatusChangeListener;
import com.team319.pid.status.PidStatus;
import com.team319.pid.status.PidStatusManager;

public class PidStatusServletSocket extends WebSocketAdapter implements IPidStatusChangeListener{

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private static final String PATH_NAME = "Path";

    public PidStatusServletSocket() {

    }

    @Override
    public void onWebSocketConnect(Session sess) {
    	super.onWebSocketConnect(sess);
    	PidStatusManager.getInstance().registerListener(this);
    	logger.info("Connected");
    	//TODO: send current waypoint list
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
    	PidStatusManager.getInstance().unregisterListener(this);
    	super.onWebSocketClose(statusCode, reason);
    }

    @Override
    public void onWebSocketError(Throwable cause) {
    	PidStatusManager.getInstance().unregisterListener(this);
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

    		PidStatus pidStatus = null;
        	try {
        		//check to see if it's a Waypoint List, if it is create a trajectory
        		pidStatus = mapper.readValue(message, PidStatus.class);

        		//let any listeners know the waypoints have changes
        		PidStatusManager.getInstance().setStatus(pidStatus);

        	} catch (JsonParseException e) {
    			logger.error("Unable to Parse Json");
    		} catch (JsonMappingException e) {
    			logger.error("The object is not a Pid Status");
    		} catch (IOException e) {
    			logger.error("Unable to Write Object");
    		}

    	}


    }

    @Override
    public void onPidStatusChange(PidStatus status) {
    	ObjectMapper mapper = new ObjectMapper();
		try {
			String pidJson = mapper.writeValueAsString(status);
			RemoteEndpoint endpoint = getRemote();
			if(endpoint != null){
				getRemote().sendStringByFuture(pidJson);
			}
		} catch (JsonProcessingException e) {
			logger.error("Unable to parse pid status json.");
		}
    }


}
