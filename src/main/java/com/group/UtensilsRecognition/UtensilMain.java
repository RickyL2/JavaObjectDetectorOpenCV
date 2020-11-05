package com.group.UtensilsRecognition;

import javax.swing.JFrame;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamEvent;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamListener;
import com.github.sarxos.webcam.WebcamPicker;
import com.github.sarxos.webcam.WebcamResolution;


public class UtensilMain{

	private static WebcamManager webcam;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		webcam = new WebcamManager();
		
		UtensilRecognitionUI frame = new UtensilRecognitionUI(webcam.GetWebCamPanel());
		frame.setVisible(true);
	}
	
}
