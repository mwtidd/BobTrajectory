package com.team319.vision.image.web.server;


import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.team319.vision.image.IImageListener;
import com.team319.vision.image.ImageManager;

public class ImageServletSocket extends WebSocketAdapter implements IImageListener{

	private Logger logger = LoggerFactory.getLogger(this.getClass());

    public ImageServletSocket() {

    }

    @Override
    public void onWebSocketConnect(Session sess) {
    	super.onWebSocketConnect(sess);
    	ImageManager.getInstance().registerListener(this);
    	logger.info("Connected");
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
    	ImageManager.getInstance().unregisterListener(this);
    	super.onWebSocketClose(statusCode, reason);
    }

    @Override
    public void onWebSocketError(Throwable cause) {
    	ImageManager.getInstance().unregisterListener(this);
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
    	}else{
    		//it wasn't a basic ping



    	}


    }

    @Override
    public void onChange(byte[] image) {
    	try {
			this.sendImage(image);
		} catch (IOException e) {
			logger.error("Unable to send Image");
		}
    }

    private void sendImage(byte[] image) throws IOException{



		RemoteEndpoint endpoint = getRemote();
		if(endpoint != null){
			FileOutputStream fos = new FileOutputStream("image.jpg");
	    	try {
	    	    fos.write(image);
	    	}
	    	finally {
	    	    fos.close();
	    	}
			/**
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ImageIO.write(image, "png", out);
			ByteBuffer byteBuffer = ByteBuffer.wrap(out.toByteArray());
			getRemote().sendBytes(byteBuffer);
			out.close();
			byteBuffer.clear();
			**/
			getRemote().sendBytes(ByteBuffer.wrap(image));
		}
    }
}
