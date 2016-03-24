package com.team319.logger.web.client;

import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LoggerClientSocket extends WebSocketAdapter {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

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
    	}
    }

    @Override
    public void onWebSocketBinary(byte[] imageData, int offset, int len) {
       //not supported
    }

}
