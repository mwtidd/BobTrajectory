package com.team319.web.trajectory.server;


import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team319.trajectory.CombinedSrxMotionProfile;
import com.team319.trajectory.ITrajectoryChangeListener;
import com.team319.trajectory.TrajectoryManager;

public class TrajectoryServletSocket extends WebSocketAdapter implements ITrajectoryChangeListener{

	private Logger logger = LoggerFactory.getLogger(this.getClass());

    public TrajectoryServletSocket() {

    }

    @Override
    public void onWebSocketConnect(Session sess) {
    	super.onWebSocketConnect(sess);
    	TrajectoryManager.getInstance().registerListener(this);
    	logger.info("Connected");
    	//TODO: send current waypoint list
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
    	TrajectoryManager.getInstance().unregisterListener(this);
    	super.onWebSocketClose(statusCode, reason);
    }

    @Override
    public void onWebSocketError(Throwable cause) {
    	TrajectoryManager.getInstance().unregisterListener(this);
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
	public void onTrajectoryChange(CombinedSrxMotionProfile combined) {
		ObjectMapper mapper = new ObjectMapper();

		try {


			String combinedJson = mapper.writeValueAsString(TrajectoryManager.getInstance().getLatestProfile());

			logger.info(combinedJson);

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
