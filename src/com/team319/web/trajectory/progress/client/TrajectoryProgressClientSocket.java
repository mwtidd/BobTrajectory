package com.team319.web.trajectory.progress.client;

import java.io.IOException;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team319.trajectory.progress.CombinedTrajectoryProgress;
import com.team319.trajectory.progress.ITrajectoryProgressChangeListener;
import com.team319.trajectory.progress.TrajectoryProgressManager;

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
public class TrajectoryProgressClientSocket extends WebSocketAdapter implements ITrajectoryProgressChangeListener{

	private final double WHEELBASE_WIDTH = 23.25 / 12; //TODO: this should really be configurable

	private static final String PATH_NAME = "Path";

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	public TrajectoryProgressClientSocket(){
		TrajectoryProgressManager.getInstance().registerListener(this);
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
    public void onProgressChange(CombinedTrajectoryProgress progress) {
    	ObjectMapper mapper = new ObjectMapper();

		try {


			String combinedJson = mapper.writeValueAsString(progress);

			logger.info(combinedJson);

			RemoteEndpoint remote = getRemote();

			if(remote != null){
				remote.sendStringByFuture(combinedJson);
			}else{
				logger.error("The client has disconnected");
			}

		} catch (IOException e) {
			logger.error("Unable to Write Object");
		} catch (Exception e){
			logger.error("Unable to Send Json");
		}
    }

    @Override
    public void onWebSocketBinary(byte[] imageData, int offset, int len) {
       //not supported
    }


}
