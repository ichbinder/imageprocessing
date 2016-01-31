
package ue06;
// Copyright (C) 2010 by Klaus Jung
// All rights reserved.
// Date: 2010-03-15

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.SystemColor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.Font;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;


public class ImageView extends JScrollPane{

	private static final long serialVersionUID = 1L;
	
	private ImageScreen	screen = null;
	private Dimension maxSize = null;
	private int borderX = -1;
	private int borderY = -1;
	private double maxViewMagnification = 1.0;		// use 0.0 to disable limits 
	private boolean keepAspectRatio = true;
	private double zoom = 1.0;
	private Contoure [] contoures = new Contoure[0];
	
	private int offsetX,  offsetY;
	
	private int clickPositionX = 0;
	private int clickPositionY = 0;
	
	public boolean viewIsSrcView = true;
	
	
	
	private boolean grid;
	
	int pixels[] = null;		// pixel array in ARGB format
	
	public ImageView(int width, int height) {
		// construct empty image of given size
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		init(bi, true);
	}
	
	public ImageView(int width, int height, double zoom) {
		this.zoom = zoom;
		// construct empty image of given size
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		init(bi, true);
	}	

	public ImageView(File file) {
		// construct image from file
		loadImage(file);
	}
	
	public ImageView(File file, double zoom) {
		this.zoom = zoom;
		loadImage(file);
	}
	
	public void setMaxSize(Dimension dim) {
		// limit the size of the image view
		maxSize = new Dimension(dim);
		
		Dimension size = new Dimension(maxSize);
		if(size.width - borderX > screen.image.getWidth()) 
			size.width = screen.image.getWidth() + borderX;
		if(size.height - borderY > screen.image.getHeight()) 
			size.height = screen.image.getHeight() + borderY;
		setPreferredSize(size);
	}
	
	public void setMinSize(int width, int height) {
		// resize image and erase all content
		if(width == getImgWidth() && height == getImgHeight()) return;
		
		Dimension size = new Dimension(maxSize);
		if(size.width - borderX > width) 
			size.width = width + borderX;
		if(size.height - borderY > height) 
			size.height = height + borderY;
		setPreferredSize(size);

		screen.invalidate();
		screen.repaint();
	}
	
	public int getImgWidth() {
		return screen.image.getWidth();
	}

	public int getImgHeight() {
		return screen.image.getHeight();
	}
	
	public void setZoom(double zoom) {
		this.zoom = zoom;
		screen.revalidate();
		screen.repaint();
	}
	
	public void setContoures(Contoure [] cons){
		
		this.contoures = cons;
	}
	
	public void updateScreen(){
		
		screen.invalidate();
		screen.repaint();
	}
	
	public void resetToSize(int width, int height) {
		// resize image and erase all content
		if(width == getImgWidth() && height == getImgHeight()) return;
		
		screen.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		pixels = new int[getImgWidth() * getImgHeight()];
		screen.image.getRGB(0, 0, getImgWidth(), getImgHeight(), pixels, 0, getImgWidth());
		
		Dimension size = new Dimension(maxSize);
		if(size.width - borderX > width) 
			size.width = width + borderX;
		if(size.height - borderY > height) 
			size.height = height + borderY;
		setPreferredSize(size);

		screen.invalidate();
		screen.repaint();
	}
	
	public int[] getPixels() {
		// get reference to internal pixels array
		if(pixels == null) {
			pixels = new int[getImgWidth() * getImgHeight()];
			screen.image.getRGB(0, 0, getImgWidth(), getImgHeight(), pixels, 0, getImgWidth());
		}
		return pixels;
	}

	public void applyChanges() {
		// if the pixels array obtained by getPixels() has been modified,
		// call this method to make your changes visible
		if(pixels != null) setPixels(pixels);
	}
	
	public void setPixels(int[] pix) {
		// set pixels with same dimension
		setPixels(pix, getImgWidth(), getImgHeight());
	}
	
	public void setPixels(int[] pix, int width, int height) {
		// set pixels with arbitrary dimension
		if(pix == null || pix.length != width * height) throw new IndexOutOfBoundsException();
	
		if(width != getImgWidth() || height != getImgHeight()) {
			// image dimension changed
			screen.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			pixels = null;
		}
		
		screen.image.setRGB(0, 0, width, height, pix, 0, width);
		if(pixels != null && pix != pixels) {
			// update internal pixels array
			System.arraycopy(pix, 0, pixels, 0, Math.min(pix.length, pixels.length));
		}
		
		Dimension size = new Dimension(maxSize);
		if(size.width - borderX > width) 
			size.width = width + borderX;
		if(size.height - borderY > height) 
			size.height = height + borderY;
		setPreferredSize(size);

		screen.invalidate();
		screen.repaint();
	}
	
	public double getMaxViewMagnification() {
		return maxViewMagnification;
	}
	
	// set 0.0 to disable limits
	//
	public void setMaxViewMagnification(double mag) {
		maxViewMagnification = mag;
	}
	
	public boolean getKeepAspectRatio() {
		return keepAspectRatio;
	}
	
	public void setKeepAspectRatio(boolean keep) {
		keepAspectRatio = keep;
	}

	public void printText(int x, int y, String text) {
		Graphics2D g = screen.image.createGraphics();
		 
		Font font = new Font("TimesRoman", Font.BOLD, 12);
		g.setFont(font);
		g.setPaint(Color.black);
		g.drawString(text, x, y);		
		g.dispose();
		
		updatePixels();	// update the internal pixels array
	}
	
	public void clearImage() {
		Graphics2D g = screen.image.createGraphics();
		
		g.setColor(Color.white);
		g.fillRect(0, 0, getImgWidth(), getImgHeight());
		g.dispose();

		updatePixels();	// update the internal pixels array
	}
	
	public void loadImage(File file) {
		// load image from file
		BufferedImage bi = null;
		boolean success = false;
		
		try {
			bi = ImageIO.read(file);
			success = true;
		} catch (Exception e) {
   		 	JOptionPane.showMessageDialog(this, "Bild konnte nicht geladen werden.", "Fehler", JOptionPane.ERROR_MESSAGE);
   		 	bi = new BufferedImage(200, 150, BufferedImage.TYPE_INT_RGB);
		}
		
		init(bi, !success);
		
		if(!success) printText(5, getImgHeight()/2, "Bild konnte nicht geladen werden.");
	}
	
	public void saveImage(String fileName) {
		try {
			File file = new File(fileName);
			String ext = (fileName.lastIndexOf(".")==-1)?"":fileName.substring(fileName.lastIndexOf(".")+1,fileName.length());
			if(!ImageIO.write(screen.image, ext, file)) throw new Exception("Image save failed");
		} catch(Exception e)  {
   		 	JOptionPane.showMessageDialog(this, "Bild konnte nicht geschrieben werden.", "Fehler", JOptionPane.ERROR_MESSAGE);			
		}
	}

	private void init(BufferedImage bi, boolean clear)
	{
		screen = new ImageScreen(bi);
		setViewportView(screen);
		screen.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				System.out.println(e.getX());
				System.out.println(e.getY());
				clickPositionX = e.getX();
				clickPositionY = e.getY();	
				updateScreen();
			}
		});
				
		maxSize = new Dimension(getPreferredSize());
		
		if(borderX < 0) 
			borderX = maxSize.width - bi.getWidth();
		if(borderY < 0) 
			borderY = maxSize.height - bi.getHeight();
		
		if(clear) clearImage();
		
		pixels = null;
	}
	
	public void setGrit(boolean gtit) {
		this.grid = gtit;
	}
	
	private void updatePixels() {
		if(pixels != null) screen.image.getRGB(0, 0, getImgWidth(), getImgHeight(), pixels, 0, getImgWidth());
	}
	
	class ImageScreen extends JComponent {
		
		private static final long serialVersionUID = 1L;
		
		private BufferedImage image = null;

		public ImageScreen(BufferedImage bi) {
			super();
			image = bi;
		}
		
		public void paintComponent(Graphics g) {
			 super.paintComponent(g);
			
			if (image != null) {
				Rectangle r = this.getBounds();
								
				if(maxViewMagnification > 0.0) {
					int maxWidth = (int)(image.getWidth() * maxViewMagnification + 0.5);
					int maxHeight = (int)(image.getHeight() * maxViewMagnification + 0.5);
					maxWidth = (int) ((int) maxWidth * zoom);
					maxHeight = (int) ((int) maxHeight * zoom);
					if(r.width  > maxWidth) r.width = maxWidth;
					if(r.height  > maxHeight) r.height = maxHeight;
				}
				
				// keep aspect ratio
				if(keepAspectRatio) {
					double ratioX = (double)r.width / image.getWidth();
					double ratioY = (double)r.height / image.getHeight();
					if(ratioX < ratioY)
						r.height = (int)(ratioX * image.getHeight() + 0.5);
					else
						r.width = (int)(ratioY * image.getWidth() + 0.5);
				}
				
//				int offsetX = 0;
//				int offsetY = 0;
				
				Graphics2D g2d = (Graphics2D) g.create();
				
				//Zeichne Originalbild
				if(!viewIsSrcView){
					g2d.drawImage(image, 0, 0, r.width, r.height, this);
				}
				//SRC-View soll immer gezeichnet werden
				else{
					g2d.drawImage(image, 0, 0, r.width, r.height, this);
				}
				g2d.setStroke(new BasicStroke(2));
				//Kontouren zeichnen
				for(int c = 0; c < contoures.length; c++){
					
					Contoure contoure = contoures[c];
					if(contoure.isOutline()) {
						g2d.setColor(Color.GRAY);
					
						Vector2 bb[] = contoure.getBoundingBox();
						if (bb[0].x < clickPositionX && clickPositionX < bb[1].x &&
							bb[0].y < clickPositionY && clickPositionY < bb[1].y) {
							
							g2d.draw(new Line2D.Double(bb[0].x*zoom, bb[0].y*zoom, bb[0].x*zoom, bb[1].y*zoom));
							g2d.draw(new Line2D.Double(bb[0].x*zoom, bb[1].y*zoom, bb[1].x*zoom, bb[1].y*zoom));
							g2d.draw(new Line2D.Double(bb[1].x*zoom, bb[1].y*zoom, bb[1].x*zoom, bb[0].y*zoom));
							g2d.draw(new Line2D.Double(bb[1].x*zoom, bb[0].y*zoom, bb[0].x*zoom, bb[0].y*zoom));
							
							g2d.setColor(Color.RED);
							Ellipse2D circle = new Ellipse2D.Float((float)(contoure.getMainEmphasi().x*zoom) - 0.5f , (float) (contoure.getMainEmphasi().y*zoom) - 0.5f, (float) (0.5*10), (float) (0.5*10));
							g2d.draw(circle);	
						}
					}																		
				}
				g2d.dispose();
			}
		}
		
		public Dimension getPreferredSize() {
			if(image != null) 
				return new Dimension((int) (image.getWidth() * zoom), (int) (image.getHeight() * zoom));
			else
				return new Dimension(100, 60);
		}
	}

}
