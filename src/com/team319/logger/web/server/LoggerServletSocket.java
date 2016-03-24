package com.team319.logger.web.server;

import java.io.IOException;

import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team319.logger.LogSender;
import com.team319.logger.LoggableRobot;
import com.team319.logger.RobotLogManager;
import com.team319.logger.RobotLogger;

public class LoggerServletSocket extends WebSocketAdapter implements LogSender {

	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public void onWebSocketClose(int statusCode, String reason) {
		super.onWebSocketClose(statusCode, reason);
		RobotLogger.getInstance().unregisterSender(this);
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

    		LoggableRobot robot = null;
        	try {
        		//check to see if it's Robot
        		robot = mapper.readValue(message, LoggableRobot.class);

        		//let any listeners know the Robot Log has changed
        		RobotLogManager.getInstance().setRobot(robot);

        	} catch (JsonParseException e) {
    			logger.error("Unable to Parse Json");
    		} catch (JsonMappingException e) {
    			logger.error("The object is not a Robot");
    		} catch (IOException e) {
    			logger.error("Unable to Write Object");
    		}

    	}
    }

    @Override
    public void onWebSocketBinary(byte[] imageData, int offset, int len) {
       //not supported
    }
	@Override
	public void sendRobotJson(String json) {
		try {
			RemoteEndpoint endpoint = getRemote();
			if(endpoint == null){
				logger.debug("No one is listening.");
			}else{
				getRemote().sendString(json);
			}
		} catch (IOException e) {
			logger.error("Unable to send log");
		}
	}

}
