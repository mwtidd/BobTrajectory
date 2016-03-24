package com.team319.vision.web.server;


import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team319.vision.ITargetListener;
import com.team319.vision.Target;
import com.team319.vision.TargetManager;

public class TargetServletSocket extends WebSocketAdapter implements ITargetListener{

	private Logger logger = LoggerFactory.getLogger(this.getClass());

    public TargetServletSocket() {

    }

    @Override
    public void onWebSocketConnect(Session sess) {
    	super.onWebSocketConnect(sess);
    	TargetManager.getInstance().registerListener(this);
    	logger.info("Connected");
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
    	TargetManager.getInstance().unregisterListener(this);
    	super.onWebSocketClose(statusCode, reason);
    }

    @Override
    public void onWebSocketError(Throwable cause) {
    	TargetManager.getInstance().unregisterListener(this);
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


    	}


    }

    @Override
    public void onTargetChange(Target target) {
    	this.sendTarget(target);
    }

    private void sendTarget(Target target){
    	ObjectMapper mapper = new ObjectMapper();
		try {
			String json = mapper.writeValueAsString(target);
			RemoteEndpoint endpoint = getRemote();
			if(endpoint != null){
				logger.info(json);
				getRemote().sendStringByFuture(json);
			}
		} catch (JsonProcessingException e) {
			logger.error("Unable to parse target json.");
		}
    }

}
