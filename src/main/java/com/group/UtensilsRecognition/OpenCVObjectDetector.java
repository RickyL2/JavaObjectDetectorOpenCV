package com.group.UtensilsRecognition;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.List;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.objdetect.CascadeClassifier;

public class OpenCVObjectDetector {

	private List<CascadeClassifier> ccs = null;
	//lists that will contain properties that relate to each cascade classifier
	private List<String> ccsLabels = null;
	private List<Float> ccsWidths = null;
	private List<Color> ccsColors = null;
	
	private boolean DEBUG_MODE = true;
	
	/**
	 * Takes in the lists of values to produce multiple cascade classifiers
	 * @param xmlNames: list of strings with each string being the full name of the xml file
	 * @param widths: list of floats representing the widths of the bounding boxes for each cascade classifier
	 * @param colors: list of colors for each of the bounding boxes
	 */
	public OpenCVObjectDetector(List<String> xmlNames, List<Float> widths, List<Color> colors)
	{
		CreateLists();
		
		//creates each classifier and adds the properties to their corresponding list
		for(int i = 0;  i < xmlNames.size() && i < xmlNames.size() &&  i < xmlNames.size(); i++)
		{
			ccs.add(new CascadeClassifier(xmlNames.get(i)));
			ccsLabels.add(xmlNames.get(i).substring(0, xmlNames.get(i).length() - 4));
			ccsWidths.add(widths.get(i));
			ccsColors.add(colors.get(i));
		}
	}
	
	/**
	 * if parameters that are given are for a single cascade classifier, will only create one
	 * @param xmlName: the name of the cascade classifier file
	 * @param width: the desired width of the bounding box
	 * @param color: the desired color of the bounding box
	 */
	public OpenCVObjectDetector(String xmlName, float width, Color color)
	{
		CreateLists();
		
		//creates new cascade classifier and saves properties to lists
		ccs.add(new CascadeClassifier(xmlName));
		ccsLabels.add(xmlName.substring(0, xmlName.length() - 4));
		ccsWidths.add(width);
		ccsColors.add(color);		
	}
	
	/** Initializes the list variables */
	private void CreateLists()
	{
		ccs = new ArrayList<CascadeClassifier>();
		ccsLabels = new ArrayList<String>();
		ccsWidths = new ArrayList<Float>();
		ccsColors = new ArrayList<Color>();
	}
	
	/**
	 * Detects objects within the provided image using the multiple cascade classifiers that were previously saved
	 * @param temp: bufferedimage that will be checked for objects
	 * @return list of detected objects that were found within the image
	 */
	public List<DetectedObject> FindObjects(BufferedImage temp)
	{
		//list of lists that will store a list of detected objects for each cascade classifier
		List<List<DetectedObject>> foundObjects = new ArrayList<List<DetectedObject>>();
		//list of threads just to keep track of them
		List<Thread> threads = new ArrayList<Thread>();
		//final list that will make up complete list of all found objects
		List<DetectedObject> totalFoundObjects = new ArrayList<DetectedObject>();
		
		//get image and convert it to usable format
		Mat src = bufferedImageToMat(temp);
		
		//Creates a list for the future detected objects of each classifier
		for(int i = 0; i < ccs.size(); i++)
		{
			foundObjects.add(new ArrayList<DetectedObject>());
		}
		
		//will go through each cascade classifier and initiate a thread
		for(int i = 0; i < ccs.size(); i++)
		{
			//needs a separate local variable for i because it is going to have a different value for each thread
			int w = i;
			Thread t = new Thread() {

				@Override
				public void run() {
					//detects objects
					MatOfRect objDetection = new MatOfRect();
					ccs.get(w).detectMultiScale(src, objDetection);
					//just for keeping track of which object we on
					int r = 0;
					//saves the detected objects as DetectedObjects instances and places them into their foundObjects list
					for(Rect rect: objDetection.toArray())
					{
						if(DEBUG_MODE)
							System.out.println( ccsLabels.get(w) + (r++) + " found at " + rect.x + "," + rect.y);
						
						Rectangle2D.Double box = new Rectangle2D.Double(src.width() - rect.x - rect.width, rect.y, rect.width, rect.height);
						DetectedObject temp = new DetectedObject(ccsWidths.get(w), ccsColors.get(w),
																	box, ccsLabels.get(w));
						foundObjects.get(w).add(temp);
					}
					
				}
			};
			t.setName("openCV_objectDetection" + w);
			t.setDaemon(true);
			t.start();
			threads.add(t);
		}
		
		//joins all the threads so will not continue until all thread operations in list are complete
		for (Thread thread : threads) {
			  try {
				thread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//places all the found objects into just one list
		for(List<DetectedObject> list: foundObjects)
		{
			for(DetectedObject obj: list)
			{
				totalFoundObjects.add(obj);
			}
		}
		
		return totalFoundObjects;
	}
	
	//converts the buffered image into a Mat that is able to be used for object detection
	//source: https://answers.opencv.org/question/28348/converting-bufferedimage-to-mat-in-java/
	public Mat bufferedImageToMat(BufferedImage sourceImg) {

	    long millis = System.currentTimeMillis();

	    DataBuffer dataBuffer = sourceImg.getRaster().getDataBuffer();
	    byte[] imgPixels = null;
	    Mat imgMat = null;

	    int width = sourceImg.getWidth();
	    int height = sourceImg.getHeight();

	    if(dataBuffer instanceof DataBufferByte) {      
	            imgPixels = ((DataBufferByte)dataBuffer).getData();
	    }

	    if(dataBuffer instanceof DataBufferInt) {

	        int byteSize = width * height;      
	        imgPixels = new byte[byteSize*3];

	        int[] imgIntegerPixels = ((DataBufferInt)dataBuffer).getData();

	        for(int p = 0; p < byteSize; p++) {         
	            imgPixels[p*3 + 0] = (byte) ((imgIntegerPixels[p] & 0x00FF0000) >> 16);
	            imgPixels[p*3 + 1] = (byte) ((imgIntegerPixels[p] & 0x0000FF00) >> 8);
	            imgPixels[p*3 + 2] = (byte) (imgIntegerPixels[p] & 0x000000FF);
	        }
	    }

	    if(imgPixels != null) {
	        imgMat = new Mat(height, width, CvType.CV_8UC3);
	        imgMat.put(0, 0, imgPixels);
	    }

	    return imgMat;
	}
	
}
