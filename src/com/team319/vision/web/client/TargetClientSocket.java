package com.team319.vision.web.client;

import java.io.IOException;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team319.vision.Target;
import com.team319.vision.TargetManager;

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
public class TargetClientSocket extends WebSocketAdapter{

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	public TargetClientSocket(){

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

    	if(message.equalsIgnoreCase("pong")){
    		//we received a pong back from the server, we should ping back
    		try {
				getRemote().sendStringByFuture("ping");
				Thread.sleep(100);
			} catch (InterruptedException e) {
				System.err.println("Unable to sleep");
			}
    	}else {
    		//we received something other than the typical pong
    		ObjectMapper mapper = new ObjectMapper();
        	try {
        		Target target = mapper.readValue(message, Target.class);
    			TargetManager.getInstance().setTarget(target);
        	} catch (JsonParseException e) {
    		} catch (JsonMappingException e) {
    		} catch (IOException e) {
    		}
    	}

    }

    @Override
    public void onWebSocketBinary(byte[] imageData, int offset, int len) {
       //not supported
    }

}
