package com.team319.web.config.client;

import java.io.IOException;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team319.trajectory.TrajectoryManager;
import com.team319.config.ConfigManager;
import com.team319.config.DriveConfig;
import com.team319.trajectory.CombinedSrxMotionProfile;
import com.team319.trajectory.ITrajectoryChangeListener;

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
public class ConfigClientSocket extends WebSocketAdapter implements ITrajectoryChangeListener{

	private final double WHEELBASE_WIDTH = 23.25 / 12; //TODO: this should really be configurable

	private static final String PATH_NAME = "Path";

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	public ConfigClientSocket(){
		TrajectoryManager.getInstance().registerListener(this);
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

    	if(message.equalsIgnoreCase("pong")){
    		//we received a pong back from the server, we should ping back
    		try {
				getRemote().sendStringByFuture("ping");
				Thread.sleep(100);
			} catch (InterruptedException e) {
				logger.error("Unable to sleep");
			}
    	}else {
    		//it wasn't a basic ping
    		ObjectMapper mapper = new ObjectMapper();

    		DriveConfig config = null;
        	try {
        		//check to see if it's a Config
        		config = mapper.readValue(message, DriveConfig.class);

        		//let any listeners know the config has changed
        		ConfigManager.getInstance().setConfig(config);

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
	public void onTrajectoryChange(CombinedSrxMotionProfile combined) {
		ObjectMapper mapper = new ObjectMapper();

		try {


			String combinedJson = mapper.writeValueAsString(combined);

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

    @Override
    public void onWebSocketBinary(byte[] imageData, int offset, int len) {
       //not supported
    }


}
