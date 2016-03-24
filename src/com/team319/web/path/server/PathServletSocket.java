package com.team319.web.path.server;


import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team319.path.IPathChangeListener;
import com.team319.path.Path;
import com.team319.path.PathManager;

public class PathServletSocket extends WebSocketAdapter implements IPathChangeListener{

	private Logger logger = LoggerFactory.getLogger(this.getClass());

    public PathServletSocket() {

    }

    @Override
    public void onWebSocketConnect(Session sess) {
    	super.onWebSocketConnect(sess);
    	PathManager.getInstance().registerListener(this);
    	logger.info("Connected");
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
    	PathManager.getInstance().unregisterListener(this);
    	super.onWebSocketClose(statusCode, reason);
    }

    @Override
    public void onWebSocketError(Throwable cause) {
    	PathManager.getInstance().unregisterListener(this);
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
    	}

    }

    @Override
    public void onPathChange(Path path) {
    	ObjectMapper mapper = new ObjectMapper();

		try {


			String combinedJson = mapper.writeValueAsString(PathManager.getInstance().getLatestPath());

			//logger.info(combinedJson);

			RemoteEndpoint remote = getRemote();

			if(remote != null){
				remote.sendStringByFuture(combinedJson);
			}else{
				logger.error("The client has disconnected");
			}

		} catch (Exception e){
			logger.error("Unable to Send Json");
		}
    }



}
