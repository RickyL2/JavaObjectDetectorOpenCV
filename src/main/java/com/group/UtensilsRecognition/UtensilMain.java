package com.group.UtensilsRecognition;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import nu.pattern.OpenCV;

public class UtensilMain{

	private static WebcamManagerV2 webcam;
	private static UtensilRecognitionUI frame;
	private static OpenCVObjectDetector objectDetector;
	
	public static void main(String[] args)
	{		
		//OpenCV.loadShared();
		OpenCV.loadLocally();
		webcam = new WebcamManagerV2();
		frame = new UtensilRecognitionUI(webcam.GetWebCamPanel());
		frame.setVisible(true);
		objectDetector = new OpenCVObjectDetector();
		
		Thread t = new Thread() {

			@Override
			public void run() {
				while(true)
				{
					if(webcam.IsCameraConnected() && frame.ShouldRun())
					{
						BufferedImage currentFrame = webcam.takePhoto();
						List<DetectedObject> foundObjects = objectDetector.FindObjects(currentFrame);
						webcam.SetDetectedObjectList(foundObjects);
					}
					try {
						Thread.sleep(40);
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
