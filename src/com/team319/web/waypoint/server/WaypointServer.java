package com.team319.web.waypoint.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.team319.TrajectoryServerMain;

import java.util.EnumSet;

import javax.servlet.DispatcherType;

/**
 * This is expected to be run on the robot.
 * It can be started from {@link TrajectoryServerMain}
 *
 * @author mwtidd
 *
 */
public class WaypointServer {

	private static Logger logger = LoggerFactory.getLogger(WaypointServer.class);

    private static Server server;

    public static void startServer() {
        if (server != null) {
        	logger.error("Server has already been started.");
            return;
        }

        server = new Server(5804);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        FilterHolder cors = context.addFilter(CrossOriginFilter.class,"/*",EnumSet.of(DispatcherType.REQUEST));
        cors.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        cors.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,POST,HEAD");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "X-Requested-With,Content-Type,Accept,Origin");

        ServletHolder commandHolder = new ServletHolder("waypoints", new WaypointServlet());
        context.addServlet(commandHolder, "/waypoints");

        try {
			server.start();
			//server.join();
		} catch (Exception e) {
			logger.error("Unable to start server");
		}
    }

}
