package com.group.UtensilsRecognition;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamDiscoveryEvent;
import com.github.sarxos.webcam.WebcamDiscoveryListener;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;

public class WebcamManager {

	private Webcam thewebcam;
	private JPanel display;
	private JLabel WarningImage;
	private JPanel ImagePanel;
	
	
	public WebcamManager()
	{
		display = new JPanel();
		
		System.out.print("these webcams have been detected\n");
		for (Webcam webcam : Webcam.getWebcams())
			System.out.println("Webcam detected: " + webcam.getName());
		
		WarningImage = new JLabel(new ImageIcon("CameraNotFoundImage.png"));		
		Webcam.addDiscoveryListener(new WebcamDiscovery());		
		SetWebCamPanel();
	}
	
	/** Will check if any webcams have been connected or disconnected and take appropriate measures
	 * to ensure everything remains working */
	private class WebcamDiscovery implements WebcamDiscoveryListener
	{
    	@Override
    	public void webcamFound(WebcamDiscoveryEvent event) {
    		System.out.format("Webcam connected: %s \n", event.getWebcam().getName());
    		SetWebCamPanel();
    	}

    	@Override
    	public void webcamGone(WebcamDiscoveryEvent event) {
    		System.out.format("Webcam disconnected: %s \n", event.getWebcam().getName());
    		//clears anything leftover from webcam session
    		thewebcam.close();
    		SetWebCamPanel();
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
		System.out.print("Guess what\n"); //Extremely important, will explode without this
		
		//removes whatever display is currently being shown
		display.removeAll();
		
		//if there are no webcams detected, will set display to error image
		if (Webcam.getWebcams().size() == 0)
		{
			ImagePanel = new JPanel();
			ImagePanel.add(WarningImage);
			display.add(ImagePanel);
		}
		
		//if there are webcam(s) detected, will grab the default webcam
		else
		{				
			thewebcam = Webcam.getDefault();
			System.out.print("getting camera" + thewebcam.getName() + "\n");
			thewebcam.setViewSize(WebcamResolution.VGA.getSize());
			//creates the panel with a view of the webcam
			WebcamPanel TheWebCamPanel = new WebcamPanel(thewebcam);
			TheWebCamPanel.setFPSDisplayed(true);
			TheWebCamPanel.setDisplayDebugInfo(true);
			TheWebCamPanel.setImageSizeDisplayed(true);
			TheWebCamPanel.setMirrored(true);
			display.add(TheWebCamPanel);
		}
		
		//finalizes display stuff
		display.validate();
		
		System.out.print("chicken butt\n"); //Extremely important, will explode without this
	}
}
