package ue02;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.ArrayBlockingQueue;

import javax.imageio.ImageIO;

import java.awt.image.DataBufferByte;

public class BreadthFirstJakob {

	private int lengthOfQ = 0;

	public static void main(String[] args) throws InterruptedException, IOException {
		BreadthFirstJakob fillJakob = new BreadthFirstJakob();
	}

	public BreadthFirstJakob() throws InterruptedException, IOException {
		File f = new File("src/ue02/META-INF/tools.png");
		BufferedImage img = ImageIO.read(f);
		this.RegionLabeling(img);
	}

	private Image RegionLabeling(BufferedImage img) throws InterruptedException {
		int m = 2;
		final int width = img.getWidth();
		final int height = img.getHeight();
		int[][] pixels = convertTo2DWithoutUsingGetRGB(img);

		BufferedImage imgOutput = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		long timeStart = System.nanoTime();
		
		for (int h = 0; h < height; h++) {
			for (int w = 0; w < width; w++) {
				if ((pixels[h][w] & 0x00ffffff) == 0) {
					pixels = this.BreadthFirst8Without(pixels, h, w, m);
					m = m + 1;
					System.out.println(pixels[h][w]);
				}
			}
		}
		
		long timeStop = System.nanoTime();
		
		System.out.println("Calc Time: " + (timeStop - timeStart));
		System.out.println("Queue max length: " + lengthOfQ);
		
	    for(int i=0; i < height; i++)
	        for(int j=0; j < width; j++)
	        	imgOutput.setRGB(j, i, pixels[i][j]);
	    
		try {
			File f = new File("src/ue02/META-INF/Output.png");
			ImageIO.write(imgOutput, "png", f);
		} catch (IOException e) {
			System.out.println("Error: " + e);
		}

		return null;
	}

	private int[][] BreadthFirst8(int[][] pixels, int u, int v, int label) {
		ArrayBlockingQueue<int[]> q = new ArrayBlockingQueue<>(pixels[0].length * pixels[1].length);
		int[] xy = { u, v };
		q.offer(xy);

		int a = 255; // alpha
		int r = (int) (Math.random() * 256); // red
		int g = (int) (Math.random() * 256); // green
		int b = (int) (Math.random() * 256); // blue

		int p = (a << 24) | (r << 16) | (g << 8) | b;
		
		while (!q.isEmpty()) {
			
			if (lengthOfQ < q.toArray().length)
				lengthOfQ = q.toArray().length;
			
			xy = q.remove();
			
			if (xy[0] > -1 && xy[0] < pixels[0].length && xy[1] > -1 && xy[1] < pixels[1].length
					&& (pixels[xy[0]][xy[1]] & 0x00ffffff) == 0) {

				pixels[xy[0]][xy[1]] = p;

				int[] tmpxy = { (xy[0] + 1), xy[1] };
				q.offer(tmpxy);
				int[] tmpxy2 = { xy[0], xy[1] + 1 };
				q.offer(tmpxy2);
				int[] tmpxy3 = { xy[0], xy[1] - 1 };
				q.offer(tmpxy3);
				int[] tmpxy4 = { xy[0] - 1, xy[1] };
				q.offer(tmpxy4);
				int[] tmpxy5 = { xy[0] - 1, xy[1] - 1 };
				q.offer(tmpxy5);
				int[] tmpxy6 = { xy[0] - 1, xy[1] + 1 };
				q.offer(tmpxy6);
				int[] tmpxy7 = { xy[0] + 1, xy[1] - 1 };
				q.offer(tmpxy7);
				int[] tmpxy8 = { xy[0] + 1, xy[1] + 1 };
				q.offer(tmpxy8);
			}
		}
		return pixels;
	}
	
	private int[][] BreadthFirst8Without(int[][] pixels, int u, int v, int label) {
		ArrayBlockingQueue<int[]> q = new ArrayBlockingQueue<>(pixels[0].length * pixels[1].length);
		int[] xy = { u, v };
		q.offer(xy);

		int a = 255; // alpha
		int r = (int) (Math.random() * 256); // red
		int g = (int) (Math.random() * 256); // green
		int b = (int) (Math.random() * 256); // blue

		int randomColor = (a << 24) | (r << 16) | (g << 8) | b;
		
		while (!q.isEmpty()) {
			
			if (lengthOfQ < q.toArray().length)
				lengthOfQ = q.toArray().length;
			
			xy = q.remove();
			
			if (xy[0] > -1 && xy[0] < pixels[0].length && xy[1] > -1 && xy[1] < pixels[1].length
					&& (pixels[xy[0]][xy[1]] & 0x00ffffff) == 0) {

				pixels[xy[0]][xy[1]] = randomColor;
				
				if ((xy[0] + 1) < pixels[0].length && 
					(pixels[(xy[0] + 1)][xy[1]] & 0x00ffffff) == 0) {
					int[] tmpxy = { (xy[0] + 1), xy[1] };
					q.offer(tmpxy);
				}
				if ((xy[1] + 1) < pixels[1].length && 
					(pixels[xy[0]][(xy[1] + 1)] & 0x00ffffff) == 0) {
					int[] tmpxy2 = { xy[0], xy[1] + 1 };
					q.offer(tmpxy2);
				}
				if ((xy[1] - 1) > -1 && 
					(pixels[xy[0]][(xy[1] - 1)] & 0x00ffffff) == 0) {
					int[] tmpxy3 = { xy[0], xy[1] - 1 };
					q.offer(tmpxy3);
				}
				if ((xy[0] - 1) > -1 && 
					(pixels[(xy[0] - 1)][xy[1]] & 0x00ffffff) == 0) {
					int[] tmpxy4 = { xy[0] - 1, xy[1] };
					q.offer(tmpxy4);
				}
				if ((xy[0] - 1) > -1 && (xy[1] - 1) > -1 &&
					(pixels[(xy[0] - 1)][(xy[1] - 1)] & 0x00ffffff) == 0) {
					int[] tmpxy5 = { xy[0] - 1, xy[1] - 1 };
					q.offer(tmpxy5);
				}
				if ((xy[0] - 1) > -1 && (xy[1] + 1) < pixels[1].length &&
						(pixels[(xy[0] - 1)][(xy[1] + 1)] & 0x00ffffff) == 0) {
					int[] tmpxy6 = { xy[0] - 1, xy[1] + 1 };
					q.offer(tmpxy6);
				}
				if ((xy[0] + 1) < pixels[0].length && (xy[1] - 1) > -1 &&
						(pixels[(xy[0] + 1)][(xy[1] - 1)] & 0x00ffffff) == 0) {
					int[] tmpxy7 = { xy[0] + 1, xy[1] - 1 };
					q.offer(tmpxy7);
				}
				if ((xy[0] + 1) < pixels[0].length && (xy[1] + 1) < pixels[1].length &&
						(pixels[(xy[0] + 1)][(xy[1] + 1)] & 0x00ffffff) == 0) {
					int[] tmpxy8 = { xy[0] + 1, xy[1] + 1 };
					q.offer(tmpxy8);
				}
			}
		}
		return pixels;
	}
	
	private int[][] BreadthFirst4(int[][] pixels, int u, int v, int label) {
		ArrayBlockingQueue<int[]> q = new ArrayBlockingQueue<>(pixels[0].length * pixels[1].length);
		
		int[] xy = { u, v };
		q.offer(xy);

		int a = 255; // alpha
		int r = (int) (Math.random() * 256); // red
		int g = (int) (Math.random() * 256); // green
		int b = (int) (Math.random() * 256); // blue

		int p = (a << 24) | (r << 16) | (g << 8) | b;

		while (!q.isEmpty()) {
			xy = q.remove();
			if (xy[0] > -1 && xy[0] < pixels[0].length && xy[1] > -1 && xy[1] < pixels[1].length
					&& (pixels[xy[0]][xy[1]] & 0x00ffffff) == 0) {

				pixels[xy[0]][xy[1]] = p;

				int[] tmpxy = { (xy[0] + 1), xy[1] };
				q.offer(tmpxy);
				int[] tmpxy2 = { xy[0], xy[1] + 1 };
				q.offer(tmpxy2);
				int[] tmpxy3 = { xy[0], xy[1] - 1 };
				q.offer(tmpxy3);
				int[] tmpxy4 = { xy[0] - 1, xy[1] };
				q.offer(tmpxy4);
			}
		}
		return pixels;
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
