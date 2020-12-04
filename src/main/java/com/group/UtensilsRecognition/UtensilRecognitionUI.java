package com.group.UtensilsRecognition;

import com.esotericsoftware.tablelayout.swing.Table;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;


public class UtensilRecognitionUI extends JFrame{
	
	//this id thing is just to get rid of some minor error for the class, can ignore
	private static final long serialVersionUID = 1L;
	
	private static final int FRAME_WIDTH = 520;
	private static final int FRAME_HEIGHT = 500;
	
	//regular ui stuff
	private String FrameName = "Utensil Recognition Software";
	private Color defaultButtonColor;
    private JButton startpredict;
    private JButton settingButton;
    
    //settings ui stuff
    
    //for creating classifier
    private JTextField classifierLabel;
    private JFileChooser classifierFileGetter;
    private FileNameExtensionFilter classifierFilter = new FileNameExtensionFilter(
            "XML", "xml");
    private JTextField classifierPath;
    private JComboBox<Float> classifierWidth;
    private JComboBox<String> classifierColor;
    private JButton getClassifierButton;
    private JButton createClassifierButton;
    
    //for viewing/deleting classifiers
	private ArrayList<CascadeClassifierProperties> oldClassifiers;
	
	//for saving/going back to main ui
	private JButton backButton;
    
    private JPanel cameraView;
    
    private JPanel MainPanel;
    
    private boolean RecognitionOn = false;

    public UtensilRecognitionUI(JPanel cameraView) {
    	
    	this.cameraView = cameraView;
    	oldClassifiers = new ArrayList<CascadeClassifierProperties>();
    	
        setTitle(FrameName);
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setMinimumSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        
        createButtons();
        createStandardUI();
    }
    
    /** Creates the buttons and other UI elements that will be used within UI. */
	private void createButtons()
	{
		//gets the buttons working
		startpredict = new JButton("On/Off");
		defaultButtonColor = startpredict.getBackground();
		getClassifierButton = new JButton("Open");
		settingButton = new JButton("Settings");
		createClassifierButton = new JButton("ADD");
		backButton = new JButton("Back");
        
        //sets it up so a file chooser will be active and will be limited in what type of
        //files it will accept
		classifierFileGetter = new JFileChooser();
		classifierFileGetter.setFileFilter(classifierFilter);
		classifierFileGetter.setFileSelectionMode(JFileChooser.FILES_ONLY);
        
        //create txt boxes and comboboxes
        classifierPath = new JTextField("select file");
        classifierLabel = new JTextField("");
        classifierPath.setEditable(false);
        CreateColorComboBox();
        CreateWidthComboBox();        
        
        //sets up buttons with a listener that will activate when button is pushed
        getClassifierButton.addActionListener(new ImportButton(classifierFileGetter, classifierPath));        
        startpredict.addActionListener(new StartPredictButton());
        settingButton.addActionListener(new SettingsButtonAction());
        createClassifierButton.addActionListener(new CreateUserButtonAction());
	}
	
	private void CreateColorComboBox()
	{
		classifierColor = new JComboBox<String>();
		classifierColor.addItem("magenta");
		classifierColor.addItem("red");
		classifierColor.addItem("pink");
		classifierColor.addItem("orange");
		classifierColor.addItem("yellow");
		classifierColor.addItem("green");
		classifierColor.addItem("blue");
		classifierColor.addItem("cyan");
		classifierColor.addItem("gray");
		classifierColor.addItem("white");
		classifierColor.addItem("black");
		classifierColor.setSelectedIndex(7);
	}
	
	private void CreateWidthComboBox()
	{
		classifierWidth = new JComboBox<Float>();
		classifierWidth.addItem(1f);
		classifierWidth.addItem(2f);
		classifierWidth.addItem(3f);
		classifierWidth.addItem(4f);
		classifierWidth.addItem(5f);
		classifierWidth.addItem(6f);
		classifierWidth.addItem(7f);
		classifierWidth.addItem(8f);
		classifierWidth.addItem(9f);
		classifierColor.setSelectedIndex(1);
	}
	
	/** Allows a JFileChooser to open when button is pressed. JFileChooser will be
	 * limited in what is allowed to be selected **/
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
            }
            
            else 
            {
                System.out.println("Process was cancelled by user.");
            }
	    }
	}
    
    /** Is responsible for activating openCV recognition by setting boolean to true. */
    private class StartPredictButton implements ActionListener
	{
	    public void actionPerformed(ActionEvent e) 
	    {
	    	RecognitionOn = !RecognitionOn;
	    	
	    	
	    	if(RecognitionOn)
	    	{
	    		startpredict.setBackground(Color.GREEN);
	    	}
	    	
	    	else
	    	{
	    		startpredict.setBackground(defaultButtonColor);
	    	}
        }
    }
    
    //returns boolean representing if app should be running
    public boolean ShouldRun()
    {
    	return RecognitionOn;
    }
    
    /** will crate classifier stuff if fields have been filled out */
    private class CreateUserButtonAction implements ActionListener
	{
	    public void actionPerformed(ActionEvent e) 
	    {
	    	if(!classifierLabel.getText().equals("") && !classifierPath.getText().equals("select file"))
	    	{
	    		CascadeClassifierProperties temp = new CascadeClassifierProperties(classifierLabel.getText()
	    											, classifierPath.getText().toString()
	    											, getFloatFromString(classifierWidth.getSelectedItem().toString())
	    											, getColorFromString(classifierColor.getSelectedItem().toString()));
	    		oldClassifiers.add(temp);	    		
	    		classifierLabel.setText("");
		    	classifierPath.setText("select file");

		    	remove(MainPanel);
		    	createSettingsUI();
				revalidate();
	    	}
        }
    }
    
    //replaces current ui stuf with those for settings
    private class SettingsButtonAction implements ActionListener
	{
	    public void actionPerformed(ActionEvent e) 
	    {
	    	RecognitionOn = false;
	    	startpredict.setBackground(defaultButtonColor);
	    	
	    	remove(MainPanel);
	    	createSettingsUI();
			revalidate();
        }
    }
    
    //will delete the classifer with matching file path that is saved with delete button
    private class DeleteButtonListener implements ActionListener
	{
		String filePath;
		public DeleteButtonListener(String path)
		{
			filePath = path;
		}
		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			for(int i = 0; i < oldClassifiers.size(); i++)
			{
				if(oldClassifiers.get(i).getFilePath() == filePath)
				{
					oldClassifiers.remove(i);
					break;
				}
			}
			
			remove(MainPanel);
	    	createSettingsUI();
			revalidate();
		}
		
	}
	
	/** Places standard UI elements into different portions and adds them to the frame. */
	private void createStandardUI()
	{
		//removeAll();
		//this MainPanel will encompass everything
		MainPanel = new JPanel();
		//by adding a borderlayout, we will be able to manage the relative location
		//of different elements and it will be able to scale the webcam viewer by itself
		MainPanel.setLayout(new BorderLayout());

		//creates table with buttons
        Table buttonTable = new Table();
        buttonTable.addCell(startpredict).left();
        buttonTable.addCell(settingButton).right();
        
        //places each portion of elements into different relative places of the JPanel
        MainPanel.add(cameraView, BorderLayout.CENTER);
        MainPanel.add(buttonTable, BorderLayout.SOUTH);
        
        //places the JPanel into the current JFrame
		add(MainPanel);
	}
	
	//creates the settings ui
	private void createSettingsUI()
	{
		//this MainPanel will encompass everything
		MainPanel = new JPanel();
		//by adding a borderlayout, we will be able to manage the relative location
		//of different elements and it will be able to scale the webcam viewer by itself
		MainPanel.setLayout(new BorderLayout());
		
		//table for previously saved classifiers and title
		Table overAllTopTable = new Table();
		
		//will place all previously saved classifiers into neat table
		Table classifiersTable = new Table();
		for(CascadeClassifierProperties currentClassifier: oldClassifiers)
		{
			String theFilePath = currentClassifier.getFilePath();
			JLabel savedLabel = new JLabel(currentClassifier.getLabel());
			JLabel savedFilePath = new JLabel(theFilePath);
			JLabel savedWidth = new JLabel("" + currentClassifier.getWidth());
			JLabel savedColor = new JLabel(currentClassifier.getWordColor());
			
			JButton deleteButton = new JButton();
			deleteButton.setText("X");
			deleteButton.addActionListener(new DeleteButtonListener(theFilePath));
			
			classifiersTable.addCell(savedLabel);
			classifiersTable.addCell(savedFilePath);
			classifiersTable.addCell(savedWidth);
			classifiersTable.addCell(savedColor);
			classifiersTable.addCell(deleteButton);
			classifiersTable.row();
		}
		
		//if any classifiers have been saved, place all top elements into overall top table
		if(!oldClassifiers.isEmpty())
		{
			overAllTopTable.addCell("Saved Cascade Classifiers");
			overAllTopTable.row();
			overAllTopTable.addCell(classifiersTable);
		}
		
		//table for all bottom screen stuff
		Table overAllBottomTable = new Table();
		
		//table stuff for adding a classifier
		Table addClassifierTable = new Table();
		
		addClassifierTable.addCell("lable");
		addClassifierTable.addCell("file path");
		addClassifierTable.addCell("");
		addClassifierTable.addCell("width");
		addClassifierTable.addCell("color");
		
		addClassifierTable.row();
		
		addClassifierTable.addCell(classifierLabel).minWidth(100);
		addClassifierTable.addCell(classifierPath).minWidth(80);
		addClassifierTable.addCell(getClassifierButton);
		addClassifierTable.addCell(classifierWidth);
		addClassifierTable.addCell(classifierColor);
		addClassifierTable.addCell(createClassifierButton);
		
		overAllBottomTable.addCell(addClassifierTable);
		
		overAllBottomTable.row();
		overAllBottomTable.addCell(backButton).width(200);
		
        
        //places each portion of elements into different relative places of the JPanel
		MainPanel.add(overAllTopTable, BorderLayout.NORTH);
        MainPanel.add(overAllBottomTable, BorderLayout.SOUTH);
        
        //places the JPanel into the current JFrame
		add(MainPanel);
	}
	
	private Color getColorFromString(String c)
	{
		Color temp;
		
		switch (c) {
        case "magenta":
        	temp = Color.MAGENTA;
            break;
        case "red":
        	temp = Color.RED;
            break;
        case "pink":
        	temp = Color.PINK;
            break;
        case "orange":
        	temp = Color.ORANGE;
            break;
        case "yellow":
        	temp = Color.YELLOW;
            break;
        case "green":
        	temp = Color.GREEN;
            break;
        case "blue":
        	temp = Color.BLUE;
            break;
        case "cyan":
        	temp = Color.CYAN;
            break;
        case "gray":
        	temp = Color.GRAY;
            break;
        case "white":
        	temp = Color.WHITE;
            break;
        default: temp = Color.BLACK;
            break;
		}
		
		return temp;
	}
	
	private Float getFloatFromString(String c)
	{
		float temp;
		
		switch (c) {
       case "2":
        	temp = 2f;
            break;
        case "3":
        	temp = 3f;
            break;
        case "4":
        	temp = 4f;
            break;
        case "5":
        	temp = 5f;
            break;
        case "6":
        	temp = 6f;
            break;
        case "7":
        	temp = 7f;
            break;
        case "8":
        	temp = 8f;
            break;
        case "9":
        	temp = 9f;
            break;
        default: temp = 1f;
            break;
		}
		
		return temp;
	}

}
