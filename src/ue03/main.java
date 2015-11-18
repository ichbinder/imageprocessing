package ue03;

import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import javax.imageio.ImageIO;

import com.sun.javafx.geom.Vec2d;

public class main {

	public static void main(String[] args) throws InterruptedException, IOException {
		// TODO Auto-generated method stub
//		File imgFile = new File("/home/jakob/git/imageprocessing/klein.png");
		File imgFile = new File("./klein.png");
		System.out.println(imgFile.getAbsolutePath());
		
	    BufferedImage hugeImage = ImageIO.read(imgFile);
		PixelGrabber pg = new PixelGrabber(hugeImage, 0, 0, hugeImage.getWidth(), hugeImage.getHeight(), false);

		pg.grabPixels(); // Throws InterruptedException

		int[] pixels = (int[])pg.getPixels();
		main main = new main();
		main.RegionLabeling(pixels, pg.getWidth(), pg.getHeight());
	}

	/**
	 * Bildet ein 1-dimensionales in ein 2-dimensionales Pixel-Array
	 * 
	 * @param src
	 *            Pixeldaten (1 dim)
	 * @param width
	 *            Bildbreite
	 * @param height
	 *            Bildhöhe
	 * @return 2-dimensionales Pixel-Array
	 */
	private int[][] prepareBinaryImage(int[] src, int width, int height) {

		int[][] pixels = new int[height][width];
		int i = 0;

		for (int h = 0; h < height; h++) {

			for (int w = 0; w < width; w++) {

				pixels[h][w] = src[i];
				i++;
			}
		}
		return pixels;
	}

	public void RegionLabeling(int[] input, int width, int height) {

		int[][] pixels = prepareBinaryImage(input, width, height);

		long timeStart = System.nanoTime();

		for (int h = 0; h < height; h++) {
			for (int w = 0; w < width; w++) {
				if ((pixels[h][w] & 0x00ffffff) == 0) {
					potrace(pixels, h, w);
				}
			}
		}

		int outputPixels[] = new int[width * height];
		int i = 0;
		for (int h = 0; h < height; h++) {
			for (int w = 0; w < width; w++) {

				outputPixels[i] = pixels[h][w];
				i++;
			}
		}
		System.arraycopy(outputPixels, 0, input, 0, input.length);

		long timeStop = System.nanoTime();

		System.out.println("Calc Time: " + (timeStop - timeStart));
	}

	
	private int convertToLabeling(int value){
		
		return value == -1 ? 0 : 1; 
	}
	private void convertPattern(int [][] pattern){
		
		for(int i = 0; i < 2; i++){			
			for(int j = 0; j < 2; j++) pattern[i][j] = convertToLabeling(pattern[i][j]);
		}
	}
	
	public void potrace(int[][] pixels, int x, int y) {

		Queue vecQueue = new LinkedList<Vec2d>();
		Vec2d vg2d = new Vec2d();		
		int[] start = { x, y };
		int[] end = {0, 0};
		int arrowDirection = 0;
		int[] patern = {0, 0, 0, 0};		
		
		int[][] pattern2D = new int [2][2];
		
		vg2d.set(x, y);
		
		vecQueue.add(vg2d);

		while (!(start == end)) {
			end[0] = x;
			end[1] = y;
//			v
				
				while (arrowDirection != 360) {
					if (arrowDirection == 0) {
						
						pattern2D[0][0] = pixels[y][x];
						pattern2D[0][1] = pixels[y][x+1];
						pattern2D[1][0] = pixels[y+1][x];
						pattern2D[1][1] = pixels[y+1][x+1];

						//Convert to readable numbers
						convertPattern(pattern2D);
						
						System.out.println(pixels[end[0]][end[1]] + " " + end[0] + ":" + end[1]);
						System.out.println(pixels[end[0]][end[1] - 1] + " " + (end[0]) + ":" + (end[1] - 1));
						System.out.println(pixels[end[0] + 1][end[1]] + " " + (end[0] + 1) + ":" + (end[1]));
						System.out.println(pixels[end[0] + 1][end[1] - 1] + " " + (end[0] + 1) + ":" + (end[1] - 1));
						
						patern[1] = pixels[end[0]][end[1] - 1] == -1 ? 0 : 1;
						patern[2] = pixels[end[0] + 1][end[1]] == -1 ? 0 : 1;
						patern[3] = pixels[end[0] + 1][end[1] - 1] == -1 ? 0 : 1;
												
//						patern = rotate(180, patern);
						checkPattern(patern, 180);
						
						
						
						
					} else if (arrowDirection == 90) {
						System.out.println(pixels[end[0]][end[1]] + " " + end[0] + ":" + end[1]);
						System.out.println(pixels[end[0] + 1][end[1]] + " " + (end[0] + 1) + ":" + (end[1]));
						System.out.println(pixels[end[0]][end[1] + 1] + " " + (end[0]) + ":" + (end[1] + 1));
						System.out.println(pixels[end[0] + 1][end[1] + 1] + " " + (end[0] + 1) + ":" + (end[1] + 1));
						
						patern[1] = pixels[end[0] + 1][end[1]] == -1 ? 0 : 1;
						patern[2] = pixels[end[0]][end[1] + 1] == -1 ? 0 : 1;
						patern[3] = pixels[end[0] + 1][end[1] + 1] == -1 ? 0 : 1;
//						patern = rotate(90, patern);
						checkPattern(patern, 90);
												
					} else if (arrowDirection == 180) {
						System.out.println(pixels[end[0]][end[1]] + " " + end[0] + ":" + end[1]);
						System.out.println(pixels[end[0]][end[1] + 1] + " " + (end[0]) + ":" + (end[1] + 1));
						System.out.println(pixels[end[0] - 1][end[1]] + " " + (end[0] - 1) + ":" + (end[1]));
						System.out.println(pixels[end[0] - 1][end[1] + 1] + " " + (end[0] - 1) + ":" + (end[1] + 1));
						
						patern[1] = pixels[end[0]][end[1] + 1] == -1 ? 0 : 1;
						patern[2] = pixels[end[0] - 1][end[1]] == -1 ? 0 : 1;
						patern[3] = pixels[end[0] - 1][end[1] + 1] == -1 ? 0 : 1;
						
						checkPattern(patern, 0);
						
					} else if (arrowDirection == 270) {
						System.out.println(pixels[end[0]][end[1]] + " " + end[0] + ":" + end[1]);
						System.out.println(pixels[end[0] - 1][end[1]] + " " + (end[0] - 1) + ":" + (end[1]));
						System.out.println(pixels[end[0]][end[1] - 1] + " " + (end[0]) + ":" + (end[1] - 1));
						System.out.println(pixels[end[0] - 1][end[1] - 1] + " " + (end[0] - 1) + ":" + (end[1] - 1));
						
						patern[1] = pixels[end[0] - 1][end[1]] == -1 ? 0 : 1;
						patern[2] = pixels[end[0]][end[1] - 1] == -1 ? 0 : 1;
						patern[3] = pixels[end[0] - 1][end[1] - 1] == -1 ? 0 : 1;
						
//						patern = rotate(270, patern);
						checkPattern(patern,270);
					}
					arrowDirection = arrowDirection + 90; 
					
				}
//			}
		}
	}

	/*
	 * Patern id:
	 * 
	 * |2|3| 
	 * |0|1|
	 */
	
	
	private void checkPattern(int[][] pattern, int rotate) {
		
		//Rotiere hier erst das Pattern
		pattern = rotate(rotate, pattern);
		
		if (pattern.length != 4) 
			throw new IllegalArgumentException();
		
		//Rechts abbiegen // -> 
		if (pattern[0][0] == 0 & pattern[0][1] == 0 & pattern[1][0] == 0 & pattern[1][1] == 1) {
			pattern[0][1] = 2;
			//Hier steht was.
		} 
		//Geradeaus
		else if (pattern[0][0] == 0 && pattern[0][1] == 1 && pattern[1][0] == 0 && pattern[1][1] == 1) {
			pattern[0][0] = 2;			
		}
		//Links abbiegen
		else if (pattern[0][0] == 1 && pattern[0][1] == 1 && pattern[1][0] == 0 && pattern[1][1] == 1) {
		
		} 		
		else if (pattern[0][0] == 1 && pattern[0][1] == 1 && pattern[1][0] == 0 && pattern[1][1] == 1) {
			
		}
		//Rotiere das Pattern zurück		
		pattern = rotate(360 - rotate, pattern);
	}


	private void checkPattern(int[] pattern, int rotate) {
		
		//Rotiere hier erst das Pattern
		pattern = rotate(rotate, pattern);
		
		if (pattern.length != 4) 
			throw new IllegalArgumentException();
		
		//Rechts abbiegen // -> 
		if (pattern[0] == 0 & pattern[1] == 0 & pattern[2] == 0 & pattern[3] == 1) {			
			pattern[1] = 2;
		} 
		//Geradeaus
		else if (pattern[0] == 0 && pattern[1] == 1 && pattern[2] == 0 && pattern[3] == 1) {
			pattern[0] = 2;
			
		}//Links abbiegen
		else if (pattern[0] == 1 && pattern[1] == 1 && pattern[2] == 0 && pattern[3] == 1) {
		} else if (pattern[0] == 1 && pattern[1] == 0 && pattern[2] == 0 && pattern[3] == 1) {
			
		}
		//Rotiere das Pattern zurück		
		pattern = rotate(360 - rotate, pattern);
	}

	private int[] rotate(double degree, int[] input) {

		int[] rotatedArray = new int[4];

		if (degree == 90) {
			rotatedArray[0] = input[1];
			rotatedArray[1] = input[3];
			rotatedArray[2] = input[0];
			rotatedArray[3] = input[2];
		} else if (degree == 180) {
			rotatedArray = rotate(90, input);
			rotatedArray = rotate(90, rotatedArray);
		} else if (degree == 270) {
			rotatedArray = rotate(180, input);
			rotatedArray = rotate(90, rotatedArray);
		}

		return rotatedArray;
	}
	
	private int[][] rotate(double degree, int[][] input) {

		int[][] rotatedArray = new int[2][2];

		if (degree == 90) {
			rotatedArray[0][0] = input[0][1];
			rotatedArray[0][1] = input[1][2];
			rotatedArray[1][0] = input[0][0];
			rotatedArray[1][1] = input[1][0];
		} else if (degree == 180) {
			rotatedArray = rotate(90, input);
			rotatedArray = rotate(90, rotatedArray);
		} else if (degree == 270) {
			rotatedArray = rotate(180, input);
			rotatedArray = rotate(90, rotatedArray);
		}

		return rotatedArray;
	}


}
