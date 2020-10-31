package com.group.UtensilsRecognition;

import javax.swing.JFrame;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamEvent;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamListener;
import com.github.sarxos.webcam.WebcamPicker;
import com.github.sarxos.webcam.WebcamResolution;


public class UtensilMain{

	private static Webcam webcam = null;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		webcam = Webcam.getDefault();
		
		if (webcam == null) {
			System.out.println("No webcams found...");
			System.exit(1);
		}
		
		webcam.setViewSize(WebcamResolution.VGA.getSize());
		System.out.print(WebcamResolution.VGA.getSize());
		WebcamPanel TheWebCamPanel = new WebcamPanel(webcam);
		TheWebCamPanel.setFPSDisplayed(true);
		TheWebCamPanel.setDisplayDebugInfo(true);
		TheWebCamPanel.setImageSizeDisplayed(true);
		TheWebCamPanel.setMirrored(true);
		
		UtensilRecognitionUI frame = new UtensilRecognitionUI(TheWebCamPanel);
		frame.setVisible(true);		
	}
	
}
