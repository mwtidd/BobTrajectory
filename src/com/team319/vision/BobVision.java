package com.team319.vision;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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

import javafx.fxml.FXML;
import javafx.scene.image.Image;

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
public class BobVision
{


	// a timer for acquiring the video stream
	private ScheduledExecutorService timer;
	// the OpenCV object that realizes the video capture
	private VideoCapture capture;
	// a flag to change the button behavior
	private boolean cameraActive;
	// the logo to be loaded
	private Mat logo;

	private static BobVision instance = null;

	private BobVision(){
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		initialize();
	}

	public static BobVision getInstance(){
		if(instance == null){
			instance = new BobVision();
		}
		return instance;
	}



	/**
	 * Initialize method, automatically called by @{link FXMLLoader}
	 */
	public void initialize()
	{
		this.capture = new VideoCapture();
		this.cameraActive = false;
	}

	/**
	 * The action triggered by pushing the button on the GUI
	 */
	@FXML
	public void startCamera(int camera)
	{
		// set a fixed width for the frame
		//this.currentFrame.setFitWidth(600);
		// preserve image ratio
		//this.currentFrame.setPreserveRatio(true);

		if (!this.cameraActive)
		{
			// start the video capture
			this.capture.open(camera);

			this.capture.set(Videoio.CAP_PROP_AUTO_EXPOSURE, 1);
			this.capture.set(Videoio.CAP_PROP_EXPOSURE, 100);

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
					}
				};

				this.timer = Executors.newSingleThreadScheduledExecutor();
				this.timer.scheduleAtFixedRate(frameGrabber, 0, 500, TimeUnit.MILLISECONDS);
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
			//this.currentFrame.setImage(null);
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


					// convert the Mat object (OpenCV) to Image (JavaFX)
					//imageToShow = mat2Image(frame);

					//Imgcodecs.imwrite("original.png", frame);
					//BGR
				    Core.inRange(frame, new Scalar(20,0,0), new Scalar(255,50,50), frame);

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
					imageToShow = mat2Image(frame);

				    Imgproc.findContours(frame, contours, hierarchy, Imgproc.RETR_EXTERNAL , Imgproc.CHAIN_APPROX_SIMPLE);

				    System.out.println("Found " + contours.size() + " contours");

				    Rect largestBox = null;
				    MatOfPoint largestContour = null;
				    double largestWidth = 0;
				    for(MatOfPoint contour: contours){
				    	Rect box = Imgproc.boundingRect(contour);
				    	if(box.width > largestWidth && box.width >= box.height){
				    		largestWidth = box.width;
				    		largestBox = box;
				    		largestContour = contour;
				    	}

				    }

				    if(largestBox != null){
				    	double screenWidth = frame.width();

			    		double centerOfMass = largestBox.x + (largestBox.width / 2);
			    		double offset = centerOfMass - (screenWidth / 2);

			    		double fov = 70;//56;//73;
			    		double ratio = fov / screenWidth;
			    		double rotation = -1 * offset * ratio;

					    Target target = new Target(rotation, 0d, 0d);
					    TargetManager.getInstance().setTarget(target);
					    System.out.println("Target Offset " + rotation);
				    }

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
		return new Image(new ByteArrayInputStream(buffer.toArray()));
	}


}
