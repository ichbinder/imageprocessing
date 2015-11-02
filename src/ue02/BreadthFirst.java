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

public class BreadthFirst {

	private int lengthOfQ = 0;

	public BreadthFirst(){
		
	}

	/**Bildet ein 1-dimensionales in ein 2-dimensionales Pixel-Array
	 * @param src Pixeldaten (1 dim)
	 * @param width Bildbreite
	 * @param height Bildhöhe
	 * @return 2-dimensionales Pixel-Array*/
	private int [][] prepareBinaryImage(int [] src, int width, int height){
		
		int [][] pixels = new int [height][width];
		int i = 0;
		
		for(int h = 0; h < height; h++){
			
			for(int w = 0; w < width; w++){
				
				pixels[h][w] = src[i];
				i++;
			}
		}		
		return pixels;
	}
	
	
	public void RegionLabeling(int [] input, int width, int height){
		int m = 2;
		
		int[][] pixels = prepareBinaryImage(input, width, height);
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
		
		int outputPixels[] = new int [width * height];
		int i = 0;
		for(int h = 0; h < height; h++){
			for(int w = 0; w < width; w++){
				
				
				outputPixels[i] = pixels[h][w];
				i++;
			}
		}
		System.arraycopy(outputPixels, 0, input, 0, outputPixels.length);
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
}
