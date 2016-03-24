package com.team319;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.DispatcherType;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
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

import com.team319.auto.AutoManager;
import com.team319.auto.web.server.AutoServlet;
import com.team319.vision.BeaconController;
import com.team319.vision.Target;
import com.team319.vision.TargetManager;
import com.team319.vision.config.VisionConfigManager;
import com.team319.vision.config.VisionRgb;
import com.team319.vision.config.web.server.VisionConfigServlet;
import com.team319.vision.image.ImageManager;
import com.team319.vision.image.web.server.ImageServlet;
import com.team319.vision.web.server.TargetServlet;
import com.team319.web.config.server.ConfigServlet;
import com.team319.web.path.server.PathServlet;
import com.team319.web.pid.server.PidServlet;
import com.team319.web.pid.status.server.PidStatusServlet;
import com.team319.web.trajectory.progress.server.TrajectoryProgressServlet;
import com.team319.web.trajectory.server.TrajectoryServlet;
import com.team319.web.waypoint.server.WaypointServlet;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * The controller associated with the only view of our application. The
 * application logic is implemented here. It handles the button for
 * starting/stopping the camera, the acquired video stream, the relative
 * controls and the histogram creation.
 *
 * @author <a href="mailto:luigi.derussis@polito.it">Luigi De Russis</a>
 * @version 1.1 (2015-10-20)
 * @since 1.0 (2013-11-20)
 *
 */
public class ServerController
{
	// the FXML button
	@FXML
	private Button button;
	// the FXML grayscale checkbox
	@FXML
	private CheckBox grayscale;
	// the FXML logo checkbox
	@FXML
	private CheckBox logoCheckBox;
	// the FXML grayscale checkbox
	@FXML
	private ImageView histogram;
	// the FXML area for showing the current frame
	@FXML
	private ImageView currentFrame;

	// a timer for acquiring the video stream
	private ScheduledExecutorService timer;
	// the OpenCV object that realizes the video capture
	private VideoCapture capture;
	// a flag to change the button behavior
	private boolean cameraActive;
	// the logo to be loaded
	private Mat logo;

	private static Logger logger = LoggerFactory.getLogger(ServerController.class);

    private static final Server SERVER = new Server(5803);

	/**
	 * Initialize method, automatically called by @{link FXMLLoader}
	 */
	public void initialize()
	{
		this.capture = new VideoCapture();

		startCamera();

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

	/**
	 * The action triggered by pushing the button on the GUI
	 */
	@FXML
	protected void startCamera()
	{
		// set a fixed width for the frame
		this.currentFrame.setFitWidth(600);
		// preserve image ratio
		this.currentFrame.setPreserveRatio(true);

		if (!this.cameraActive)
		{
			// start the video capture
			//this.capture.open(0);
			this.capture.open(1);

			this.capture.set(Videoio.CAP_PROP_AUTO_EXPOSURE, 0);
			this.capture.set(Videoio.CAP_PROP_EXPOSURE, VisionConfigManager.getInstance().getConfig().getCamera().getExposure());//-10);

			// is the video stream available?
			if (this.capture.isOpened())
			{
				this.cameraActive = true;

				// grab a frame every 33 ms (30 frames/sec)
				Runnable frameGrabber = new Runnable() {

					@Override
					public void run()
					{
						Image imageToShow = grabFrame();
						currentFrame.setImage(imageToShow);
					}
				};

				this.timer = Executors.newSingleThreadScheduledExecutor();
				this.timer.scheduleAtFixedRate(frameGrabber, 0, 500, TimeUnit.MILLISECONDS);

				// update the button content
				this.button.setText("Stop Camera");
			}
			else
			{
				// log the error
				System.err.println("Impossible to open the camera connection...");
			}
		}
		else
		{
			// the camera is not active at this point
			this.cameraActive = false;
			// update again the button content
			this.button.setText("Start Camera");

			// stop the timer
			try
			{
				this.timer.shutdown();
				this.timer.awaitTermination(33, TimeUnit.MILLISECONDS);
			}
			catch (InterruptedException e)
			{
				// log the exception
				System.err.println("Exception in stopping the frame capture, trying to release the camera now... " + e);
			}

			// release the camera
			this.capture.release();
			// clean the frame
			this.currentFrame.setImage(null);
		}
	}

	/**
	 * The action triggered by selecting/deselecting the logo checkbox
	 */
	@FXML
	protected void loadLogo()
	{
		if (logoCheckBox.isSelected())
		{
			// read the logo only when the checkbox has been selected
			this.logo = Imgcodecs.imread("resources/Poli.png");
		}
	}

	/**
	 * Get a frame from the opened video stream (if any)
	 *
	 * @return the {@link Image} to show
	 */
	private Image grabFrame()
	{
		// init everything
		Image imageToShow = null;
		Mat frame = new Mat();

		this.capture.set(Videoio.CAP_PROP_BRIGHTNESS, VisionConfigManager.getInstance().getConfig().getCamera().getBrightness());
		this.capture.set(Videoio.CAP_PROP_EXPOSURE, VisionConfigManager.getInstance().getConfig().getCamera().getExposure());
		this.capture.set(Videoio.CAP_PROP_CONTRAST, VisionConfigManager.getInstance().getConfig().getCamera().getContrast());
		this.capture.set(Videoio.CAP_PROP_GAIN, VisionConfigManager.getInstance().getConfig().getCamera().getGain());

		// check if the capture is open
		if (this.capture.isOpened())
		{
			try
			{
				// read the current frame
				this.capture.read(frame);

				// if the frame is not empty, process it
				if (!frame.empty())
				{


					// add a logo...
					if (logoCheckBox.isSelected() && this.logo != null)
					{
						Rect roi = new Rect(frame.cols() - logo.cols(), frame.rows() - logo.rows(), logo.cols(),
								logo.rows());
						Mat imageROI = frame.submat(roi);
						// add the logo: method #1
						Core.addWeighted(imageROI, 1.0, logo, 0.8, 0.0, imageROI);

						// add the logo: method #2
						// logo.copyTo(imageROI, logo);
					}

					// if the grayscale checkbox is selected, convert the image
					// (frame + logo) accordingly
					if (grayscale.isSelected())
					{
						Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2GRAY);
					}

					// show the histogram
					this.showHistogram(frame, grayscale.isSelected());

					// convert the Mat object (OpenCV) to Image (JavaFX)
					imageToShow = mat2Image(frame);

					Mat proc = new Mat();

					//Imgcodecs.imwrite("original.png", frame);
					//BGR

					VisionRgb rgb;

					if(AutoManager.getInstance().isOnRed()){
						rgb = VisionConfigManager.getInstance().getConfig().getRedLight();
					}else{
						rgb = VisionConfigManager.getInstance().getConfig().getBlueLight();
					}

					Core.inRange(frame, new Scalar(rgb.getBlueMin(),rgb.getGreenMin(),rgb.getRedMin()), new Scalar(rgb.getBlueMax(),rgb.getGreenMax(),rgb.getRedMax()), proc);

				    //Core.inRange(frame, new Scalar(0,0,20), new Scalar(0,0,255), frame);


				    List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
				    Mat hierarchy = new Mat();

				    /**
				    //remove noise
				    int size = 2;
				    Imgproc.erode(frame, frame, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(size,size)));
				    Imgproc.dilate(frame, frame, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(size,size)));

				    //join the target
				    size = 20;
				    Imgproc.dilate(frame, frame, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(size,size)));
				    Imgproc.erode(frame, frame, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(size,size)));
					**/
				    /**

				    Imgproc.erode(frame, frame, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(size,size)));
				    Imgproc.dilate(frame, frame, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(size,size)));


				    Imgproc.dilate(frame, frame, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(size,size)));
				    Imgproc.erode(frame, frame, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(size,size)));

					**/
				 // convert the Mat object (OpenCV) to Image (JavaFX)
					//imageToShow = mat2Image(frame);

				    Imgproc.findContours(proc, contours, hierarchy, Imgproc.RETR_EXTERNAL , Imgproc.CHAIN_APPROX_SIMPLE);

				    System.out.println("Found " + contours.size() + " contours");

				    Rect largestBox = null;
				    MatOfPoint largestContour = null;
				    double largestWidth = 0;
				    for(MatOfPoint contour: contours){
				    	Rect box = Imgproc.boundingRect(contour);
				    	if(box.width > largestWidth){
				    		largestWidth = box.width;//* box.height;
				    		largestBox = box;
				    		largestContour = contour;
				    	}

				    }

				    if(largestBox != null){

				    	Imgproc.rectangle(frame, new Point(largestBox.x, largestBox.y), new Point(largestBox.x + largestBox.width, largestBox.y + largestBox.height), new Scalar(0,255,0));

				    	double screenWidth = frame.width();

			    		double centerOfMass = largestBox.x + (largestBox.width / 2);
			    		double offset = centerOfMass - (screenWidth / 2);

			    		System.out.println("Target Offset " + offset);

			    		double fov = 70;//56;//73;
			    		double ratio = fov / screenWidth;
			    		double rotation = -1 * offset * ratio;

					    Target target = new Target(rotation, 0d, 0d);
					    TargetManager.getInstance().setTarget(target);

				    }

				    imageToShow = mat2Image(frame);

				    this.sendImage(frame);

				}

			}
			catch (Exception e)
			{
				// log the error
				System.err.println("Exception during the frame elaboration: " + e);
			}
		}



		return imageToShow;
	}

	/**
	 * Compute and show the histogram for the given {@link Mat} image
	 *
	 * @param frame
	 *            the {@link Mat} image for which compute the histogram
	 * @param gray
	 *            is a grayscale image?
	 */
	private void showHistogram(Mat frame, boolean gray)
	{
		// split the frames in multiple images
		List<Mat> images = new ArrayList<Mat>();
		Core.split(frame, images);

		// set the number of bins at 256
		MatOfInt histSize = new MatOfInt(256);
		// only one channel
		MatOfInt channels = new MatOfInt(0);
		// set the ranges
		MatOfFloat histRange = new MatOfFloat(0, 256);

		// compute the histograms for the B, G and R components
		Mat hist_b = new Mat();
		Mat hist_g = new Mat();
		Mat hist_r = new Mat();

		// B component or gray image
		Imgproc.calcHist(images.subList(0, 1), channels, new Mat(), hist_b, histSize, histRange, false);

		// G and R components (if the image is not in gray scale)
		if (!gray)
		{
			Imgproc.calcHist(images.subList(1, 2), channels, new Mat(), hist_g, histSize, histRange, false);
			Imgproc.calcHist(images.subList(2, 3), channels, new Mat(), hist_r, histSize, histRange, false);
		}

		// draw the histogram
		int hist_w = 150; // width of the histogram image
		int hist_h = 150; // height of the histogram image
		int bin_w = (int) Math.round(hist_w / histSize.get(0, 0)[0]);

		Mat histImage = new Mat(hist_h, hist_w, CvType.CV_8UC3, new Scalar(0, 0, 0));
		// normalize the result to [0, histImage.rows()]
		Core.normalize(hist_b, hist_b, 0, histImage.rows(), Core.NORM_MINMAX, -1, new Mat());

		// for G and R components
		if (!gray)
		{
			Core.normalize(hist_g, hist_g, 0, histImage.rows(), Core.NORM_MINMAX, -1, new Mat());
			Core.normalize(hist_r, hist_r, 0, histImage.rows(), Core.NORM_MINMAX, -1, new Mat());
		}

		// effectively draw the histogram(s)
		for (int i = 1; i < histSize.get(0, 0)[0]; i++)
		{
			// B component or gray image
			Imgproc.line(histImage, new Point(bin_w * (i - 1), hist_h - Math.round(hist_b.get(i - 1, 0)[0])),
					new Point(bin_w * (i), hist_h - Math.round(hist_b.get(i, 0)[0])), new Scalar(255, 0, 0), 2, 8, 0);
			// G and R components (if the image is not in gray scale)
			if (!gray)
			{
				Imgproc.line(histImage, new Point(bin_w * (i - 1), hist_h - Math.round(hist_g.get(i - 1, 0)[0])),
						new Point(bin_w * (i), hist_h - Math.round(hist_g.get(i, 0)[0])), new Scalar(0, 255, 0), 2, 8,
						0);
				Imgproc.line(histImage, new Point(bin_w * (i - 1), hist_h - Math.round(hist_r.get(i - 1, 0)[0])),
						new Point(bin_w * (i), hist_h - Math.round(hist_r.get(i, 0)[0])), new Scalar(0, 0, 255), 2, 8,
						0);
			}
		}

		// display the histogram...
		Image histImg = mat2Image(histImage);
		this.histogram.setImage(histImg);

	}

	/**
	 * Convert a Mat object (OpenCV) in the corresponding Image for JavaFX
	 *
	 * @param frame
	 *            the {@link Mat} representing the current frame
	 * @return the {@link Image} to show
	 */
	private Image mat2Image(Mat frame)
	{
		// create a temporary buffer
		MatOfByte buffer = new MatOfByte();
		// encode the frame in the buffer, according to the PNG format
		Imgcodecs.imencode(".png", frame, buffer);
		// build and return an Image created from the image encoded in the
		// buffer

		byte[] bytes = buffer.toArray();

		InputStream in = new ByteArrayInputStream(bytes);


		return new Image(new ByteArrayInputStream(buffer.toArray()));
	}


	private void sendImage(Mat frame)
	{
		// create a temporary buffer
		MatOfByte buffer = new MatOfByte();
		// encode the frame in the buffer, according to the PNG format
		Imgcodecs.imencode(".png", frame, buffer);
		// build and return an Image created from the image encoded in the
		// buffer

		byte[] bytes = buffer.toArray();

		ImageManager.getInstance().setImage(bytes);

	}


}
