package com.team319.vision.config.web.client;

import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team319.vision.config.IVisionConfigChangeListener;
import com.team319.vision.config.VisionConfig;

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
public class VisionConfigClientSocket extends WebSocketAdapter implements IVisionConfigChangeListener{

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	public VisionConfigClientSocket(){
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

    	//logger.info(message);
    }

    @Override
    public void onWebSocketBinary(byte[] imageData, int offset, int len) {
       //not supported
    }

    @Override
    public void onChange(VisionConfig config) {
    	sendConfig(config);
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
