package com.team319.web.waypoint.client;

import java.io.IOException;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team319.waypoint.IWaypointChangeListener;
import com.team319.waypoint.WaypointList;
import com.team319.waypoint.WaypointManager;

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
public class WaypointClientSocket extends WebSocketAdapter implements IWaypointChangeListener{

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	public WaypointClientSocket(){
		WaypointManager.getInstance().registerListener(this);
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
				final JsonNode jsonNode = mapper.readTree(message);

				final String className = jsonNode.get("__class").asText();

				if(className.equalsIgnoreCase(WaypointList.class.getName())){
					WaypointList waypointList = mapper.readValue(message, WaypointList.class);
					WaypointManager.getInstance().setWaypointList(waypointList);
				}else{

				}


			} catch (JsonProcessingException e1) {
				logger.error("Unable to Process Json");
			} catch (IOException e1) {
				logger.error("IO Error");
			}


    	}






    	//logger.info(message);
    }

    @Override
    public void onWebSocketBinary(byte[] imageData, int offset, int len) {
       //not supported
    }

    @Override
    public void onWaypointChange(WaypointList waypointList) {
    	ObjectMapper mapper = new ObjectMapper();
		try {
			String waypointJson = mapper.writeValueAsString(waypointList);

			logger.info(waypointJson);

			RemoteEndpoint remote = getRemote();

			if(remote != null){
				remote.sendStringByFuture(waypointJson);
			}else{
				logger.error("The client has disconnected");
			}
		} catch (JsonProcessingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}


    }


}
