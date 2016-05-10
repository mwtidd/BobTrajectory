package com.team319.auto.web.server;


import java.io.IOException;

import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team319.auto.AutoConfig;
import com.team319.auto.AutoConfigException;
import com.team319.auto.AutoManager;
import com.team319.auto.AutoModes;
import com.team319.auto.IAutoConfigChangeListener;
import com.team319.auto.IAutoModesChangeListener;

public class AutoServletSocket extends WebSocketAdapter implements IAutoConfigChangeListener, IAutoModesChangeListener{

	private Logger logger = LoggerFactory.getLogger(this.getClass());

    public AutoServletSocket() {

    }

    @Override
    public void onWebSocketConnect(Session sess) {
    	super.onWebSocketConnect(sess);
    	AutoManager.getInstance().registerListener((IAutoModesChangeListener)this);
    	AutoManager.getInstance().registerListener((IAutoConfigChangeListener)this);
    	logger.info("Connected");

    	this.send(AutoManager.getInstance().getModes());
    	this.send(AutoManager.getInstance().getConfig());
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
    	AutoManager.getInstance().unregisterListener((IAutoModesChangeListener)this);
    	AutoManager.getInstance().unregisterListener((IAutoConfigChangeListener)this);
    	super.onWebSocketClose(statusCode, reason);
    }

    @Override
    public void onWebSocketError(Throwable cause) {
    	AutoManager.getInstance().unregisterListener((IAutoModesChangeListener)this);
    	AutoManager.getInstance().unregisterListener((IAutoConfigChangeListener)this);
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

				if(className.equalsIgnoreCase(AutoModes.class.getName())){
					//check to see if it's Auto Modes
	        		AutoModes autoModes = mapper.readValue(message, AutoModes.class);

	        		//let any listeners know the auto modes have changes
	        		AutoManager.getInstance().setModes(autoModes);
				}else if(className.equalsIgnoreCase(AutoConfig.class.getName())){
					//check to see if it's an Auto Config
            		AutoConfig autoConfig = mapper.readValue(message, AutoConfig.class);

            		//let any listeners know the auto has changed
            		AutoManager.getInstance().setConfig(autoConfig);
				}else if(className.equalsIgnoreCase(AutoConfigException.class.getName())){
					//check to see if it's an Exception
					AutoConfigException exception = mapper.readValue(message, AutoConfigException.class);

            		//let any listeners know an exception has occurred
            		AutoManager.getInstance().throwAutoConfigException(exception);
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
    public void onChange(AutoModes autoModes) {
    	this.send(autoModes);
    }

    @Override
    public void onChange(AutoConfig config) {
    	this.send(config);
    }

    @Override
    public void onConfigException(AutoConfigException exception) {
    	this.send(exception);
    }

    private void send(AutoModes autoModes){
    	ObjectMapper mapper = new ObjectMapper();
		try {
			String json = mapper.writeValueAsString(autoModes);
			RemoteEndpoint endpoint = getRemote();
			if(endpoint != null){
				getRemote().sendStringByFuture(json);
			}
		} catch (JsonProcessingException e) {
			logger.error("Unable to parse auto modes json.");
		}
    }

    private void send(AutoConfig config){
    	ObjectMapper mapper = new ObjectMapper();
		try {
			String json = mapper.writeValueAsString(config);
			RemoteEndpoint endpoint = getRemote();
			if(endpoint != null){
				logger.info(json);
				getRemote().sendStringByFuture(json);
			}
		} catch (JsonProcessingException e) {
			logger.error("Unable to parse auto config json.");
		}
    }

    private void send(AutoConfigException exception){
    	ObjectMapper mapper = new ObjectMapper();
		try {
			String json = mapper.writeValueAsString(exception);
			RemoteEndpoint endpoint = getRemote();
			if(endpoint != null){
				//logger.info(json);
				getRemote().sendStringByFuture(json);
			}
		} catch (JsonProcessingException e) {
			logger.error("Unable to parse exception json.");
		}
    }

}
