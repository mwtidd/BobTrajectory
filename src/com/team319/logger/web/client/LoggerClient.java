package com.team319.logger.web.client;

import java.net.ConnectException;
import java.net.URI;
import java.util.concurrent.Future;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerClient {

	private static Logger logger = LoggerFactory.getLogger(LoggerClient.class);

	public static void start() throws Exception {

        URI destUri = new URI("ws://roborio-319-frc.local:5802/logger");

        WebSocketClient client = new WebSocketClient();

        boolean connected = false;
        while(!connected){
	    	try{
	        	client.start();
	            LoggerClientSocket socket = new LoggerClientSocket();
	            Future<Session> futureSession = client.connect(socket,destUri);
	            Session session = futureSession.get();

	            session.getRemote().sendString("start");
	            connected = true;
	        }catch(Throwable t){
	        	if(t.getCause() instanceof ConnectException){
	        		logger.info("Waiting for the server to start...");
	            	Thread.sleep(100);
	        	}else{
	        		logger.error("An error occured with the client.");
	        		return;
	        	}

	        }
        }

    }

}
