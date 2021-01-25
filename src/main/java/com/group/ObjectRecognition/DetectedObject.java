package com.group.ObjectRecognition;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

//A class for saving properties for a detected object
//everything pretty self-explanatory
public class DetectedObject {
	
	private float strokeWidth = 0.0f;
	private Color objectColor = null;
	private Rectangle2D.Double boundingBox = null;
	private String objectName = null;
	
	public DetectedObject(float strokeWidth, Color objectColor,
							Rectangle2D.Double boundingBox, String objectName)
	{
		this.strokeWidth = strokeWidth;
		this.objectColor = objectColor;
		this.boundingBox = boundingBox;
		this.objectName = objectName;
	}
	
	public Stroke getStroke()
	{
		return new BasicStroke(strokeWidth);
	}
	
	public Color getColor()
	{
		return objectColor;
	}
	
	public Rectangle2D.Double getShape()
	{
		return boundingBox;
	}
	
	public String getName()
	{
		return objectName;
	}
	
}
