package com.group.UtensilsRecognition;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.event.ItemEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamDevice;
import com.github.sarxos.webcam.WebcamDiscoveryEvent;
import com.github.sarxos.webcam.WebcamDiscoveryListener;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.github.sarxos.webcam.ds.dummy.WebcamDummyDevice;

public class WebcamManager {

	private Webcam thewebcam;
	
	private JPanel display;
	private BufferedImage warningPic;
	private JLabel WarningImage;
	private Boolean cameraConnected;
	WebcamPanel TheWebCamPanel = null;
	
	
	public WebcamManager()
	{
//		Webcam.setDriver(new WebcamDummyDriver(1));
//		carl = Webcam.getDefault();		
//		Webcam.setDriver(new WebcamDefaultDriver());
		display = new JPanel();
		cameraConnected = false;
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandlingForThread());
		
		System.out.print("these webcams have been detected\n");
		for (Webcam webcam : Webcam.getWebcams())
			System.out.println("Webcam detected: " + webcam.getName());
		
		GetWarningPic();
		Image dimg = warningPic.getScaledInstance(1280, 960,
				Image.SCALE_SMOOTH);
		WarningImage = new JLabel(new ImageIcon(dimg));
		Webcam.addDiscoveryListener(new WebcamDiscovery());
		SetWebCamPanel();
	}
	
	private void GetWarningPic()
	{
		try
		{
			warningPic = ImageIO.read(new File("CameraNotFoundImage.png"));
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			warningPic = new BufferedImage(640, 480, BufferedImage.TYPE_INT_RGB);
		}
	}
	
	class ExceptionHandlingForThread implements Thread.UncaughtExceptionHandler
	{  
	    // Method to handle the 
	    // uncaught exception 
	    public void uncaughtException(Thread t, Throwable e) 
	    { 
	  
	        // Custom task that needs to be 
	        // performed when an exception occurs 
	    	System.err.println(String.format("Exception in thread %s", t.getName()));
			e.printStackTrace();
		}
    }
	/** Will check if any webcams have been connected or disconnected and take appropriate measures
	 * to ensure everything remains working */
	private class WebcamDiscovery implements WebcamDiscoveryListener
	{
    	@Override
    	public void webcamFound(WebcamDiscoveryEvent event) {
    		// starting time 
            long start = System.currentTimeMillis();
            
    		System.out.format("Webcam connected: %s \n", event.getWebcam().getName());
    		
    		if (Webcam.getWebcams().size() > 0)
    		{
    			display.removeAll();
    			TheWebCamPanel.stop();
    			//clears anything leftover from webcam session
        		thewebcam.close();
    		}
    		
    		SetWebCamPanel();
    		
    		
    		
    		// ending time
    		long end = System.currentTimeMillis();
            System.out.println("conection takes " + 
                                        (end - start) + "ms");
    	}

    	@Override
    	public void webcamGone(WebcamDiscoveryEvent event) {
    		// starting time 
            long start = System.currentTimeMillis();
            
    		System.out.format("Webcam disconnected: %s \n", event.getWebcam().getName());
    		TheWebCamPanel.stop();
    		SetWebCamPanel();
    		//clears anything leftover from webcam session
    		thewebcam.close();
    		
    		// ending time
    		long end = System.currentTimeMillis();
            System.out.println("disconection takes " + 
                                        (end - start) + "ms");
    	}
    }
	
	/** will return a JPanel with a view of either the webcam or an error image */
	public JPanel GetWebCamPanel()
	{
		return display;
	}
	
	/** corrects the panel that will be shown to the user */
	private void SetWebCamPanel()
	{
		// starting time 
        long start = System.currentTimeMillis();
        
        //removes whatever display is currently being shown and creates a new layout
		//layout is needed so view can scale
		display.removeAll();
		display.setLayout(new BorderLayout());
		
		System.out.print("Guess what\n"); //Extremely important, will explode without this
		
		// ending time
        long partOne = System.currentTimeMillis(); 
        System.out.println("clearing panels takes " + 
                                    (partOne - start) + "ms");
		
		cameraConnected = false;
		
		//if there are no webcams detected, will set display to error image
		if (Webcam.getWebcams().size() == 0)
		{
			
			display.add(WarningImage, BorderLayout.CENTER);
			//display.add(new WebcamPanel(new WebcamDummyDevice(1).), BorderLayout.CENTER);
		}
		
		//if there are webcam(s) detected, will connect to webcam and setup panel
		else
		{
			GetCamera();
			
			//creates the panel with a view of the webcam
			TheWebCamPanel = new WebcamPanel(thewebcam, false);
			
			Thread t = new Thread() {

				@Override
				public void run() {
					TheWebCamPanel.start();
				}
			};
			t.setName("example-starter");
			t.setDaemon(true);
			t.start();
			
			TheWebCamPanel.setFPSDisplayed(true);
			TheWebCamPanel.setDisplayDebugInfo(true);
			TheWebCamPanel.setImageSizeDisplayed(true);
			TheWebCamPanel.setMirrored(true);			
	        
			display.add(TheWebCamPanel, BorderLayout.CENTER);
			
			// ending time
	        long partTwo = System.currentTimeMillis(); 
	        System.out.println("creating webcam view takes " + 
	                                    (partTwo - start) + "ms");
		}
		
		//finalizes display stuff
		display.validate();
		display.repaint();
		
		
		// ending time
        long end = System.currentTimeMillis(); 
        System.out.println("switch func takes " + 
                                    (end - start) + "ms");
	}
	
	
	private void GetCamera()
	{
		cameraConnected = true;
		thewebcam = Webcam.getDefault();
		System.out.print("getting camera" + thewebcam.getName() + "\n");
		thewebcam.setViewSize(WebcamResolution.VGA.getSize());
		
		
	}
	
	public Boolean IsCameraConnected()
	{
		return cameraConnected;
	}
	
	public BufferedImage takePhoto()
	{
		if(IsCameraConnected())
			return thewebcam.getImage();
		else
			return warningPic;
	}
}
