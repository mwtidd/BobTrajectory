package com.team319.auto.web.client;

import java.io.IOException;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team319.auto.AutoConfig;
import com.team319.auto.AutoConfigException;
import com.team319.auto.AutoManager;
import com.team319.auto.AutoModes;
import com.team319.auto.IAutoConfigChangeListener;
import com.team319.auto.IAutoModesChangeListener;

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
public class AutoClientSocket extends WebSocketAdapter implements IAutoModesChangeListener, IAutoConfigChangeListener{

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	public AutoClientSocket(){
		AutoManager.getInstance().registerListener((IAutoModesChangeListener)this);
		AutoManager.getInstance().registerListener((IAutoConfigChangeListener)this);
	}

	@Override
	public void onWebSocketClose(int statusCode, String reason) {
		AutoManager.getInstance().unregisterListener((IAutoModesChangeListener)this);
		AutoManager.getInstance().unregisterListener((IAutoConfigChangeListener)this);
		super.onWebSocketClose(statusCode, reason);
	}

	@Override
	public void onWebSocketError(Throwable cause) {
		AutoManager.getInstance().unregisterListener((IAutoModesChangeListener)this);
		AutoManager.getInstance().unregisterListener((IAutoConfigChangeListener)this);
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

    		ObjectMapper mapper = new ObjectMapper();

    		try {
				final JsonNode jsonNode = mapper.readTree(message);

				final String className = jsonNode.get("__class").asText();

				if(className.equalsIgnoreCase(AutoConfig.class.getName())){
					//check to see if it's an Auto Config
            		AutoConfig autoConfig = mapper.readValue(message, AutoConfig.class);

            		//let any listeners know the auto has changed
            		AutoManager.getInstance().setConfig(autoConfig);
				}


			} catch (JsonProcessingException e1) {
				logger.error("Unable to Process Json");
			} catch (IOException e1) {
				logger.error("IO Error");
			}

    	}

    	//logger.info(message);
    }

    @Override
    public void onWebSocketBinary(byte[] imageData, int offset, int len) {
       //not supported
    }

    @Override
    public void onChange(AutoModes autoModes) {
    	sendModes(autoModes);
    }

    @Override
    public void onChange(AutoConfig config) {
    	// TODO Auto-generated method stub

    }

    @Override
    public void onConfigException(AutoConfigException exception) {
    	send(exception);
    }

    public void sendModes(AutoModes autoModes){
    	ObjectMapper mapper = new ObjectMapper();
		try {
			String json = mapper.writeValueAsString(autoModes);

			logger.info(json);

			RemoteEndpoint remote = getRemote();

			if(remote != null){
				remote.sendStringByFuture(json);
			}else{
				logger.error("The client has disconnected");
			}
		} catch (JsonProcessingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    }

    private void send(AutoConfigException exception){
    	ObjectMapper mapper = new ObjectMapper();
		try {
			String json = mapper.writeValueAsString(exception);
			RemoteEndpoint endpoint = getRemote();
			if(endpoint != null){
				logger.info(json);
				getRemote().sendStringByFuture(json);
			}
		} catch (JsonProcessingException e) {
			logger.error("Unable to parse exception json.");
		}
    }


}
