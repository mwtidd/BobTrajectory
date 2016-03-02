package com.team319.web.trajectory.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.team319.TrajectoryServerMain;
import com.team319.trajectory.TrajectoryManager;
import com.team319.web.config.server.ConfigServlet;
import com.team319.web.path.server.PathServlet;
import com.team319.web.waypoint.client.WaypointClient;
import com.team319.web.waypoint.server.WaypointServlet;

import java.util.EnumSet;

import javax.servlet.DispatcherType;

/**
 * This is expected to be run on the kangaroo or the driver station.
 * It can be started from {@link TrajectoryServerMain}
 *
 * It sends trajectory updates to any clients that are listening.
 *
 * @author mwtidd
 *
 */
public class TrajectoryServer {

	private static Logger logger = LoggerFactory.getLogger(TrajectoryServer.class);

    private static Server server;

    public static void startServer() throws Exception{

        server = new Server(5803);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        FilterHolder cors = context.addFilter(CrossOriginFilter.class,"/*",EnumSet.of(DispatcherType.REQUEST));
        cors.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        cors.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,POST,HEAD");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "X-Requested-With,Content-Type,Accept,Origin");

        ServletHolder trajectoryServlet = new ServletHolder("trajectory", new TrajectoryServlet());
        context.addServlet(trajectoryServlet, "/trajectory");

        ServletHolder waypointServlet = new ServletHolder("waypoints", new WaypointServlet());
        context.addServlet(waypointServlet, "/waypoints");

        ServletHolder pathServlet = new ServletHolder("path", new PathServlet());
        context.addServlet(pathServlet, "/path");

        ServletHolder configServlet = new ServletHolder("config", new ConfigServlet());
        context.addServlet(configServlet, "/config");

        try {
        	TrajectoryManager.getInstance().loadTrajectories();
			server.start();
			server.join();
		} catch (Exception e) {
			logger.error("Unable to start server");
		}
    }

}
