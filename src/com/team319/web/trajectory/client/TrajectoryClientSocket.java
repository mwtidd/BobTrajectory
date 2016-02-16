package com.team319.web.trajectory.client;

import java.io.IOException;
import java.util.List;

import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team319.trajectory.Waypoint;
import com.team319.trajectory.TrajectoryManager;
import com.team319.trajectory.BobPath;
import com.team319.trajectory.CombinedSrxMotionProfile;
import com.team319.trajectory.PathChangeListener;
import com.team319.trajectory.PathManager;

/**
 * This is meant to run locally on the robot
 *
 * It is responsible to maintaining a socket connection between the trajectory server and the robot
 *
 * @author mwtidd
 *
 */
public class TrajectoryClientSocket extends WebSocketAdapter implements PathChangeListener{

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	public TrajectoryClientSocket(){
		PathManager.getInstance().registerListener(this);
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

    	if(message.equalsIgnoreCase("pong")){
    		//we received a pong back from the server, we should ping back
    		try {
				getRemote().sendString("ping");
				Thread.sleep(100);
			} catch (IOException e) {
				logger.error("Unable to send ping");
			} catch (InterruptedException e) {
				logger.error("Unable to sleep");
			}
    	}else {
    		//we received something other than the typical pong
    		ObjectMapper mapper = new ObjectMapper();
        	try {
        		//hopefully it's an SRX Profile, let's deserialize it and try to pass it on
        		CombinedSrxMotionProfile profile = mapper.readValue(message, CombinedSrxMotionProfile.class);
    			TrajectoryManager.getInstance().setLatestProfile(profile);
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

    @Override
    public void onPathChange(BobPath path) {
    	ObjectMapper mapper = new ObjectMapper();
		try {
			String pathJson = mapper.writeValueAsString(path);
			RemoteEndpoint endpoint = getRemote();
			if(endpoint != null){
				getRemote().sendString(pathJson);
			}
		} catch (JsonProcessingException e) {
			logger.error("Unable to parse path json.");
		} catch (IOException e) {
			logger.error("Unable to send json.");
		}

    }

}
