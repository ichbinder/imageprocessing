package ue02;
// Copyright (C) 2014 by Klaus Jung
// All rights reserved.
// Date: 2014-10-02
// Authors: André Vallentin: 	527538
// 			Jakob Warnow: 		531600

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import ue02.SequentialLabeling.FillMode;

import java.awt.event.*;
import java.awt.*;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Binarize extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private static final int border = 10;
	private static final int maxWidth = 400;
	private static final int maxHeight = 400;
	private static final File openPath = new File(".");
	private static final String title = "Binarisierung";
	private static final String author = "Vallentin, Andre, Jakob Warnow";
	private static final String initalOpen = "tools1.png";
	
	private static JFrame frame;
	
	private ImageView srcView;				// source image view
	private ImageView dstView;				// binarized image view
	
	private JComboBox<String> methodList;	// the selected binarization method
	private JLabel statusLine;				// to print some status text

    private JSlider slider; //to set the binarize percentage value
    private JLabel thresholdGUI; // the current threshold value to display
	final private short maxK = 50; // Maximum number of iterations to find the perfect threshold for the Isodata-Algorithm
	private JCheckBox checkboxOutline; //Checkbox to enable/disable outline on binarize picture
	private SequentialLabeling sequentialLabeling; //Object to use the method for FloodFilling
	private BreadthFirst breathFirst;
	private DepthFirst depthFirst;
	
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
        JButton load = new JButton("Bild �ffnen");
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
        String[] methodNames = {"BreadthFirst", "DepthFirst", "FloodFill-Sequential"};
        
        methodList = new JComboBox<String>(methodNames);
        methodList.setSelectedIndex(0);		// set initial method
        methodList.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
                binarizeImage();
        	}
        });
        
        slider = new JSlider(0, 255);
//        slider.setMinorTickSpacing(25);
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
			
			@Override
			public void stateChanged(ChangeEvent e) {
				
				methodList.setSelectedIndex(0);
				binarizeImage();
			}
		});
        
        thresholdGUI = new JLabel("Threshold: ");
        
        checkboxOutline = new JCheckBox("Outline");
        checkboxOutline.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
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
        customControl.add(thresholdGUI);               
//---------------------
        
        controls.add(customControl);
        controls.add(checkboxOutline);
        
        JPanel images = new JPanel(new FlowLayout());
        images.add(srcView);
        images.add(dstView);
        
        add(controls, BorderLayout.NORTH);
        add(images, BorderLayout.CENTER);
        add(statusLine, BorderLayout.SOUTH);
                       
        setBorder(BorderFactory.createEmptyBorder(border,border,border,border));        
        // perform the initial binarization
        
        sequentialLabeling = new SequentialLabeling();
        breathFirst = new BreadthFirst();
        depthFirst = new DepthFirst();
        
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
		
    	switch(methodList.getSelectedIndex()) {
    	case 0:	// BreathFirst
    		breathFirst.RegionLabeling(dstPixels, width, height);   
    		break;
    	case 1:	// Depth-First
    		depthFirst.RegionLabeling(dstPixels, width, height);
    		break;
    	case 2: //Sequential Labeling
    		binarizeToByteRange(dstPixels, slider.getValue());
    		sequentialLabeling.SequentialLabeling(dstPixels, width, height);
    		break;
    	}
    	
    	if(checkboxOutline.isSelected()){    		
    		outline(dstPixels, width, height);
    	}
    	
		long time = System.currentTimeMillis() - startTime;
		   	
        dstView.setPixels(dstPixels, width, height);
        
        //dstView.saveImage("out.png");
    	
        frame.pack();
        
    	statusLine.setText(message + " in " + time + " ms");
    }
    
    void binarize50(int pixels[]) {
    	for(int i = 0; i < pixels.length; i++) {
    		int gray = ((pixels[i] & 0xff) + ((pixels[i] & 0xff00) >> 8) + ((pixels[i] & 0xff0000) >> 16)) / 3;
    		pixels[i] = gray < 128 ? 0xff000000 : 0xffffffff;
    	}    	
    }
    
    void binarize(int pixels[], int maxValue) {
    	for(int i = 0; i < pixels.length; i++) {
    		int gray = ((pixels[i] & 0xff) + ((pixels[i] & 0xff00) >> 8) + ((pixels[i] & 0xff0000) >> 16)) / 3;    		
    		pixels[i] = gray < maxValue ? 0xff000000 : 0xffffffff;
    	}    	
    }
    

    void binarizeToByteRange(int pixels[], int maxValue) {
    	for(int i = 0; i < pixels.length; i++) {
    		int gray = ((pixels[i] & 0xff) + ((pixels[i] & 0xff00) >> 8) + ((pixels[i] & 0xff0000) >> 16)) / 3;    		
    		pixels[i] = gray < maxValue ? 0 : 255;
    	}    	
    }    
    
    private int [] createHistogram(int pixels[]){
    	
    	int [] histogram =  new int [256];

       	for(int i = 0; i < pixels.length; i++){
       		int gray = ((pixels[i] & 0xff) + ((pixels[i] & 0xff00) >> 8) + ((pixels[i] & 0xff0000) >> 16)) / 3;
       		histogram[gray]++;
       	}
    	return histogram;
    }
    
    private int isoDataAlgorithm(int [] histogram, int threshold, short k){
    	
    	int [] leftHist = new int [threshold];
    	int [] rightHist = new int [256-threshold];

    	System.arraycopy(histogram, 0, leftHist, 0, leftHist.length);
    	System.arraycopy(histogram, threshold, rightHist, 0, rightHist.length);
    	
    	IntStream leftStream = Arrays.stream(leftHist);
    	IntStream rightStream = Arrays.stream(rightHist);
    	int pA = leftStream.sum();
    	int pB = rightStream.sum();
    	int leftSum = 0 , rightSum = 0;

    	//Kalkuliere Schwerpunkt der linken Seite des Histogramms
    	for(int i = 0; i < threshold; i++){    		
    		leftSum = leftSum + i * histogram[i];
    	}
		int mueA = pA == 0 ? 0 : leftSum / pA;			
    	
    	//Kalkuliere Schwerpunkt der rechten Seite des Histogramms    	
    	for(int i = threshold; i < 256; i++){
    		rightSum = rightSum + i * histogram[i];
    	}
		int mueB = pB == 0 ? 0 : rightSum / pB;		
		
		//Aufpassen: Falls mueA oder mueB == 0 sind muss der Threshold = dem Gegenüber sein
		int nextThreshold = 0;
		if(mueA == 0) nextThreshold = mueB;
		else if(mueB == 0) nextThreshold = mueA;
		else nextThreshold = (mueA + mueB) / 2;
		k++;

		if(threshold != nextThreshold || k < maxK){
    		threshold = isoDataAlgorithm(histogram, nextThreshold, k);    		
    	}
    	return threshold;
    }
    
    private void outline( int [] src, int width, int height){
    	
    	int [] output = new int [src.length];    	
    	System.arraycopy(src, 0, output, 0, output.length);
    	
    	for(int h = 0; h < height; h++){
    		
    		for(int w = 0; w < width; w++){
    			
    			//4er Nachbarschaft
    			int up = (h-1)*width+w;
    			int left = h*width+w-1;
    			int pos = h*width+w;
    			int right = h*width+w+1;
    			int down = (h+1)*width+w;
    			int value = ((src[pos] & 0xff) + ((src[pos] & 0xff00) >> 8) + ((src[pos] & 0xff0000) >> 16)) / 3;
    			
    			//Falls aktuelle Position = Schwarz
    			if(value == 0){

    				//Oben
    				if(h-1 >= 0){
    					output[up] = 0xff000000;
    				}
    				//Links
    				if(w-1 >= 0){    					
    					output[left] = 0xff000000;
    				}
    				//Rechts
    				if(w+1 < width){
    					output[right] = 0xff000000;
    				}
    				//Unten
    				if(h+1 < height){    					
    					output[down] = 0xff000000;
    				}
    			}
    		}
    	}    
    	System.arraycopy(output, 0, src, 0, src.length);    	
    }
}
    
