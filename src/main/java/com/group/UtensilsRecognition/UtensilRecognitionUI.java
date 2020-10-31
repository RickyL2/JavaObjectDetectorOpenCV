package com.group.UtensilsRecognition;

import com.esotericsoftware.tablelayout.swing.Table;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.tensorflow.DataType;
import org.tensorflow.Graph;
import org.tensorflow.Output;
import org.tensorflow.Session;
import org.tensorflow.Tensor;


public class UtensilRecognitionUI extends JFrame{
	
	private static final int FRAME_WIDTH = 540;
	private static final int FRAME_HEIGHT = 500;
	
	private String FrameName = "Utensil Recognition Software";
    private Table table;
    private JButton startpredict;
    private JButton modelImport;
    private JButton labelImport;
    
    
    private JFileChooser tensorflowmodel;
    private JFileChooser tensorflowlabels;
    private FileNameExtensionFilter tensorflowmodelfilter = new FileNameExtensionFilter(
            "PB TENSORFLOW MODELS", "pb");
    private FileNameExtensionFilter tensorflowlabelfilter = new FileNameExtensionFilter(
            "TXT FILE", "txt");
    private JTextField modelpth;
    private JTextField labelpth;
    
    private JPanel cameraView;
    
    private JTextField result;

    public UtensilRecognitionUI(JPanel cameraView) {
    	
    	this.cameraView = cameraView;
        setTitle(FrameName);
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        //setMinimumSize(new Dimension(500, 350));
        createButtons();
        createTable();
    }
    
    /**
	 * creates the buttons that will be used
	 */
	private void createButtons()
	{
		startpredict = new JButton("Start Identification");
		modelImport = new JButton("Choose Tensorflow Model");
	    labelImport = new JButton("Choose Tensorflow Labels");
	    
        startpredict.setEnabled(false);
        
        tensorflowmodel = new JFileChooser();
        tensorflowmodel.setFileFilter(tensorflowmodelfilter);
        tensorflowmodel.setFileSelectionMode(JFileChooser.FILES_ONLY);
        tensorflowlabels = new JFileChooser();
        tensorflowlabels.setFileFilter(tensorflowlabelfilter);
        tensorflowlabels.setFileSelectionMode(JFileChooser.FILES_ONLY);
        
        result = new JTextField();
        modelpth = new JTextField();
        modelpth.setEditable(false);
        labelpth = new JTextField();
        labelpth.setEditable(false);
        
        modelImport.addActionListener(new ImportButton(tensorflowmodel, modelpth));
        labelImport.addActionListener(new ImportButton(tensorflowlabels, labelpth));
        startpredict.addActionListener(new StartPredictButton());
	}
	
	/**
	 * creates the table that will be used for organizing everything
	 */
	private void createTable()
	{
		System.out.print("I made it to creatTable\n");
		table = new Table();
        getContentPane().add(table);
        table.addCell(modelpth).width(250);
        table.addCell(modelImport);
        table.row();
        table.addCell(labelpth).width(250);
        table.addCell(labelImport);
        table.row();
        System.out.print("added camera");
        table.addCell(cameraView).size(448, 336).colspan(2);
        table.row();
        table.addCell(startpredict).colspan(2);
        table.row();
        table.addCell(result).width(300).colspan(2);       
        setLocationRelativeTo(null);
        setResizable(false);
	}

    //@Override
    public class ImportButton implements ActionListener
	{
    	private JFileChooser FileChooser;
    	private JTextField fieldFilePath;
    	private String filepath;
    	
    	public ImportButton (JFileChooser FileChooser, JTextField fieldFilePath)
		{
    		this.FileChooser = FileChooser;
    		this.fieldFilePath = fieldFilePath;
		}
    	
	    public void actionPerformed(ActionEvent e) 
	    {
	    	int returnVal = FileChooser.showOpenDialog(UtensilRecognitionUI.this);
	    	
            if (returnVal == JFileChooser.APPROVE_OPTION) 
            {
                File file = FileChooser.getSelectedFile();
                filepath = file.getAbsolutePath();
				fieldFilePath.setText(filepath.toString());
				System.out.println("New Path: " + filepath);

				EnableStartButton();
            }
            
            else 
            {
                System.out.println("Process was cancelled by user.");
            }
	    }
	}
    
    public void EnableStartButton()
    {
    	if (modelpth.getText().length() > 0 && labelpth.getText().length() > 0)
    		startpredict.setEnabled(true);
    }
    
    
    public class StartPredictButton implements ActionListener
	{
	    public void actionPerformed(ActionEvent e) 
	    {
            
        }
    }

}
