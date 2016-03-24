package com.team319.auto.web.server;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

public class AutoServlet extends WebSocketServlet {

    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.register(AutoServletSocket.class);
    }

}