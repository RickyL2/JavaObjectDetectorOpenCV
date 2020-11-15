package com.group.UtensilsRecognition;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.tensorflow.Graph;
import org.tensorflow.Session;
import org.tensorflow.Tensor;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;

public class UtensilMain{

	private static WebcamManagerV2 webcam;
	private static UtensilRecognitionUI frame;
	
	//all this needs to put in ObjectPrediction class, to try out, replace string with file location on your pc
    private static String modelpath = "C:/Users/heror/Documents/inception_dec_2015/tensorflow_inception_graph.pb";
    private static String labelpath = "C:/Users/heror/Documents/inception_dec_2015/imagenet_comp_graph_label_strings.txt";
    private static byte[] graphDef;
    private static List<String> labels;
    private static BufferedImage pic;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		webcam = new WebcamManagerV2();
		//Webcam webcam = Webcam.getDefault();
		//webcam.setViewSize(WebcamResolution.VGA.getSize());
		
//		WebcamPanel TheWebCamPanel = new WebcamPanel(webcam);
//		TheWebCamPanel.setFPSDisplayed(true);
//		TheWebCamPanel.setDisplayDebugInfo(true);
//		TheWebCamPanel.setImageSizeDisplayed(true);
//		TheWebCamPanel.setMirrored(true);	
		frame = new UtensilRecognitionUI(webcam.GetWebCamPanel());
		//frame = new UtensilRecognitionUI(TheWebCamPanel);
		frame.setVisible(true);
		
		//everything below here is just for trying out the object detection tensorflow thing
		//this setup is only temporary
//		graphDef = readAllBytesOrExit(Paths.get(modelpath));
//		labels = readAllLinesOrExit(Paths.get(labelpath));
//		pic = webcam.takePhoto();
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		byte[] imageBytes = null;
//		try {
//			ImageIO.write( pic, "jpg", baos );
//			baos.flush();
//			imageBytes = baos.toByteArray();
//			baos.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//        try (Tensor image = Tensor.create(imageBytes)) 
//        {
//            float[] labelProbabilities = executeInceptionGraph(graphDef, image);
//            int bestLabelIdx = maxIndex(labelProbabilities);
//            frame.UpdateResults("");
//            frame.UpdateResults(String.format(
//                            "BEST MATCH: %s (%.2f%% likely)",
//                            labels.get(bestLabelIdx), labelProbabilities[bestLabelIdx] * 100f));
//            System.out.println(
//                String.format(
//                        "BEST MATCH: %s (%.2f%% likely)",
//                        labels.get(bestLabelIdx), labelProbabilities[bestLabelIdx] * 100f));
//            
//            for (int i = 0; i < labelProbabilities.length; i++)
//            {
//            	if(labelProbabilities[i] > 0.3)
//            	{
//            		System.out.println(labels.get(i) + " " + labelProbabilities[i] * 100f + "\n");
//            	}
//            }
//        }
//	}
//	
//	private static byte[] readAllBytesOrExit(Path path) {
//        try {
//            return Files.readAllBytes(path);
//        } catch (IOException e) {
//            System.err.println("Failed to read [" + path + "]: " + e.getMessage());
//            System.exit(1);
//        }
//        return null;
//	}
//	
//	private static List<String> readAllLinesOrExit(Path path) {
//        try {
//            return Files.readAllLines(path, Charset.forName("UTF-8"));
//        } catch (IOException e) {
//            System.err.println("Failed to read [" + path + "]: " + e.getMessage());
//            System.exit(0);
//        }
//        return null;
//    }
//	
//	private static float[] executeInceptionGraph(byte[] graphDef, Tensor image)
//    {
//        Graph g = new Graph();
//        g.importGraphDef(graphDef);
//        try (Session s = new Session(g);
//                Tensor result = s.runner().feed("DecodeJpeg/contents", image).fetch("softmax").run().get(0)) 
//        {
//            final long[] rshape = result.shape();
//            if (result.numDimensions() != 2 || rshape[0] != 1)
//            {
//                throw new RuntimeException(
//                        String.format(
//                                "Expected model to produce a [1 N] shaped tensor where N is the number of labels, instead it produced one with shape %s",
//                                Arrays.toString(rshape)));
//            }
//            int nlabels = (int) rshape[1];
//            return result.copyTo(new float[1][nlabels])[0];
//        }
//    }
//	
//	private static int maxIndex(float[] probabilities) {
//        int best = 0;
//        for (int i = 1; i < probabilities.length; ++i) {
//            if (probabilities[i] > probabilities[best]) {
//                best = i;
//            }
//        }
//        return best;
//    }
	}
	
}
