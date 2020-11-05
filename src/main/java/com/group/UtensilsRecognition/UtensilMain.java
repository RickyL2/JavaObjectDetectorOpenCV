package com.group.UtensilsRecognition;

public class UtensilMain{

	private static WebcamManager webcam;
	private static UtensilRecognitionUI frame;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		webcam = new WebcamManager();
		
		frame = new UtensilRecognitionUI(webcam.GetWebCamPanel());
		frame.setVisible(true);
		
		
		
		
	}
	
}
