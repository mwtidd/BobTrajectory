package com.team319.web.pid.client;

import java.io.IOException;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team319.pid.Pid;
import com.team319.pid.PidManager;

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
public class PidClientSocket extends WebSocketAdapter{

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	public PidClientSocket(){
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
				logger.error("Unable to sleep");
			}
    	}else {
    		//we received something other than the typical pong

    		ObjectMapper mapper = new ObjectMapper();
        	try {
        		//lets try to build a path out of the message
    			Pid pid = mapper.readValue(message, Pid.class);

    			PidManager.getInstance().setPid(pid);
        		logger.info("Got New Pid");
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


}
