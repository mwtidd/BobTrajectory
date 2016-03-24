package com.team319.auto.web.client;

import java.net.ConnectException;
import java.net.URI;
import java.util.concurrent.Future;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.team319.auto.AutoManager;

/**
 * This is meant to run on the Kangraoo or the Driver Station
 *
 * It can be started from {@link WaypointClientMain}
 *
 * It is responsible to establishing a socket connection with the robot
 *
 * @author mwtidd
 *
 */
public class AutoClient {

	private static Logger logger = LoggerFactory.getLogger(AutoClient.class);

	public static void start(String ipAddress) throws Exception {

		URI destUri = new URI("ws://"+ipAddress+":5803/auto");

        WebSocketClient client = new WebSocketClient();

        //listen for and connect to server via a thread so if there are any issues it doesn't hang
        new Thread(new Runnable() {

			@Override
			public void run() {
				boolean connected = false;
				while(!connected){
					//keep trying to connect
			    	try{
			        	client.start();
			            AutoClientSocket socket = new AutoClientSocket();
			            Future<Session> futureSession = client.connect(socket,destUri);

			            //wait for the connection to be established
			            Session session = futureSession.get();

			            //start playing ping pong with the robot
			            session.getRemote().sendStringByFuture("ping");

			            //the socket connection has been established, our work is done here
			            connected = true;
			            logger.info("Connected");

			            socket.sendModes(AutoManager.getInstance().getModes());

			        }catch(Throwable t){
			        	//handle the various issues that may have occured

			        	if(t.getCause() instanceof ConnectException){
			        		logger.info("Waiting for the server to start...");
			        	}else{
			        		logger.error("An error occured with the client.");
			        		return;
			        	}

			        }
			    	try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						logger.error("An error occured with the thread sleep.");
						return;
					}
		        }
			}
		}).start();

    }

}
