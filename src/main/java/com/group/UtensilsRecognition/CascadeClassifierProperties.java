package com.group.UtensilsRecognition;

import java.awt.Color;

public class CascadeClassifierProperties
{
	private String label = null;
	private String filePath = null;
	private float strokeWidth = 0.0f;
	private Color objectColor = null;
	
	public CascadeClassifierProperties(String label, String filepath, float strokeWidth
										, Color objectColor)
	{
		this.label = label;
		this.filePath = filepath;
		this.strokeWidth = strokeWidth;
		this.objectColor = objectColor;
	}
	
	public String getLabel()
	{
		return label;
	}
	
	public String getFilePath()
	{
		return filePath;
	}

	public float getWidth()
	{
		return strokeWidth;
	}

	public Color getColor()
	{
		return objectColor;
	}
	
	public String getWordColor()
	{
		String temp;
		
		if(objectColor == Color.MAGENTA)
        	temp = "magenta";
		else if(objectColor == Color.RED)
        	temp = "red";
		else if(objectColor == Color.PINK)
        	temp = "pink";
		else if(objectColor == Color.ORANGE)
        	temp = "orange";
		else if(objectColor == Color.YELLOW)
        	temp = "yellow";
		else if(objectColor == Color.GREEN)
        	temp = "green";
		else if(objectColor == Color.BLUE)
        	temp = "blue";
		else if(objectColor == Color.CYAN)
        	temp = "cyan";
		else if(objectColor == Color.GRAY)
        	temp = "gray";
		else if(objectColor == Color.WHITE)
        	temp = "white";		
		else
			temp = "black";
		
		return temp;
	}

}
