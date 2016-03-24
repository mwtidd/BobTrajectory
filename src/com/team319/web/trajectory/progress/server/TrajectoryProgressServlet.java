package com.team319.web.trajectory.progress.server;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

public class TrajectoryProgressServlet extends WebSocketServlet {

    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.register(TrajectoryProgressServletSocket.class);
    }

}