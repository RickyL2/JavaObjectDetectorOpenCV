package com.group.UtensilsRecognition;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamDiscoveryEvent;
import com.github.sarxos.webcam.WebcamDiscoveryListener;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamPicker;
import com.github.sarxos.webcam.WebcamResolution;

public class WebcamManagerV2 
{

	//where all webcam ui elements will be put into
	private JPanel display = null;
	//stuff for integrating webcam with ui
	private WebcamPanel TheWebCamPanel = null;
	private WebcamPicker picker = null;	
	private ImagePanel warningPanel;
	
	//for drawing found objects on webcam panel
	private List<DetectedObject> foundObjects = null;
	private WebcamPanel.Painter painter = null;
	private int LabelfontSize = 20;
	
	private Webcam currentWebcam = null;
	private boolean webCamPanelHasBeenInitialized = false;
	private static String WARNING_LABEL = "No Camera Connected";
	
	private boolean DEBUG_MODE = true;
	
	public WebcamManagerV2()
	{
		//adds a box that says "taco" near the top left of webcam panel, for testing purposes
		if(DEBUG_MODE)
		{
			Rectangle2D.Double box = new Rectangle2D.Double(10.0f, 10.0f, 30.0f, 30.0f);
			DetectedObject billy = new DetectedObject(3.0f, Color.BLUE, box, "TACO");
			foundObjects = new ArrayList<DetectedObject>();
			foundObjects.add(billy);
		}
		
		//is required so threaded stuff wont give out an error exception thing
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandlingForThread());
		
		//the overall display panel
		display = new JPanel();
		
		//for being able to select a different camera
		picker = new WebcamPicker();
		picker.addItemListener(new PanelInteractivity());
		
		//creates panel with error image, looks dope
		warningPanel = new ImagePanel(createErrorImage(WARNING_LABEL));
		
		//will print what webcams are currently available to connect too
		System.out.print("these webcams have been detected\n");
		for (Webcam webcam : Webcam.getWebcams())
		{
			System.out.println("Webcam detected: " + webcam.getName());
		}
		
		//sets it so it can detect when a webcam has been connected/disconnected
		Webcam.addDiscoveryListener(new WebcamDiscovery());
		
		//will connect to the default cam if cams are present
		if(!Webcam.getWebcams().isEmpty())
			ConnectToCam(Webcam.getDefault(), true);
		
		//if no cams present, will show dope error image
		else
		{
			display.setLayout(new BorderLayout());
			display.add(warningPanel, BorderLayout.CENTER);
		}
	}
	
	/** Will connect to the desired webcam */
	private class PanelInteractivity implements ItemListener
	{
		@Override
		public void itemStateChanged(ItemEvent e)
		{
			// TODO Auto-generated method stub
			if (e.getItem() != currentWebcam)
			{
				if (currentWebcam != null) 
				{
					Webcam temp = (Webcam) e.getItem();
					System.out.println("Selected " + temp.getName());
					ConnectToCam(temp, false);
				}
			}
		}
    	
    }
	
	/** Will check if any webcams have been connected or disconnected and take appropriate measures
	 * to ensure everything remains in working order */
	private class WebcamDiscovery implements WebcamDiscoveryListener
	{
    	@Override
    	public void webcamFound(WebcamDiscoveryEvent event)
    	{    		
    		// starting time 
            long start = System.currentTimeMillis();
            
    		System.out.format("Webcam connected: %s \n", event.getWebcam().getName());
    		
    		//clears display
    		//display.remove(1);
    		display.removeAll();
    		
    		// stops webcam panel so it can be restarted with new cam
    		// can't stop the webcam panel if it never existed
    		if(webCamPanelHasBeenInitialized)
    			TheWebCamPanel.stop();
    		
    		//sets to new webcam picker because webcams are diff now so need to update
    		//picker options, this is easier than changing available options
    		picker = new WebcamPicker();
    		picker.addItemListener(new PanelInteractivity());
    		ConnectToCam(event.getWebcam(), false);
    		// ending time
    		long end = System.currentTimeMillis();
            System.out.println("conecting to " + event.getWebcam().getName() + " took " + 
                                        (end - start) + "ms");
    	}

    	@Override
    	public void webcamGone(WebcamDiscoveryEvent event)
    	{
    		// starting time 
            long start = System.currentTimeMillis();
            
    		System.out.format("Webcam disconnected: %s \n", event.getWebcam().getName());
    		
    		picker = new WebcamPicker();
    		picker.addItemListener(new PanelInteractivity());
    		DisconnectfromCam(event.getWebcam());
    		// ending time
    		long end = System.currentTimeMillis();
    		
            System.out.println("disconection takes " + (end - start) + "ms");
    	}
    }
	
	private void DisconnectfromCam(Webcam theUnchosenOne)
	{
		// starting time 
        long start = System.currentTimeMillis();
        
        currentWebcam = null;
        
        //will simply stop the webcam panel. since more webcams are still connected,
        //after current webcam is disconnected, all of them will temporarily disconnect
        //and then reconnect so the webcam found will take care of reconnecting
		if(Webcam.getWebcams().size() > 0)
			TheWebCamPanel.stop();
		
		//if no other webcam is connected, need to quickly place error pic while
		//closing webcam panel can be done in the background to speed things up
		else
		{
			Thread t = new Thread() {

				@Override
				public void run() {
					if(webCamPanelHasBeenInitialized)
						TheWebCamPanel.stop();
					long partOne = System.currentTimeMillis(); 
			        System.out.println("stopping thewebcampanel takes " + 
			                                    (partOne - start) + "ms");
				}
			};
			t.setName("webcampanel-stopper");
			t.setDaemon(true);
			t.start();
			
			//removes whatever display is currently being shown and creates a new layout
			//layout is needed so view can scale
			display.removeAll();
			display.setLayout(new BorderLayout());
			display.add(warningPanel, BorderLayout.CENTER);
	        
	        //finalizes display stuff
			display.validate();
			display.repaint();
		}
		
		//putting this in a thread to close might make it fast but also causes
		//probs when adding the cam back in
		theUnchosenOne.close();
	}
	
	/** Will connect to the desired cam and set up the ui elements accordingly
	 * @param theChosenOne: the desired webcam to connect to
	 * @param fasterMethod: if func should connect to cam with a separate thread.
	 * 						starts sooner but can cause probs if this func is run multiple
	 * 						times simultaneously
	 */
	private void ConnectToCam(Webcam theChosenOne, boolean fasterMethod)
	{
		System.out.print("Connecting to " + theChosenOne.getName() + "\n");
		currentWebcam = theChosenOne;
		
		display.removeAll();		
		display.setLayout(new BorderLayout());
		
		theChosenOne.setViewSize(WebcamResolution.VGA.getSize());
		
		//this should only really be used in the constructor as it can go wrong when
		//dealing with multiple cams
		if(fasterMethod)
		{
			Thread t = new Thread() {

				@Override
				public void run() {
					//will try to close other open cams and because of this
					//not very reliable since this is in a thread. is possible
					//that two diff threads will try to close each others cam
					startWebcamView(theChosenOne);
				}
			};
			t.setName("webcampanel-starter");
			t.setDaemon(true);
			t.start();
		}
		
		//this is safer but possibly a bit slower as you got to wait for
		//it to finish before moving on
		else
			startWebcamView(theChosenOne);
		
	}
	
	/** Will update the ui elements of display to match with chosen webcam
	 * @param theChosenOne: the webcam that will be used */
	public void startWebcamView(Webcam theChosenOne)
	{
		// if for some crazy reason there are other webcams open, close them. no mercy
		for(Webcam webcam : Webcam.getWebcams())
		{
			if(webcam != theChosenOne && webcam.isOpen())
				webcam.close();
		}
		
		TheWebCamPanel = new WebcamPanel(theChosenOne, false);
		webCamPanelHasBeenInitialized = true;
		painter = TheWebCamPanel.getDefaultPainter();
		TheWebCamPanel.setPainter(new DrawableWebCamPanel());
		
		TheWebCamPanel.start();
		TheWebCamPanel.setMirrored(true);	
		
		//just shows some info, not really important. I like it there but i
		//suppose it would look better without
		if(DEBUG_MODE)
		{
			TheWebCamPanel.setFPSDisplayed(true);
			TheWebCamPanel.setDisplayDebugInfo(true);
			TheWebCamPanel.setImageSizeDisplayed(true);
		}		
        
//		painter = (DrawableWebCamPanel) TheWebCamPanel.getDefaultPainter();
		
		picker.setSelectedItem(theChosenOne);
		display.add(picker, BorderLayout.NORTH);
		display.add(TheWebCamPanel, BorderLayout.CENTER);
		
		//finalizes display stuff
		display.validate();
		display.repaint();
	}
	
	/** @return boolean representing if there are any connected webcams  and
	 * if the current cam is active */
	public Boolean IsCameraConnected()
	{
		return !Webcam.getWebcams().isEmpty() && currentWebcam != null
				&& currentWebcam.isOpen();
	}
	
	/** @return Buffered image from connected and open webcam. If none
	 * available, will return what a black image */
	public BufferedImage takePhoto()
	{
		if(IsCameraConnected())
			return currentWebcam.getImage();
		
		return new BufferedImage(640, 480, BufferedImage.TYPE_INT_RGB);
	}
	
	public void SetDetectedObjectList(List<DetectedObject> newListOfObjects)
	{
		foundObjects = newListOfObjects;
	}
	
	/** will return a JPanel with a view of either the webcam or an error image */
	public JPanel GetWebCamPanel()
	{
		return display;
	}
	
	/** Creates error image with text in the middle
	 * @param s: string text that will be displayed within image
	 * @return buffered error image */
	public BufferedImage createErrorImage(String s)
	{
		Dimension resolution = WebcamResolution.VGA.getSize();

		int w = resolution.width;
		int h = resolution.height;
		
		//gets required stuff ready to create graphics
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsConfiguration gc = ge.getDefaultScreenDevice().getDefaultConfiguration();
		BufferedImage bi = gc.createCompatibleImage(w, h);
		
		Graphics2D g2 = ge.createGraphics(bi);
		//sets background to black
		g2.setBackground(new Color(0, 0, 0));
		
		//Creates various colored rectangles
		//white rec
		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, Math.round(w/7), 9 * Math.round(h/10));
		//bottom blue rec
		g2.setColor(Color.BLUE);
		g2.fillRect(0, 9 * Math.round(h/10), Math.round(w/7), h);
		//yellow rec
		g2.setColor(Color.YELLOW);
		g2.fillRect(Math.round(w/7), 0, Math.round(w/7), 9 * Math.round(h/10));
		//bottom black rec
		g2.setColor(Color.BLACK);
		g2.fillRect(Math.round(w/7), 9 * Math.round(h/10), Math.round(w/7), h);
		//cyan rec
		g2.setColor(Color.CYAN);
		g2.fillRect(Math.round(2*w/7), 0, Math.round(w/7 + 0.5f), 9 * Math.round(h/10));
		//bottom magenta rec
		g2.setColor(Color.MAGENTA);
		g2.fillRect(Math.round(2*w/7), 9 * Math.round(h/10), Math.round(w/7 + 0.5f), h);
		//green rec
		g2.setColor(Color.GREEN);
		g2.fillRect(Math.round(3*w/7 - 0.5f), 0, Math.round(w/7), 9 * Math.round(h/10));
		//bottom pink rec
		g2.setColor(Color.GRAY);
		g2.fillRect(Math.round(3*w/7 - 0.5f), 9 * Math.round(h/10), Math.round(w/7), h);
		//magenta rec
		g2.setColor(Color.MAGENTA);
		g2.fillRect(Math.round(4*w/7), 0, Math.round(w/7 + 0.5f), 9 * Math.round(h/10));
		//bottom cyan rec
		g2.setColor(Color.CYAN);
		g2.fillRect(Math.round(4*w/7), 9 * Math.round(h/10), Math.round(w/7 +0.5f), h);
		//red rec
		g2.setColor(Color.RED);
		g2.fillRect(Math.round(5*w/7), 0, Math.round(w/7), 9 * Math.round(h/10));
		//bottom black rec
		g2.setColor(Color.BLACK);
		g2.fillRect(Math.round(5*w/7), 9 * Math.round(h/10), Math.round(w/7), h);
		//blue rec
		g2.setColor(Color.BLUE);
		g2.fillRect(Math.round(6*w/7), 0, Math.round(w/7), 9 * Math.round(h/10));
		//bottom white rec
		g2.setColor(Color.WHITE);
		g2.fillRect(Math.round(6*w/7), 9 * Math.round(h/10), Math.round(w/7), h);
		
		//crates text for image		
		Font font = new Font("sans-serif", Font.BOLD, 30);
		g2.setFont(font);
		FontMetrics metrics = g2.getFontMetrics(font);
		int sw = (w - metrics.stringWidth(s)) / 2;
		int sh = (h - metrics.getHeight()) / 2 + metrics.getHeight() / 2;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(Color.BLACK);
		g2.drawString(s, sw, sh);
		g2.dispose();
		bi.flush();

		return bi;
	}
	
	/** Class that will import buffered image or string of image location and
	 * creates panel for displaying image 
	 * source: http://www.java2s.com/Tutorials/Java/Swing_How_to/JPanel/Scale_image_as_with_JPanel.htm */
	class ImagePanel extends JPanel 
	{
		  private static final long serialVersionUID = 1L;

		  private Image img;
		  private Image scaled;

		  public ImagePanel(String img) {
		    this(new ImageIcon(img).getImage());
		  }

		  public ImagePanel(Image img) {
		    this.img = img;
		  }

		  @Override
		  public void invalidate() {
		    super.invalidate();
		    int width = getWidth();
		    int height = getHeight();

		    if (width > 0 && height > 0) {
		      scaled = img.getScaledInstance(getWidth(), getHeight(),
		          Image.SCALE_FAST);
		    }
		  }

		  @Override
		  public Dimension getPreferredSize() {
		    return img == null ? new Dimension(200, 200) : new Dimension(
		        img.getWidth(this), img.getHeight(this));
		  }

		  @Override
		  public void paintComponent(Graphics g) {
		    super.paintComponent(g);
		    g.drawImage(scaled, 0, 0, this.getWidth(), this.getHeight(), null);
		  }
		}

	/** this error exception is needed for the thread that will show the webcam view
	 * came from an example file that is included with webcam api*/
	class ExceptionHandlingForThread implements Thread.UncaughtExceptionHandler
	{  
	    // Method to handle the 
	    // uncaught exception 
	    public void uncaughtException(Thread t, Throwable e) 
	    {
	    	// Custom task that needs to be 
	        // performed when an exception occurs 
	    	System.err.println(String.format("Exception in thread %s", t.getName()));
			e.printStackTrace();
		}
    }
	
	//will allow us to draw over the current webcam panel
	class DrawableWebCamPanel implements WebcamPanel.Painter
	{
		@Override
		public void paintImage(WebcamPanel panel, BufferedImage image, Graphics2D g2) {
			//paints default image from webcam panel
			if (painter != null) {
				painter.paintImage(panel, image, g2);
			}
			//if there are no found objecs, just don't do anything else here
			if (foundObjects == null) {
				return;
			}
			
			Iterator<DetectedObject> dfi = foundObjects.iterator();
			int tx = TheWebCamPanel.getWidth();
			int ty = TheWebCamPanel.getHeight();
			
			//go through each detected object in display them on screen
			while (dfi.hasNext()) {
				
				DetectedObject currentObject = dfi.next();
				Rectangle2D.Double box = currentObject.getShape();
				double scaler = 1.0;
				
				//if screen width is smaller than screen height
				if(tx < ty)
				{
					//handles scaling bounding box position and size according to screen size
					scaler = tx/WebcamResolution.VGA.getSize().getWidth();
					double extraY = (ty - WebcamResolution.VGA.getSize().getHeight() * scaler)/2;
					g2.setStroke(currentObject.getStroke());
					g2.setColor(currentObject.getColor());
					box = new Rectangle2D.Double(currentObject.getShape().getX() * scaler,
												currentObject.getShape().getY() * scaler + extraY,
												currentObject.getShape().getWidth() * scaler,
												currentObject.getShape().getHeight() * scaler);
				}
				
				//if screen width is bigger than screen height
				else if(tx > ty)
				{
					//handles scaling bounding box position and size according to screen size
					scaler = ty/WebcamResolution.VGA.getSize().getHeight();
					double extraX = (tx - WebcamResolution.VGA.getSize().getWidth() * scaler)/2;
					g2.setStroke(currentObject.getStroke());
					g2.setColor(currentObject.getColor());
					box = new Rectangle2D.Double(currentObject.getShape().getX() * scaler + extraX,
												currentObject.getShape().getY() * scaler,
												currentObject.getShape().getWidth() * scaler,
												currentObject.getShape().getHeight() * scaler);
				}
				
				//actually draws scaled elements
				g2.draw(box);					
				Font font = new Font("sans-serif", Font.BOLD, (int) Math.round(LabelfontSize * scaler));
				g2.setFont(font);
				FontMetrics metrics = g2.getFontMetrics(font);
				//will show label twice overlapping for neat visual effect
				g2.drawString(currentObject.getName(), Math.round(box.getX()),
						Math.round(box.getY()) + metrics.getHeight()/2);
				
				g2.setColor(Color.WHITE);
				g2.drawString(currentObject.getName(), Math.round(box.getX()) + (int) Math.ceil(1*scaler),
						Math.round(box.getY()) + metrics.getHeight()/2 + (int) Math.ceil(1*scaler));
			}
		}

		@Override
		public void paintPanel(WebcamPanel arg0, Graphics2D arg1) {
			// TODO Auto-generated method stub
			if (painter != null) {
				painter.paintPanel(arg0, arg1);
			}
		}
		
	}
}
