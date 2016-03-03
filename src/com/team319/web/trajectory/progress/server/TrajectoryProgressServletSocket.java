package com.team319.web.trajectory.progress.server;


import java.io.IOException;

import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team319.path.IPathChangeListener;
import com.team319.path.Path;
import com.team319.path.PathManager;
import com.team319.trajectory.TrajectoryManager;
import com.team319.trajectory.progress.CombinedTrajectoryProgress;
import com.team319.trajectory.progress.ITrajectoryProgressChangeListener;
import com.team319.trajectory.progress.TrajectoryProgressManager;
import com.team319.waypoint.WaypointList;
import com.team319.waypoint.WaypointManager;

public class TrajectoryProgressServletSocket extends WebSocketAdapter implements ITrajectoryProgressChangeListener{

	private Logger logger = LoggerFactory.getLogger(this.getClass());

    public TrajectoryProgressServletSocket() {

    }

    @Override
    public void onWebSocketConnect(Session sess) {
    	super.onWebSocketConnect(sess);
    	TrajectoryProgressManager.getInstance().registerListener(this);
    	logger.info("Connected");
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
    	TrajectoryProgressManager.getInstance().unregisterListener(this);
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
				getRemote().sendString("pong");
			} catch (IOException e) {
				logger.error("Unable to send pong");
			} catch (InterruptedException e) {
				logger.error("Unable to sleep");
			}
    	}else{
    		//it wasn't a basic ping
    		ObjectMapper mapper = new ObjectMapper();

    		CombinedTrajectoryProgress progress = null;
        	try {
        		//check to see if it's a TrajectoryProgress
        		progress = mapper.readValue(message, CombinedTrajectoryProgress.class);

        		//let any listeners know the progress has changed
        		TrajectoryProgressManager.getInstance().setProgress(progress);

        	} catch (JsonParseException e) {
    			logger.error("Unable to Parse Json");
    		} catch (JsonMappingException e) {
    			logger.error("The object is not a WaypointList");
    		} catch (IOException e) {
    			logger.error("Unable to Write Object");
    		}

    	}

    }

    @Override
    public void onProgressChange(CombinedTrajectoryProgress progress) {
    	ObjectMapper mapper = new ObjectMapper();

		try {


			String combinedJson = mapper.writeValueAsString(progress);

			//logger.info(combinedJson);

			RemoteEndpoint remote = getRemote();

			if(remote != null){
				remote.sendString(combinedJson);
			}else{
				logger.error("The client has disconnected");
			}

		} catch (IOException e) {
			logger.error("Unable to Write Object");
		} catch (Exception e){
			logger.error("Unable to Send Json");
		}
    }



}
