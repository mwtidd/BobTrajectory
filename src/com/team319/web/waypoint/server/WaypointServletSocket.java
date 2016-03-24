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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team319.auto.AutoConfig;
import com.team319.auto.AutoConfigException;
import com.team319.auto.AutoManager;
import com.team319.auto.AutoModes;
import com.team319.trajectory.TrajectoryManager;
import com.team319.waypoint.IWaypointChangeListener;
import com.team319.waypoint.WaypointList;
import com.team319.waypoint.WaypointManager;

public class WaypointServletSocket extends WebSocketAdapter implements IWaypointChangeListener{

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private static final String PATH_NAME = "Path";

    public WaypointServletSocket() {

    }

    @Override
    public void onWebSocketConnect(Session sess) {
    	super.onWebSocketConnect(sess);
    	WaypointManager.getInstance().registerListener(this);
    	logger.info("Connected");
    	sendWaypoints(WaypointManager.getInstance().getWaypointList());
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

    		try {
				final JsonNode jsonNode = mapper.readTree(message);

				final String className = jsonNode.get("__class").asText();

				if(className.equalsIgnoreCase(WaypointList.class.getName())){
					WaypointList waypointList = mapper.readValue(message, WaypointList.class);
					WaypointManager.getInstance().setWaypointList(waypointList);
	    			TrajectoryManager.getInstance().generateTrajectory();
				}else{

				}


			} catch (JsonProcessingException e1) {
				logger.error("Unable to Process Json");
			} catch (IOException e1) {
				logger.error("IO Error");
			}

    	}


    }

    @Override
    public void onWaypointChange(WaypointList waypointList) {
    	sendWaypoints(waypointList);
    	WaypointManager.getInstance().save();
    }

    private void sendWaypoints(WaypointList waypointList){
    	ObjectMapper mapper = new ObjectMapper();
		try {
			String waypointListJson = mapper.writeValueAsString(waypointList);
			RemoteEndpoint endpoint = getRemote();
			if(endpoint != null){
				getRemote().sendStringByFuture(waypointListJson);
			}
		} catch (JsonProcessingException e) {
			logger.error("Unable to parse waypoint list json.");
		}
    }

}
