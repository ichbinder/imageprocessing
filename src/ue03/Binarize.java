package ue03;
// Copyright (C) 2014 by Klaus Jung
// All rights reserved.
// Date: 2014-10-02
// Authors: André Vallentin: 	527538
// 			Jakob Warnow: 		531600

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.event.*;
import java.awt.*;
import java.io.File;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Queue;

public class Binarize extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private static final int border = 10;
	private static final int maxWidth = 400;
	private static final int maxHeight = 400;
	private static final File openPath = new File(".");
	private static final String title = "Binarisierung";
	private static final String author = "Vallentin, Andre, Jakob Warnow";
	private static final String initalOpen = "tools.png";
		
	private int zoom = 100;
	
	
	private static JFrame frame;
	
	private ImageView srcView;				// source image view
	private ImageView dstView;				// binarized image view
	
	private JComboBox<String> methodList;	// the selected binarization method
	private JLabel statusLine;				// to print some status text

    private JSlider slider; //to set the binarize percentage value

    private javax.swing.JCheckBox potraceCB;
    private Potrace potrace;

	public Binarize() {
        super(new BorderLayout(border, border));
        
        // load the default image
        File input = new File(initalOpen);
        
        if(!input.canRead()) input = openFile(); // file not found, choose another image
        
        srcView = new ImageView(input);
        srcView.setMaxSize(new Dimension(maxWidth, maxHeight));
       
		// create an empty destination image
		dstView = new ImageView(srcView.getImgWidth(), srcView.getImgHeight());
		dstView.setMaxSize(new Dimension(maxWidth, maxHeight));
		
		// load image button
        JButton load = new JButton("Bild öffnen");
        load.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		File input = openFile();
        		if(input != null) {
	        		srcView.loadImage(input);
	        		srcView.setMaxSize(new Dimension(maxWidth, maxHeight));
	                binarizeImage();
        		}
        	}        	
        });
         
        // selector for the binarization method
        JLabel methodText = new JLabel("Methode:");
        String[] methodNames = {"BreadthFirst", "DepthFirst", "Sequential"};
        
        methodList = new JComboBox<String>(methodNames);
        methodList.setSelectedIndex(0);		// set initial method
        methodList.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
                binarizeImage();
        	}
        });
        
        slider = new JSlider(0, 255);
        slider.setMinorTickSpacing(25);
        slider.setMajorTickSpacing(255/2);

        
      //Create the label table
        Hashtable labelTable = new Hashtable();
        labelTable.put( new Integer( 0 ), new JLabel("0") );
        labelTable.put( new Integer( 128 ), new JLabel("128") );
        labelTable.put( new Integer( 255 ), new JLabel("255") );
        slider.setLabelTable( labelTable );
        slider.setPaintTicks(true);        
        slider.setPaintLabels(true);

        slider.addChangeListener(new ChangeListener() {
			
			public void stateChanged(ChangeEvent e) {
				
				methodList.setSelectedIndex(0);
				binarizeImage();
			}
		});
        
        
        // some status text
        statusLine = new JLabel(" ");
        
        // arrange all controls
        JPanel controls = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(0,border,0,0);
        controls.add(load, c);
        controls.add(methodText, c);
        controls.add(methodList, c);
//---------------------
      
        JPanel customControl = new JPanel();
        customControl.setLayout(new BoxLayout(customControl, BoxLayout.PAGE_AXIS)); //Vertikal
        customControl.add(slider);
//---------------------
      
        controls.add(customControl);
        
        JPanel images = new JPanel(new FlowLayout());
        images.add(srcView);
        images.add(dstView);
        
        add(controls, BorderLayout.NORTH);
        add(images, BorderLayout.CENTER);

        potraceCB = new javax.swing.JCheckBox("Potrace", false);
        potrace = new Potrace();

        potraceCB.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
            	binarizeImage();
            }
          });

        
        JPanel southControls = new JPanel();
        southControls.setLayout(new BoxLayout(southControls, BoxLayout.PAGE_AXIS)); //Vertikal
        southControls.add(statusLine);
        southControls.add(potraceCB);
        
//        add(statusLine, BorderLayout.SOUTH);
        add(southControls, BorderLayout.SOUTH);
        
        setBorder(BorderFactory.createEmptyBorder(border,border,border,border));        
        // perform the initial binarization        
        binarizeImage();
	}
	
	public void stateChanged(ChangeEvent e) {
	    binarizeImage();
	}
	
	private File openFile() {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Images (*.jpg, *.png, *.gif)", "jpg", "png", "gif");
        chooser.setFileFilter(filter);
        chooser.setCurrentDirectory(openPath);
        int ret = chooser.showOpenDialog(this);
        if(ret == JFileChooser.APPROVE_OPTION) {
    		frame.setTitle(title + chooser.getSelectedFile().getName());
        	return chooser.getSelectedFile();
        }
        return null;		
	}
	
	private static void createAndShowGUI() {
		// create and setup the window
		frame = new JFrame(title + " - " + author + " - " + initalOpen);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JComponent newContentPane = new Binarize();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        // display the window.
        frame.pack();
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        frame.setLocation((screenSize.width - frame.getWidth()) / 2, (screenSize.height - frame.getHeight()) / 2);
        frame.setVisible(true);
	}

	public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
	}	
	
    protected void binarizeImage() {
  
        String methodName = (String)methodList.getSelectedItem();
        
        // image dimensions
        int width = srcView.getImgWidth();
        int height = srcView.getImgHeight();
    	
    	// get pixels arrays
    	int srcPixels[] = srcView.getPixels();
    	int dstPixels[] = java.util.Arrays.copyOf(srcPixels, srcPixels.length);
    	
    	String message = "Binarisieren mit \"" + methodName + "\"";

    	statusLine.setText(message);

		long startTime = System.currentTimeMillis();
		
//    	switch(methodList.getSelectedIndex()) {
//    	case 0:	// BreathFirst
//    		breathFirst.RegionLabeling(dstPixels, width, height);   
//    		break;
//    	case 1:	// Depth-First
//    		depthFirst.RegionLabeling(dstPixels, width, height);
//    		break;
//    	case 2: //Sequential Labeling
//    		binarizeToByteRange(dstPixels, 128);
//    		sequentialLabeling.SequentialLabeling(dstPixels, width, height);
//    		break;
//    	}
    	
/*    	if(checkboxOutline.isSelected()){    		
    		outline(dstPixels, width, height);
    	}
  */  	
		
		if(potraceCB.isSelected()){ 
			potrace.RegionLabeling(dstPixels, width, height);
			Queue<Point> paths = potrace.paths;
		};
		
		long time = System.currentTimeMillis() - startTime;
		   	
        dstView.setPixels(dstPixels, width, height);
        
//        dstView.setPixels(dstView.getPixels(), width*zoom, height*zoom);
        //dstView.saveImage("out.png");
    	
        frame.pack();
        
    	statusLine.setText(message + " in " + time + " ms");
    }
}
    
