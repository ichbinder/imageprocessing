package ue03;
// Copyright (C) 2014 by Klaus Jung

import javax.imageio.ImageIO;

// All rights reserved.
// Date: 2014-10-02
// Authors: André Vallentin: 	527538
// 			Jakob Warnow: 		531600

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Queue;

import java.awt.Dimension;
import java.awt.image.DataBufferInt;

public class Binarize extends JPanel {

	private static final long serialVersionUID = 1L;
	private static final int border = 10;
	private static final int maxWidth = 450;
	private static final int maxHeight = 450;
	private static final File openPath = new File(".");
	private static final String title = "Binarisierung";
	private static final String author = "Vallentin, Andrezz, Jakob Warnow";
	private static final String initalOpen = "test2.png";	
	private static final double initalZoom = 1;
	private static double currentZoom = initalZoom;
	
	private static JFrame frame;

	private ImageView srcView; // source image view
	private ImageView dstView; // binarized image view

	private JComboBox<String> methodList; // the selected binarization method
	private JLabel statusLine; // to print some status text
	
	private JCheckBox drawPaths;
	
	private Potrace potrace;
	
	private JSlider magnification; // to set the binarize percentage value

	public Binarize() {
        super(new BorderLayout(border, border));
        
        // load the default image
        File input = new File(initalOpen);
        
        if(!input.canRead()) 
        	input = openFile(); // file not found, choose another image
        
        srcView = new ImageView(input, initalZoom);
        srcView.setMaxSize(new Dimension(maxWidth, maxHeight));
        srcView.setMinSize(maxWidth, maxHeight);
       
		// create an empty destination image
        dstView = new ImageView(input, currentZoom);
		dstView.setMaxSize(new Dimension((int) (maxWidth * currentZoom), (int ) (maxHeight * currentZoom)));
		
		dstView.setMinSize(maxWidth, maxHeight);
		
		// load image button
        JButton load = new JButton("Bild öffnen");
        load.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		File input = openFile();
        		if(input != null) {
	        		srcView.loadImage(input);	        		
	        		srcView.setMinSize(maxWidth, maxHeight);
	        		dstView.loadImage(input);
	        		dstView.setMaxSize(new Dimension((int) (maxWidth * currentZoom), (int ) (maxHeight * currentZoom)));
	        		dstView.setMinSize(maxWidth, maxHeight);	        		
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
        
        
//        magnification = new JSlider(JSlider.HORIZONTAL,10,10000,10);
        magnification = new JSlider(JSlider.HORIZONTAL,10,100,10);

        magnification.setMinorTickSpacing(1); //Abstände im Feinraster
        magnification.setMajorTickSpacing(10);
        magnification.setPaintTicks(true);
        Hashtable<Integer, JLabel> markLabels = new Hashtable<Integer, JLabel>();
        markLabels.put(new Integer(10), new JLabel("10x"));
//        markLabels.put(new Integer(20), new JLabel("20x"));
        markLabels.put(new Integer(50), new JLabel("50x"));
//        markLabels.put(new Integer(60), new JLabel("60x"));
//        markLabels.put(new Integer(80), new JLabel("80x"));
        markLabels.put(new Integer(100), new JLabel("100x"));
//        markLabels.put(new Integer(1000), new JLabel("1000x"));

        magnification.setLabelTable(markLabels);
        magnification.setPaintLabels(true);
        magnification.setSnapToTicks(true);
        
        magnification.addChangeListener(new ChangeListener() {
			
			public void stateChanged(ChangeEvent e) {
				
//				currentZoom = magnification.getValue();
//        		dstView.setMaxSize(new Dimension((int) (maxWidth * currentZoom), (int ) (maxHeight * currentZoom)));
//        		dstView.setMinSize(maxWidth, maxHeight);

        		//srcView.setZoom(currentZoom);
//        		dstView.setZoom(currentZoom);
//				srcView.setZoom(magnification.getValue()/10);
//				dstView.setZoom(magnification.getValue()/10);
//				System.out.println(magnification.getValue() + "jojo");
//				methodList.setSelectedIndex(0);
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
        customControl.add(magnification);
//---------------------
      
        controls.add(customControl);
        
        JPanel images = new JPanel(new FlowLayout());
        images.add(srcView);
        images.add(dstView);
        
        add(controls, BorderLayout.NORTH);
        add(images, BorderLayout.CENTER);
        
        potrace = new Potrace();
        JPanel southControls = new JPanel();
        drawPaths = new JCheckBox("Show paths");
        drawPaths.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
            	binarizeImage();
            }
          });
        
        
        southControls.setLayout(new BoxLayout(southControls, BoxLayout.PAGE_AXIS)); //Vertikal
        southControls.add(drawPaths);
        southControls.add(statusLine);
        
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
		if (ret == JFileChooser.APPROVE_OPTION) {
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
		newContentPane.setOpaque(true); // content panes must be opaque
		frame.setContentPane(newContentPane);

		// display the window.
		frame.pack();
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolkit.getScreenSize();
		frame.setLocation((screenSize.width - frame.getWidth()) / 2, (screenSize.height - frame.getHeight()) / 2);
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	protected void binarizeImage() {

		String methodName = (String) methodList.getSelectedItem();

		// // image dimensions
		int width  = dstView.getImgWidth();
		int height = dstView.getImgHeight();

		// // get pixels arrays
//		int[] srcPixels = dstView.getPixels();
//		int dstPixels[] = java.util.Arrays.copyOf(srcPixels,srcPixels.length);
		int[] dstPixels = dstView.getPixels();
				
		String message = "Binarisieren mit \"" + methodName + "\"";

		statusLine.setText(message);

		long startTime = System.currentTimeMillis();
		
		if(drawPaths.isSelected()){
			
			int [] pathPics = potrace.RegionLabeling(dstPixels, width, height);
			System.arraycopy(pathPics, 0, dstPixels, 0, pathPics.length);
	        dstView.setPixels(pathPics, width, height);
		}
			
		long time = System.currentTimeMillis() - startTime;
		// dstView.setPixels(dstPixels, width, height);

		frame.pack();

		statusLine.setText(message + " in " + time + " ms");
	}
}
