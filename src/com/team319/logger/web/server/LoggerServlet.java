package com.team319.logger.web.server;

import javax.servlet.annotation.WebServlet;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

@WebServlet(name="Trajectory Servlet", urlPatterns = {"/trajectory"})
public class LoggerServlet extends WebSocketServlet {

    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.register(LoggerServletSocket.class);
    }

}