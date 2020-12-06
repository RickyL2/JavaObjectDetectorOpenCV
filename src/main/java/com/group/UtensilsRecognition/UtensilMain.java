package com.group.UtensilsRecognition;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import nu.pattern.OpenCV;

public class UtensilMain{

	private static WebcamManagerV2 webcam;
	private static UtensilRecognitionUI frame;
	private static OpenCVObjectDetector objectDetector;
	
	private static int fpsForObjectDetection = 20;
	
	public static void main(String[] args)
	{
		//loads opencv stuff
		OpenCV.loadLocally();
		
		webcam = new WebcamManagerV2();
		frame = new UtensilRecognitionUI(webcam.GetWebCamPanel());
		frame.setVisible(true);
		objectDetector = new OpenCVObjectDetector();
		
		int timeBetweenEachDetection = 1000/fpsForObjectDetection;
		
		//creates thread that will manage object detection
		Thread t = new Thread() {

			@Override
			public void run() {
				while(true)
				{
					//check if list of classifiers have been updated
					if(frame.updatedClassifierListAvailable())
					{
						//gets updates list
						ArrayList<CascadeClassifierProperties> newClassifiers = frame.getUpdatedListOfClassifiers();
						//sends new list to object detector
						objectDetector.setToNewClassifiers(newClassifiers);
					}
					
					//checks if camera is connected and if object recognition should even be running based on ui
					if(webcam.IsCameraConnected() && frame.ShouldRun())
					{
						//take a photo from webcam
						BufferedImage currentFrame = webcam.takePhoto();
						//give photo to objectDetector and get list of detected objects
						List<DetectedObject> foundObjects = objectDetector.FindObjects(currentFrame);
						//gives list to webcam so webcam can draw boxes around found objects
						webcam.SetDetectedObjectList(foundObjects);
					}
					
					//if object recognition not running, clears list so that any drawn boxes are removed
					else
						webcam.SetDetectedObjectList(null);
					
					//delays next object detection by certain amount of time. without it, will be doing this as
					//many times as it possibly can which is quite excessive and makes app quite laggy
					try {
						Thread.sleep(timeBetweenEachDetection);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}				
			}
		};
		t.setName("objectDetectionLoop");
		t.setDaemon(true);
		t.start();
	}
	
}
