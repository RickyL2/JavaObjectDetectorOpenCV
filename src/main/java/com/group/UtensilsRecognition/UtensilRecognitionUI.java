package com.group.UtensilsRecognition;

import com.esotericsoftware.tablelayout.swing.Table;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;


public class UtensilRecognitionUI extends JFrame{
	
	//this id thing is just to get rid of some minor error for the class, can ignore
	private static final long serialVersionUID = 1L;
	
	private static final int FRAME_WIDTH = 520;
	private static final int FRAME_HEIGHT = 500;
	
	private String FrameName = "Utensil Recognition Software";
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
        setMinimumSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        
        createButtons();
        createTable();
    }
    
    /** Creates the buttons and other UI elements that will be used within UI. */
	private void createButtons()
	{
		//sets these to a new JButton object and labels the buttons
		startpredict = new JButton("Start Identification");
		modelImport = new JButton("Choose Tensorflow Model");
	    labelImport = new JButton("Choose Tensorflow Labels");
	    
	    //initially this button will not work
        startpredict.setEnabled(false);
        
        //sets it up so a file chooser will be active and will be limited in what type of
        //files it will accept
        tensorflowmodel = new JFileChooser();
        tensorflowmodel.setFileFilter(tensorflowmodelfilter);
        tensorflowmodel.setFileSelectionMode(JFileChooser.FILES_ONLY);
        
        tensorflowlabels = new JFileChooser();
        tensorflowlabels.setFileFilter(tensorflowlabelfilter);
        tensorflowlabels.setFileSelectionMode(JFileChooser.FILES_ONLY);
        
        //create txt boxes that are not editable by user, will display file locations and
        //results of image scan
        result = new JTextField();
        modelpth = new JTextField();
        modelpth.setEditable(false);
        labelpth = new JTextField();
        labelpth.setEditable(false);
        
        //sets up buttons with a listener that will activate when button is pushed
        modelImport.addActionListener(new ImportButton(tensorflowmodel, modelpth));
        labelImport.addActionListener(new ImportButton(tensorflowlabels, labelpth));
        startpredict.addActionListener(new StartPredictButton());
	}
	
	/** Will enable the start button within UI once files have been chosen. */
	private void EnableStartButton()
    {
		//if txt fields are not empty, must mean that files have been chosen
    	if (modelpth.getText().length() > 0 && labelpth.getText().length() > 0)
    		startpredict.setEnabled(true);
    }
	
	/** Allows a JFileChooser to open when button is pressed. JFileChooser will be
	 * limited in what is allowed to be selected. Filelocation will be outputted
	 * to a JTextField */
    private class ImportButton implements ActionListener
	{
    	private JFileChooser FileChooser;
    	private JTextField fieldFilePath;
    	
    	public ImportButton (JFileChooser FileChooser, JTextField fieldFilePath)
		{
    		this.FileChooser = FileChooser;
    		this.fieldFilePath = fieldFilePath;
		}
    	
    	//allows user to get desired file and displays file location within txt field
	    public void actionPerformed(ActionEvent e) 
	    {
	    	int returnVal = FileChooser.showOpenDialog(UtensilRecognitionUI.this);
	    	
            if (returnVal == JFileChooser.APPROVE_OPTION) 
            {
                File file = FileChooser.getSelectedFile();
				fieldFilePath.setText(file.getAbsolutePath().toString());
				System.out.println("New Path: " + fieldFilePath.getText());
				
				//will attempt to enable the start button
				EnableStartButton();
            }
            
            else 
            {
                System.out.println("Process was cancelled by user.");
            }
	    }
	}
    
    /** Is responsible for activating tensorflow image analysis, not yet complete. */
    private class StartPredictButton implements ActionListener
	{
	    public void actionPerformed(ActionEvent e) 
	    {
            
        }
    }
    
    /** Will update the result txt box
     * @param resultTxt is the new txt for the result box */
    public void UpdateResults(String resultTxt)
    {
    	result.setText(resultTxt);
    }
	
	/** Places UI elements into different portions and adds them to the frame. */
	private void createTable()
	{
		//this MainPanel will encompass everything
		JPanel MainPanel = new JPanel();
		//by adding a borderlayout, we will be able to manage the relative location
		//of different elements and it will be able to scale the webcam viewer by itself
		MainPanel.setLayout(new BorderLayout());
		
		//creates a table for the elements repsonsible for importing the tensorflow
		//model and label txt document
		Table importTable = new Table();
		importTable.addCell(modelpth).minWidth(250).left();
		importTable.addCell(modelImport).right();
		importTable.row();
		importTable.addCell(labelpth).minWidth(250).left();
		importTable.addCell(labelImport).right();
        
		//creates another table but with the start button and the result txt field
        Table resultTable = new Table();
        resultTable.addCell(startpredict).left();
        resultTable.addCell(result).width(300).right();
        
        //places each portion of elements into different relative places of the JPanel
        MainPanel.add(importTable, BorderLayout.NORTH);		
		MainPanel.add(cameraView, BorderLayout.CENTER);
        MainPanel.add(resultTable, BorderLayout.SOUTH);
        
        //places the JPanel into the current JFrame
		add(MainPanel);
	}

}
