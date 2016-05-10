package com.team319.vision;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import com.team319.auto.AutoManager;
import com.team319.vision.config.IVisionConfigChangeListener;
import com.team319.vision.config.VisionConfig;
import com.team319.vision.config.VisionConfigManager;
import com.team319.vision.config.VisionRgb;
import com.team319.vision.image.ImageManager;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import javafx.scene.image.Image;

public class CameraController implements IVisionConfigChangeListener{
	// a timer for acquiring the video stream
 	private ScheduledExecutorService timer;
 	// the OpenCV object that realizes the video capture
 	private VideoCapture capture;

 	private int cameraNumber = 0;

 	public CameraController(){
 		cameraNumber = VisionConfigManager.getInstance().getConfig().getCamera().getNumber();
 		VisionConfigManager.getInstance().registerListener(this);
 	}

 	public void initialize(){
 		capture = new VideoCapture();
 	}

 	/**
 	 * The action triggered by pushing the button on the GUI
 	 */
 	public void startCamera()
 	{
		// start the video capture
		//this.capture.open(0);
		capture.open(cameraNumber);



		// is the video stream available?
		if (capture.isOpened())
		{
			// grab a frame every 33 ms (30 frames/sec)
			Runnable frameGrabber = new Runnable() {

				@Override
				public void run()
				{
					grabFrame();
				}
			};

			timer = Executors.newSingleThreadScheduledExecutor();
			timer.scheduleAtFixedRate(frameGrabber, 0, 500, TimeUnit.MILLISECONDS);


		}
		else
		{
			// log the error
			System.err.println("Impossible to open the camera connection...");
		}
 	}

 	/**
 	 * Get a frame from the opened video stream (if any)
 	 *
 	 * @return the {@link Image} to show
 	 */
 	private void grabFrame()
 	{
 		// init everything
 		Mat frame = new Mat();


 		// check if the capture is open
 		if (capture.isOpened())
 		{
 			try
 			{
 				// read the current frame
 				capture.read(frame);

 				// if the frame is not empty, process it
 				if (!frame.empty())
 				{

 					// convert the Mat object (OpenCV) to Image (JavaFX)

 					Mat filteredFrame = new Mat();

 					//Imgcodecs.imwrite("original.png", frame);
 					//BGR

 					VisionRgb rgb;

 					if(AutoManager.getInstance().isOnRed()){
 						rgb = VisionConfigManager.getInstance().getConfig().getRedLight();
 					}else{
 						rgb = VisionConfigManager.getInstance().getConfig().getBlueLight();
 					}

 					Core.inRange(frame, new Scalar(rgb.getBlueMin(),rgb.getGreenMin(),rgb.getRedMin()), new Scalar(rgb.getBlueMax(),rgb.getGreenMax(),rgb.getRedMax()), filteredFrame);

 				    //Core.inRange(frame, new Scalar(0,0,20), new Scalar(0,0,255), frame);


 				    List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
 				    Mat hierarchy = new Mat();


 				    //remove noise
 				    int size = 5;
 				    Imgproc.erode(frame, frame, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(size,size)));
 				    Imgproc.dilate(frame, frame, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(size,size)));
 				    /**
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

 				    Mat countourFrame = filteredFrame.clone();

 				    Imgproc.findContours(countourFrame, contours, hierarchy, Imgproc.RETR_EXTERNAL , Imgproc.CHAIN_APPROX_SIMPLE);

 				    System.out.println("Found " + contours.size() + " contours");

 				    Rect largestBox = null;
 				    double largestWidth = 0;
 				    for(MatOfPoint contour: contours){
 				    	Rect box = Imgproc.boundingRect(contour);
 				    	if(box.width < VisionConfigManager.getInstance().getConfig().getCamera().getMinTargetWidth()){

 				    	}else if(box.height > VisionConfigManager.getInstance().getConfig().getCamera().getMaxTargetWidth()){

 				    	}else if(box.width > largestWidth){
 				    		largestWidth = box.width;//* box.height;
 				    		largestBox = box;
 				    	}

 				    }


 				    if(largestBox != null){

 				    	Imgproc.rectangle(frame, new Point(largestBox.x, largestBox.y), new Point(largestBox.x + largestBox.width, largestBox.y + largestBox.height), new Scalar(0,255,0));

 				    	Imgproc.rectangle(filteredFrame, new Point(largestBox.x, largestBox.y), new Point(largestBox.x + largestBox.width, largestBox.y + largestBox.height), new Scalar(0,255,0));

 				    	/*
 				    	double screenWidth = frame.width();

 			    		double centerOfMass = largestBox.x + (largestBox.width / 2);
 			    		double offset = centerOfMass - (screenWidth / 2);

 			    		System.out.println("Target Offset " + offset);

 			    		double fov = 70;//56;//73;
 			    		double ratio = fov / screenWidth;
 			    		double rotation = -1 * offset * ratio;
 				    	*/
 				    	double verticalDegrees = 0;
 				    	double horizontalDegrees = 0;
 				    	double distance = 0;

 				    	{
 				    		double screenWidth = frame.width();

 				    		double centerOfMass = largestBox.x + (largestBox.width / 2);
 				    		double offset = centerOfMass - (screenWidth / 2 );

 				    		System.out.println("Target x Offset " + offset);

 				    		double fov = 70;//56;//73;
 	 			    		double ratio = fov / screenWidth;
 	 			    		double rotation = -1 * offset * ratio;

 	 			    		horizontalDegrees = rotation;
 				    	}

 				    	{
 				    		double screenHeight = frame.height();

 				    		double y  = largestBox.y - largestBox.height;

 				    		double offset = y - (screenHeight / 2);

 				    		//SmartDashboard.putNumber("Target Y Offset", offset);
 				    		double fov = 43.3;
 				    		double ratio = fov / screenHeight;
 				    		double rotation = -1 * offset * ratio;

 				    		//SmartDashboard.putNumber("Target Y Rotation", rotation );
 				    		double posRatio = (largestBox.y * 1d )/(frame.height() * 1d);

 				    		verticalDegrees = posRatio;

 				    		System.out.println("Target Y Offset " + posRatio);
 				    	}

 				    	{
 				    		double ratio = (largestBox.width * 1d) / (frame.width() * 1d);
 				    		System.out.println("Target Width " + ratio);
 				    		distance = ratio;
 				    	}




 					    Target target = new Target(horizontalDegrees, verticalDegrees, distance);
 					    TargetManager.getInstance().setTarget(target);

 				    }

 				   if(VisionConfigManager.getInstance().getConfig().getCamera().isFilterImage()){
 	 				   sendImage(filteredFrame);
				    }else{
 	 				   sendImage(frame);
				    }




 				}

 			}
 			catch (Exception e)
 			{
 				// log the error
 				System.err.println("Exception during the frame elaboration: " + e);
 			}
 		}
 	}


 	private static void sendImage(Mat frame)
 	{

 		Mat flippedImage = new Mat();
 		Core.flip(frame, flippedImage, -1);

 		Mat resizedImage = new Mat();
 		Size size = new Size(320, 240);
 		Imgproc.resize(flippedImage, resizedImage, size);

 		// create a temporary buffer
 		MatOfByte buffer = new MatOfByte();
 		// encode the frame in the buffer, according to the PNG format
 		MatOfInt  compression = new MatOfInt(Imgcodecs.IMWRITE_PNG_COMPRESSION, 5);
 		Imgcodecs.imencode(".png", resizedImage, buffer, compression);
 		// build and return an Image created from the image encoded in the
 		// buffer

 		byte[] bytes = buffer.toArray();

 		ImageManager.getInstance().setImage(bytes);

 	}

 	@Override
 	public void onChange(VisionConfig config) {
 		if(config.getCamera().getNumber() != cameraNumber){
 			cameraNumber = config.getCamera().getNumber();
 			//camera number changed
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

			startCamera();
 		}

 		capture.set(Videoio.CAP_PROP_FRAME_WIDTH, 640);
 		capture.set(Videoio.CAP_PROP_FRAME_HEIGHT, 480);

 		//capture.set(Videoio.CAP_PROP_FRAME_WIDTH, 320);
 		//capture.set(Videoio.CAP_PROP_FRAME_HEIGHT, 240);

 		capture.set(Videoio.CAP_PROP_AUTO_EXPOSURE, VisionConfigManager.getInstance().getConfig().getCamera().getAutoExposure());
 		capture.set(Videoio.CAP_PROP_EXPOSURE, VisionConfigManager.getInstance().getConfig().getCamera().getExposure());

 		capture.set(Videoio.CAP_PROP_BRIGHTNESS, VisionConfigManager.getInstance().getConfig().getCamera().getBrightness());
 		capture.set(Videoio.CAP_PROP_CONTRAST, VisionConfigManager.getInstance().getConfig().getCamera().getContrast());
 		capture.set(Videoio.CAP_PROP_SATURATION, VisionConfigManager.getInstance().getConfig().getCamera().getSaturation());
 		capture.set(Videoio.CAP_PROP_GAIN, VisionConfigManager.getInstance().getConfig().getCamera().getGain());
 		capture.set(Videoio.CAP_PROP_SHARPNESS, VisionConfigManager.getInstance().getConfig().getCamera().getSharpness());

 		if(VisionConfigManager.getInstance().getConfig().getCamera().isAutoFocus()){
 			capture.set(Videoio.CAP_PROP_AUTOFOCUS, 1);
 		}else{
 			capture.set(Videoio.CAP_PROP_AUTOFOCUS, 0);
 		}
 		capture.set(Videoio.CAP_PROP_FOCUS, VisionConfigManager.getInstance().getConfig().getCamera().getFocus());
 	}

}
