package com.team319.vision.config.web.server;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

public class VisionConfigServlet extends WebSocketServlet {

    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.register(VisionConfigServletSocket.class);
    }

}