package com.team319.web.pid.status.client;

import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team319.pid.status.IPidStatusChangeListener;
import com.team319.pid.status.PidStatus;
import com.team319.pid.status.PidStatusManager;

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
public class PidStatusClientSocket extends WebSocketAdapter implements IPidStatusChangeListener{

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	public PidStatusClientSocket(){
		PidStatusManager.getInstance().registerListener(this);
	}

	@Override
	public void onWebSocketClose(int statusCode, String reason) {
		PidStatusManager.getInstance().unregisterListener(this);
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
    public void onPidStatusChange(PidStatus status) {
    	ObjectMapper mapper = new ObjectMapper();
		try {
			String statusJson = mapper.writeValueAsString(status);

			logger.info(statusJson);

			RemoteEndpoint remote = getRemote();

			if(remote != null){
				remote.sendStringByFuture(statusJson);
			}else{
				logger.error("The client has disconnected");
			}
		} catch (JsonProcessingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    }

}
