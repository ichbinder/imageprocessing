package ue02;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import java.awt.image.DataBufferByte;


public class Main {

	public static void main(String[] args) throws InterruptedException, IOException {
		
//		 img = Toolkit.getDefaultToolkit().createImage("src/ue02/META-INF/klein.png");
		File f = new File("src/ue02/META-INF/klein.png");
		BufferedImage img = ImageIO.read(f);
		RegionLabeling(img);

	}
	
	private static Image RegionLabeling(BufferedImage img) throws InterruptedException {
//		PixelGrabber pg = new PixelGrabber(img, 0, 0, -1, -1, false);
//		pg.grabPixels(); // Throws InterruptedException
		int m = 2;
		final int width = img.getWidth();
	    final int height = img.getHeight();

		int[][] pixels = convertTo2DWithoutUsingGetRGB(img);
		
		for(int h = 0; h < height; h++){
    		for(int w = 0; w < width; w++){
    			if ((pixels[h][w] & 0x00ffffff) == 0) {
    				System.out.println(Integer.toHexString(pixels[h][w]));
    				
    			}
    		}
		}
		
		return null;
	}
	
	private static void FloodFill(BufferedImage img, int u, int v, int label) {
		
	}
	
	private static int[][] convertTo2DWithoutUsingGetRGB(BufferedImage image) {

	      final byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
	      final int width = image.getWidth();
	      final int height = image.getHeight();
	      final boolean hasAlphaChannel = image.getAlphaRaster() != null;

	      int[][] result = new int[height][width];
	      if (hasAlphaChannel) {
	         final int pixelLength = 4;
	         for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
	            int argb = 0;
	            argb += (((int) pixels[pixel] & 0xff) << 24); // alpha
	            argb += ((int) pixels[pixel + 1] & 0xff); // blue
	            argb += (((int) pixels[pixel + 2] & 0xff) << 8); // green
	            argb += (((int) pixels[pixel + 3] & 0xff) << 16); // red
	            result[row][col] = argb;
	            col++;
	            if (col == width) {
	               col = 0;
	               row++;
	            }
	         }
	      } else {
	         final int pixelLength = 3;
	         for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
	            int argb = 0;
	            argb += -16777216; // 255 alpha
	            argb += ((int) pixels[pixel] & 0xff); // blue
	            argb += (((int) pixels[pixel + 1] & 0xff) << 8); // green
	            argb += (((int) pixels[pixel + 2] & 0xff) << 16); // red
	            result[row][col] = argb;
	            col++;
	            if (col == width) {
	               col = 0;
	               row++;
	            }
	         }
	      }

	      return result;
	   }
}
