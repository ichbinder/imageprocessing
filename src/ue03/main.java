package ue03;

import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class main {
	
	private int arrowDirection = 0;


	public static void main(String[] args) throws InterruptedException, IOException {
		// TODO Auto-generated method stub
		File imgFile = new File("/home/jakob/git/imageprocessing/klein.png");
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

	public void potrace(int[][] pixels, int y, int x) {
		int[] start = { y, x };
		int[] end = {0, 0};
		int[] patern = {0, 0, 0, 0};

		while (!(start == end)) {
			end[0] = y;
			end[1] = x;
				
				while (arrowDirection != 360) {
					if (arrowDirection == 0) {
						System.out.println(pixels[end[0]][end[1] - 1] + " " + end[0] + ":" + (end[1] - 1));
						System.out.println(pixels[end[0]][end[1]] + " " + (end[0]) + ":" + (end[1]));
						System.out.println(pixels[end[0] + 1][end[1] - 1] + " " + (end[0] + 1) + ":" + (end[1] - 1));
						System.out.println(pixels[end[0] + 1][end[1]] + " " + (end[0] + 1) + ":" + (end[1]));
						
						patern[0] = pixels[end[0]][end[1] - 1] == -1 ? 0 : 1;
						patern[1] = pixels[end[0]][end[1]] == -1 ? 0 : 1;
						patern[2] = pixels[end[0] + 1][end[1] - 1] == -1 ? 0 : 1;
						patern[3] = pixels[end[0] + 1][end[1]] == -1 ? 0 : 1;
						patern = rotate(180, patern);
						checkPatern(patern);
					} else if (arrowDirection == 90) {
						System.out.println(pixels[end[0]][end[1]] + " " + end[0] + ":" + end[1]);
						System.out.println(pixels[end[0]][end[1] + 1] + " " + (end[0]) + ":" + (end[1] + 1));
						System.out.println(pixels[end[0] + 1][end[1]] + " " + (end[0] + 1) + ":" + (end[1]));
						System.out.println(pixels[end[0] + 1][end[1] + 1] + " " + (end[0] + 1) + ":" + (end[1] + 1));
						
						patern[0] = pixels[end[0]][end[1]] == -1 ? 0 : 1;
						patern[1] = pixels[end[0]][end[1] + 1] == -1 ? 0 : 1;
						patern[2] = pixels[end[0] + 1][end[1]] == -1 ? 0 : 1;
						patern[3] = pixels[end[0] + 1][end[1] + 1] == -1 ? 0 : 1;
						patern = rotate(90, patern);
						checkPatern(patern);
					} else if (arrowDirection == 180) {
						System.out.println(pixels[end[0] - 1][end[1]] + " " + (end[0] - 1) + ":" + end[1]);
						System.out.println(pixels[end[0] - 1][end[1] + 1] + " " + (end[0] - 1) + ":" + (end[1] + 1));
						System.out.println(pixels[end[0]][end[1]] + " " + (end[0]) + ":" + (end[1]));
						System.out.println(pixels[end[0]][end[1] + 1] + " " + (end[0]) + ":" + (end[1] + 1));
						
						patern[0] = pixels[end[0] - 1][end[1]] == -1 ? 0 : 1;
						patern[1] = pixels[end[0] - 1][end[1] + 1] == -1 ? 0 : 1;
						patern[2] = pixels[end[0]][end[1]] == -1 ? 0 : 1;
						patern[3] = pixels[end[0]][end[1] + 1] == -1 ? 0 : 1;
						checkPatern(patern);
					} else if (arrowDirection == 270) {
						System.out.println(pixels[end[0] - 1][end[1] - 1] + " " + (end[0] - 1) + ":" + (end[1] - 1));
						System.out.println(pixels[end[0] - 1][end[1]] + " " + (end[0] - 1) + ":" + (end[1]));
						System.out.println(pixels[end[0]][end[1] - 1] + " " + (end[0]) + ":" + (end[1] - 1));
						System.out.println(pixels[end[0]][end[1]] + " " + (end[0]) + ":" + (end[1]));
						
						patern[0] = pixels[end[0] - 1][end[1] - 1] == -1 ? 0 : 1;
						patern[1] = pixels[end[0] - 1][end[1]] == -1 ? 0 : 1;
						patern[2] = pixels[end[0]][end[1] - 1] == -1 ? 0 : 1;
						patern[3] = pixels[end[0]][end[1]] == -1 ? 0 : 1;
						patern = rotate(270, patern);
						checkPatern(patern);
					}
					
					if (arrowDirection == 270)
						arrowDirection = 0;
					else
						arrowDirection = arrowDirection + 90;					
				}
//			}
		}
	}

	/*
	 * Patern id:
	 * 
	 * |0|1| 
	 * |2|3|
	 */

	private void checkPatern(int[] patern) {
		if (patern.length != 4) 
			throw new IllegalArgumentException();
		if (patern[0] == 1 & patern[1] == 1 & patern[2] == 1 & patern[3] == 0) {
			System.out.println("nach rechts");
		} else if (patern[0] == 1 && patern[1] == 0 && patern[2] == 1 && patern[3] == 0) {
			System.out.println("gradeaus");
		} else if (patern[0] == 1 && patern[1] == 0 && patern[2] == 0 && patern[3] == 0) {
			System.out.println("nach links");
		} else if (patern[0] == 0 && patern[1] == 1 && patern[2] == 0 && patern[3] == 1) {
			System.out.println("?nach Rechts");
		}
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

}
