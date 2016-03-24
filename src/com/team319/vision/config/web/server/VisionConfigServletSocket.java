package com.team319.vision.config.web.server;


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
import com.team319.vision.config.VisionConfig;
import com.team319.vision.config.VisionConfigManager;

public class VisionConfigServletSocket extends WebSocketAdapter{

	private Logger logger = LoggerFactory.getLogger(this.getClass());

    public VisionConfigServletSocket() {

    }

    @Override
    public void onWebSocketConnect(Session sess) {
    	super.onWebSocketConnect(sess);
    	logger.info("Connected");
    	this.sendConfig(VisionConfigManager.getInstance().getConfig());

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

    		VisionConfig config = null;
        	try {
        		//check to see if it's a Vision Config
        		config = mapper.readValue(message, VisionConfig.class);

        		//let any listeners know the vision config has changed
        		VisionConfigManager.getInstance().setConfig(config);

        	} catch (JsonParseException e) {
    			logger.error("Unable to Parse Json");
    		} catch (JsonMappingException e) {
    			logger.error("The object is not a WaypointList");
    		} catch (IOException e) {
    			logger.error("Unable to Write Object");
    		}

    	}


    }

    private void sendConfig(VisionConfig config){
    	ObjectMapper mapper = new ObjectMapper();
		try {
			String json = mapper.writeValueAsString(config);
			RemoteEndpoint endpoint = getRemote();
			if(endpoint != null){
				getRemote().sendStringByFuture(json);
			}
		} catch (JsonProcessingException e) {
			logger.error("Unable to parse vision config json.");
		}
    }

}
