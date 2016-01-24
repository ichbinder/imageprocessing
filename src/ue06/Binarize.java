package ue06;
// Copyright (C) 2014 by Klaus Jung

// All rights reserved.
// Date: 2015-11-25
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

public class Binarize extends JPanel {

	private static final long serialVersionUID = 1L;
	private static final int border = 10;
	private static final int maxWidth = 800;
	private static final int maxHeight = 600;
	private static final File openPath = new File(".");
	private static final String title = "Binarisierung";
	private static final String author = "Vallentin, Andre, Jakob Warnow";
	private static final String initalOpen = "";	

	private static final double initalZoom = 1;
	private static double currentZoom = initalZoom;
		
	private static JFrame frame;

	private ImageView dstView; // binarized image view

	private JLabel statusLine; // to print some status text

	private JCheckBox drawPicture;
	private JCheckBox drawPaths;
	

	private JCheckBox grid;	

	
	
	private Potrace potrace;
	
	private JSlider magnification; // to set the binarize percentage value

	
	public Binarize() {
        super(new BorderLayout(border, border));
        
        // load the default image
        File input = new File(initalOpen);
        
        if(!input.canRead()) 
        	input = openFile(); // file not found, choose another image
        
        
       
		// create an empty destination image
        dstView = new ImageView(input, currentZoom);
        dstView.viewIsSrcView = false;
		dstView.setMaxSize(new Dimension((int) (maxWidth * currentZoom), (int ) (maxHeight * currentZoom)));
		
		dstView.setMinSize(maxWidth, maxHeight);
		
		// load image button
        JButton load = new JButton("Bild öffnen");
        load.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		File input = openFile();
        		if(input != null) {
	        		dstView.loadImage(input);
	        		dstView.setMaxSize(new Dimension((int) (maxWidth * currentZoom), (int ) (maxHeight * currentZoom)));
	        		dstView.setMinSize(maxWidth, maxHeight);	        		
	        		reset();
	                binarizeImage();
        		}
        	}        	
        });
         
        magnification = new JSlider(JSlider.HORIZONTAL,10,1000,10);

        magnification.setMinorTickSpacing(50); //Abstände im Feinraster
        magnification.setMajorTickSpacing(100);
        magnification.setPaintTicks(true);
        Hashtable<Integer, JLabel> markLabels = new Hashtable<Integer, JLabel>();
        markLabels.put(new Integer(10), new JLabel("1x"));
        markLabels.put(new Integer(500), new JLabel("50x"));
        markLabels.put(new Integer(1000), new JLabel("100x"));

        magnification.setLabelTable(markLabels);
        magnification.setPaintLabels(true);
        magnification.setSnapToTicks(true);
        
        magnification.addChangeListener(new ChangeListener() {
			
			public void stateChanged(ChangeEvent e) {
				
				currentZoom = magnification.getValue()/10;
        		dstView.setMaxSize(new Dimension((int) (maxWidth * currentZoom), (int ) (maxHeight * currentZoom)));
        		dstView.setMinSize(maxWidth, maxHeight);
        		dstView.setZoom(currentZoom);
			}
		});
                
        // some status text
        statusLine = new JLabel(" ");
        
        // arrange all controls        
        JPanel controls = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(0,border,0,0);
        controls.add(load, c);
//---------------------
      
        JPanel customControl = new JPanel();
        customControl.setLayout(new BoxLayout(customControl, BoxLayout.PAGE_AXIS)); //Vertikal
        customControl.add(magnification);
//---------------------
      
        controls.add(customControl);
        
        JPanel images = new JPanel(new FlowLayout());
        images.add(dstView);
        
        add(controls, BorderLayout.NORTH);
        add(images, BorderLayout.CENTER);
        
        potrace = new Potrace();
        
        
        drawPicture = new JCheckBox("Show picture");
        drawPicture.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
            	
            	dstView.setDrawPicture(drawPicture.isSelected());
            	dstView.updateScreen();
            }
          });
        drawPicture.setSelected(true);
        
        drawPaths = new JCheckBox("Show paths");
        drawPaths.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
            	
            	dstView.setDrawPaths(drawPaths.isSelected());
            	dstView.updateScreen();
            }
          });
       
        JPanel bezierControl = new JPanel();
        bezierControl.setLayout(new BoxLayout(bezierControl, BoxLayout.PAGE_AXIS)); //Vertikal       
        
        grid = new JCheckBox("Show grid");
        grid.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
            	
            	dstView.setGrit(grid.isSelected());
            	dstView.updateScreen();
            }
        });

        JPanel leftSide = new JPanel();
        leftSide.setLayout(new BoxLayout(leftSide, BoxLayout.PAGE_AXIS)); //Vertikal

        leftSide.add(drawPicture);
        leftSide.add(drawPaths);

        leftSide.add(grid);
        
        bezierControl.add(leftSide);

        //Slider                        
        JPanel lbSliderControl = new JPanel();
        lbSliderControl.setLayout(new BoxLayout(lbSliderControl, BoxLayout.PAGE_AXIS)); //Vertikal        
              
        bezierControl.add(lbSliderControl);
        
        JPanel southLayoutControls = new JPanel();
        southLayoutControls.setLayout(new BorderLayout());
        add(bezierControl, BorderLayout.EAST);      
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

		// // image dimensions
		int width  = dstView.getImgWidth();
		int height = dstView.getImgHeight();

		// // get pixels arrays
		int[] dstPixels = dstView.getPixels();
		String message = "Konturfindung.";
		statusLine.setText(message);

		long startTime = System.currentTimeMillis();
		potrace.FindContoures(dstPixels, width, height);
		dstView.setContoures(potrace.getContoures());
			
		System.out.println("Konturen gefunden");
			
	}
	
	
	public JPanel createBorderPanel(JLabel lbDesc, JSlider slider, JLabel lbValue){
		
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
        panel.add(lbDesc, BorderLayout.WEST);
        panel.add(slider, BorderLayout.CENTER);
        panel.add(lbValue, BorderLayout.EAST);

		return panel;
	}
	
	private void reset(){
		
		dstView.setContoures(new Contoure[0]);
		potrace.reset();
	}
}
