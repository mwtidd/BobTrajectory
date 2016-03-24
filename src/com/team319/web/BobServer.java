package com.team319.web;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.team319.BobServerMain;
import com.team319.auto.AutoManager;
import com.team319.auto.web.server.AutoServlet;
import com.team319.config.ConfigManager;
import com.team319.vision.BeaconController;
import com.team319.vision.Target;
import com.team319.vision.TargetManager;
import com.team319.vision.config.VisionConfigManager;
import com.team319.vision.config.VisionRgb;
import com.team319.vision.config.web.server.VisionConfigServlet;
import com.team319.vision.image.ImageManager;
import com.team319.vision.image.web.server.ImageServlet;
import com.team319.vision.web.server.TargetServlet;
import com.team319.waypoint.WaypointManager;
import com.team319.web.config.server.ConfigServlet;
import com.team319.web.path.server.PathServlet;
import com.team319.web.pid.server.PidServlet;
import com.team319.web.pid.status.server.PidStatusServlet;
import com.team319.web.trajectory.progress.server.TrajectoryProgressServlet;
import com.team319.web.trajectory.server.TrajectoryServlet;
import com.team319.web.waypoint.client.WaypointClientSocket;
import com.team319.web.waypoint.server.WaypointServlet;

import javafx.scene.image.Image;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.DispatcherType;

/**
 * This is expected to be run on the kangaroo or the driver station.
 * It can be started from {@link BobServerMain}
 *
 * It sends trajectory updates to any clients that are listening.
 *
 * @author mwtidd
 *
 */
public class BobServer {

	private static Logger logger = LoggerFactory.getLogger(BobServer.class);

	private static final Server SERVER = new Server(5803);

	private static final CameraController CAMERA = new CameraController();

    public static void startServer() throws Exception{
    	initialize();
    }




 	/**
 	 * Initialize method, automatically called by @{link FXMLLoader}
 	 */
 	public static void initialize()
 	{
 		CAMERA.initialize();

 		CAMERA.startCamera();

 		AutoManager.getInstance().recall();
 		WaypointManager.getInstance().recall();

 		BeaconController.getInstance().initialize();

 		new Thread(new Runnable() {

 			@Override
 			public void run() {

 				ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
 		        context.setContextPath("/");
 		        SERVER.setHandler(context);

 		        FilterHolder cors = context.addFilter(CrossOriginFilter.class,"/*",EnumSet.of(DispatcherType.REQUEST));
 		        cors.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
 		        cors.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
 		        cors.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,POST,HEAD");
 		        cors.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "X-Requested-With,Content-Type,Accept,Origin");

 		        ServletHolder trajectoryServlet = new ServletHolder("trajectory", new TrajectoryServlet());
 		        context.addServlet(trajectoryServlet, "/trajectory");

 		        ServletHolder trajectoryProgressServlet = new ServletHolder("progress", new TrajectoryProgressServlet());
 		        context.addServlet(trajectoryProgressServlet, "/trajectory/progress");

 		        ServletHolder waypointServlet = new ServletHolder("waypoints", new WaypointServlet());
 		        context.addServlet(waypointServlet, "/waypoints");

 		        ServletHolder pathServlet = new ServletHolder("path", new PathServlet());
 		        context.addServlet(pathServlet, "/path");

 		        ServletHolder configServlet = new ServletHolder("config", new ConfigServlet());
 		        context.addServlet(configServlet, "/config");

 		        ServletHolder pidServlet = new ServletHolder("pid", new PidServlet());
 		        context.addServlet(pidServlet, "/pid");

 		        ServletHolder pidStatusServlet = new ServletHolder("status", new PidStatusServlet());
 		        context.addServlet(pidStatusServlet, "/pid/status");

 		        ServletHolder autoServlet = new ServletHolder("auto", new AutoServlet());
 		        context.addServlet(autoServlet, "/auto");

 		        ServletHolder targetServlet = new ServletHolder("target", new TargetServlet());
 		        context.addServlet(targetServlet, "/vision/target");

 		        ServletHolder visionConfigServlet = new ServletHolder("visionConfig", new VisionConfigServlet());
 		        context.addServlet(visionConfigServlet, "/vision/config");

 		        ServletHolder visionImageServlet = new ServletHolder("visionImage", new ImageServlet());
 		        context.addServlet(visionImageServlet, "/vision/image");

 		        String appDir = this.getClass().getClassLoader().getResource("app/").toExternalForm();
 		        ServletHolder holderPwd = new ServletHolder("default", new DefaultServlet());
 		        holderPwd.setInitParameter("resourceBase", appDir);
 		        holderPwd.setInitParameter("dirAllowed", "true");
 		        context.addServlet(holderPwd, "/");

 		        try {
 					SERVER.start();
 					logger.info("Server Started");
 					SERVER.join();
 					logger.info("Server Join");
 				} catch (Exception e) {
 					logger.error("Unable to Start Server");
 				}


 			}

 		}).start();

 		/**

         try {
         	TrajectoryManager.getInstance().loadTrajectories();
         	SERVER.start();
 			//startCamera();
 			BeaconController.getInstance().initialize();

 			SERVER.join();
 			//server.join();
 		} catch (Exception e) {
 			logger.error("Unable to start server");
 		}
 		**/
 	}





}
