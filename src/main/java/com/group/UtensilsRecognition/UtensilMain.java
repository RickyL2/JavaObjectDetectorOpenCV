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
	
	public static List<String> xmlNames = new ArrayList<String>();
	public static List<Float> widths = new ArrayList<Float>();
	public static List<Color> colors = new ArrayList<Color>();
	
	public static void main(String[] args)
	{	
		//creates lists
		xmlNames.add("face.xml");
		widths.add(1f);
		colors.add(Color.RED);
		
		xmlNames.add("spoon.xml");
		widths.add(2f);
		colors.add(Color.BLUE);
		
		//OpenCV.loadShared();
		OpenCV.loadLocally();
		webcam = new WebcamManagerV2();
		frame = new UtensilRecognitionUI(webcam.GetWebCamPanel());
		frame.setVisible(true);
		//objectDetector = new OpenCVObjectDetector("face.xml", 1, Color.RED);
		objectDetector = new OpenCVObjectDetector(xmlNames, widths, colors);
		
		Thread t = new Thread() {

			@Override
			public void run() {
				while(true)
				{
					if(webcam.IsCameraConnected())
					{
						BufferedImage currentFrame = webcam.takePhoto();
						List<DetectedObject> foundObjects = objectDetector.FindObjects(currentFrame);
						webcam.SetDetectedObjectList(foundObjects);
					}
					try {
						Thread.sleep(125);
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
