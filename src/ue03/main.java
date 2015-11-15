package ue03;

import java.util.HashSet;
import java.util.Set;
import java.awt.Point;

public class main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		int [] array = new int []{0,1,1,1};
		
		double arrowDirection = 270;
		Point [] pixelCoordinates = new Point[] { new Point(1,2), new Point(1,3), new Point(2,2), new Point(2,3)};
		
		System.out.println(array[0] + "|" +array[1]);
		System.out.println(array[2] + "|" +array[3]);
		
		main m = new main();
/*		
		int [] rotate = m.rotate(90, array);
		System.out.println("----90");
		System.out.println(rotate[0] + "|" +rotate[1]);
		System.out.println(rotate[2] + "|" +rotate[3]);
		*/
		System.out.println("----180");
		
		int [] rotate2 = m.rotate(180, array);
		
		System.out.println(rotate2[0] + "|" +rotate2[1]);
		System.out.println(rotate2[2] + "|" +rotate2[3]);
		
		System.out.println("FORWARD");
		
		
/*		
		System.out.println("----270");
		
		int [] rotate3 = m.rotate(270, array);
		
		System.out.println(rotate3[0] + "|" +rotate3[1]);
		System.out.println(rotate3[2] + "|" +rotate3[3]);
*/				
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
	
	public void RegionLabeling(int [] input, int width, int height) {
		int m = 2;

		int[][] pixels = prepareBinaryImage(input, width, height);

		long timeStart = System.nanoTime();
		
		for (int h = 0; h < height; h++) {
			for (int w = 0; w < width; w++) {
				if ((pixels[h][w] & 0x00ffffff) == 0) {
//					pixels = this.BreadthFirst8Without(pixels, h, w, m);
					m = m + 1;
//					System.out.println(pixels[h][w]);
				}
			}
		}
		
		int outputPixels[] = new int [width * height];
		int i = 0;
		for(int h = 0; h < height; h++){
			for(int w = 0; w < width; w++){
				
				
				outputPixels[i] = pixels[h][w];
				i++;
			}
		}
		System.arraycopy(outputPixels, 0, input, 0, input.length);
		
		long timeStop = System.nanoTime();
		
		System.out.println("Calc Time: " + (timeStop - timeStart));
//		System.out.println("Stack max length: " + lengthOfS);		
	}
	
	public void potrace(int[][] pixels, int height, int width) {
		int[] start = {height, width};
		Set edge = new HashSet<>();
		int[] end = {};
		
		while (!(start==end)) {
			if (end[0] > -1 && end[0] < pixels[0].length && end[1] > -1 && end[1] < pixels[1].length
					&& (pixels[end[0]][end[1]] & 0x00ffffff) == 0) {	
				
			}
		}
	}
	
	private int [] rotate(double degree, int [] input){
		
		int [] rotatedArray = new int [4];
		
//		System.arraycopy(input, 0, rotatedArray, 0, rotatedArray.length);
		
		if(degree == 90){
			rotatedArray[0] = input[1];
			rotatedArray[1] = input[3];
			rotatedArray[2] = input[0];
			rotatedArray[3] = input[2];			
		}
		
		else if (degree == 180){
			
			rotatedArray = rotate(90, input);			
			rotatedArray = rotate(90, rotatedArray);
		}
		else if (degree == 270){
			
			rotatedArray = rotate(180, input);			
			rotatedArray = rotate(90, rotatedArray);			
		}
		
		return rotatedArray;
	}

}
