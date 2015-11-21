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
import java.awt.image.BufferedImage;
import java.awt.*;
import java.io.File;
import java.util.Hashtable;

import java.awt.Dimension;
import java.awt.image.DataBufferInt;

public class Binarize extends JPanel {

	private static final long serialVersionUID = 1L;
	private static final int border = 10;
	private static final int maxWidth = 450;
	private static final int maxHeight = 450;
	private static final File openPath = new File(".");
	private static final String title = "Binarisierung";
	private static final String author = "Vallentin, Andre, Jakob Warnow";
	private static final String initalOpen = "klein.png";
	private static final double initalZoom = 2;

	private double zoomlvl = 1;

	private static JFrame frame;

	private ImageView srcView; // source image view
	private ImageView dstView; // binarized image view

	private JComboBox<String> methodList; // the selected binarization method
	private JLabel statusLine; // to print some status text

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
        dstView = new ImageView(input, initalZoom);
		dstView.setMaxSize(new Dimension(maxWidth, maxHeight));
		dstView.setMinSize(maxWidth, maxHeight);
		
		// load image button
        JButton load = new JButton("Bild �ffnen");
        load.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		File input = openFile();
        		if(input != null) {
	        		srcView.loadImage(input);
	        		srcView.setMaxSize(new Dimension(maxWidth, maxHeight));
	        		srcView.setMinSize(maxWidth, maxHeight);
	        		dstView.loadImage(input);
	        		dstView.setMaxSize(new Dimension(maxWidth, maxHeight));
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
        magnification.setLabelTable(markLabels);
        magnification.setPaintLabels(true);
        magnification.setSnapToTicks(true);
        
//        magnification.addChangeListener(new BoundedChangeListener());

//        
//      //Create the label table
//        Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
//        labelTable.put( new Integer( 0 ), new JLabel("0") );
//        labelTable.put( new Integer( 50 ), new JLabel("50") );
//        labelTable.put( new Integer( 100 ), new JLabel("100") );
//        magnification.setLabelTable( labelTable );
//        magnification.setPaintTicks(true);        
//        magnification.setPaintLabels(true);

        magnification.addChangeListener(new ChangeListener() {
			
			public void stateChanged(ChangeEvent e) {
				srcView.setZoom(magnification.getValue()/10);
				dstView.setZoom(magnification.getValue()/10);
				System.out.println(magnification.getValue()/10);
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
        customControl.add(magnification);
//---------------------
      
        controls.add(customControl);
        
        JPanel images = new JPanel(new FlowLayout());
        images.add(srcView);
        images.add(dstView);
        
        add(controls, BorderLayout.NORTH);
        add(images, BorderLayout.CENTER);
        add(statusLine, BorderLayout.SOUTH);
                       
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
		// int width = srcView.getImgWidth();
		// int height = srcView.getImgHeight();
		//
		// // get pixels arrays
		// int[] srcPixels = srcView.getPixels();
		// int dstPixels[] = java.util.Arrays.copyOf(srcPixels,
		// srcPixels.length);

		String message = "Binarisieren mit \"" + methodName + "\"";

		statusLine.setText(message);

		long startTime = System.currentTimeMillis();

		switch (methodList.getSelectedIndex()) {
		case 0: // BreathFirst
			// int[] srcPixels2 = srcView.getPixels();
			// BufferedImage originalImage = new BufferedImage(width, height,
			// BufferedImage.TYPE_INT_ARGB);
			// int[] pixel = ((DataBufferInt)
			// originalImage.getRaster().getDataBuffer()).getData();
			// System.arraycopy(srcPixels2, 0, pixel, 0, srcPixels2.length);
			// srcView.zoom(originalImage., srcView.getImgWidth()+100,
			// srcView.getImgHeight()+100);
			break;
		case 1: // Depth-First

			srcView.setZoom(1.5);

			// depthFirst.RegionLabeling(dstPixels, width, height);
			break;
		// case 2: //Sequential Labeling
		// binarizeToByteRange(dstPixels, 128);
		// sequentialLabeling.SequentialLabeling(dstPixels, width, height);
		// break;
		}

		/*
		 * if(checkboxOutline.isSelected()){ outline(dstPixels, width, height);
		 * }
		 */
		long time = System.currentTimeMillis() - startTime;

		// dstView.setPixels(dstPixels, width, height);

		frame.pack();

		statusLine.setText(message + " in " + time + " ms");
	}
}
