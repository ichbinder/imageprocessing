package ue03;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import javax.imageio.ImageIO;

public class Potrace {
	
	private int[][] pixels;

	public Queue<Point> outSidePaths;
	public enum PathDirection {LEFT, RIGHT, UP, DOWN};
	
	private PathDirection direction;
	
	public Potrace(){
		direction = PathDirection.RIGHT;
		outSidePaths = new LinkedList<Point>();
	}
	
	
	public static void main(String[] args) throws InterruptedException, IOException {
		// TODO Auto-generated method stub
//		File imgFile = new File("/home/jakob/git/imageprocessing/klein.png");
		File imgFile = new File("./test2.png");
		System.out.println(imgFile.getAbsolutePath());
		
	    BufferedImage hugeImage = ImageIO.read(imgFile);
		PixelGrabber pg = new PixelGrabber(hugeImage, 0, 0, hugeImage.getWidth(), hugeImage.getHeight(), false);

		pg.grabPixels(); // Throws InterruptedException

		int[] pixels = (int[])pg.getPixels();
		Potrace main = new Potrace();
		main.RegionLabeling(pixels, pg.getWidth(), pg.getHeight());
	}

	/**
	 * Bildet ein 1-dimensionales in ein 2-dimensionales Pixel-Array und setzt Label für Vordergrund- (1) 
	 * und Hintergrundpixel (0).
	 * 
	 * @param src Pixeldaten (1 dim)
	 * @param width Bildbreite
	 * @param height Bildhöhe
	 * @return 2-dimensionales Pixel-Array
	 */
	private int[][] prepareBinaryImage(int[] src, int width, int height) {

		int[][] pixels = new int[height][width];
		int i = 0;

		for (int h = 0; h < height; h++) {

			for (int w = 0; w < width; w++) {

				//Sollte Wert 0 (Schwarz) sein, trage 1 als Vordergrund ein, sonst 0 für Hintergrund (Weiß)
				pixels[h][w] = src[i] == -1 ? 0 : 1;
				i++;
			}
		}
		return pixels;
	}

	public int [] RegionLabeling(int[] input, int width, int height) {

		pixels = prepareBinaryImage(input, width, height);
		int [] outputPixels = new int[input.length];
		long timeStart = System.nanoTime();

		boolean testStop = false;
		for (int h = 0; h < height; h++) {
			if(testStop) break;
			for (int w = 0; w < width; w++) {
				if (pixels[h][w] == 1) {
					potrace(pixels, h, w, height, width);
					testStop = true;
					break;
				}
			}
		}
		
		int i = 0;
		for(int h = 0; h < height; h++){
			
			for(int w = 0; w < width; w++){
				outputPixels[i] = pixels[h][w];
				i++;
			}
		}
		return outputPixels;
	}

	
	private int convertToLabeling(int value){
		
		return value == -1 ? 0 : 1; 
	}
	
	
	
	public int getIntFromColor(int Red, int Green, int Blue){
	    Red = (Red << 16) & 0x00FF0000; //Shift red 16-bits and mask out other stuff
	    Green = (Green << 8) & 0x0000FF00; //Shift Green 8-bits and mask out other stuff
	    Blue = Blue & 0x000000FF; //Mask out anything not blue.

	    return 0xFF000000 | Red | Green | Blue; //0xFF000000 for 100% Alpha. Bitwise OR everything together.
	}
	
	public void potrace(int[][] pixels, int y, int x, int h, int w) {

		Queue<Point> outerPath = findPath(pixels, x, y);
		
		outSidePaths.addAll(outerPath);
		
		int color = getIntFromColor(255, 0, 0);
		
		drawShape(pixels, outSidePaths, color);
		
		
		/*
		//Copy Pixels		
		int [][] insidePixels = copyPixels(pixels, h, w);
		retrieveInsidePixels(outerPath, insidePixels);	
		printPixels(pixels);
		Queue<Point> insidePath = findPath(insidePixels, x, y);				
		*/
	}
		
	private void drawShape(int pixels [][], Queue<Point> path, int color){

		if(path.isEmpty()) return;

		int lastY = path.poll().y;
		while(!path.isEmpty()){

			Point p = path.poll();
			if(p.y > lastY){
				pixels[lastY][p.x] = color;				
			}
			else if(p.y < lastY){
				pixels[p.y][p.x] = color;
			}
			lastY = p.y;
		}		
	}
	
	
	private Point[][] getMinimumMaximumPath(Queue<Point> points){
		
		Point [][] minMaxPoints = null;
		
		return minMaxPoints;
	}
	
	private int [][] copyPixels(int [][] original, int h, int w){
		
		int [][] copy = new int [h][w];
		
		for(int i = 0; i < h; i++){
			
			for(int j = 0; j < w; j++){
				System.arraycopy(copy[i], 0, original[i], 0, original.length);
			}
		}
		return copy;
	}
		
	private Queue<Point> findPath(int [][] pixels, int x, int y){
		
		Queue<Point> path = new LinkedList<Point>();
		Point startPoint = new Point(x, y);
		Point endPoint = new Point(x, y);		

		path.add(startPoint);
		printPixels(pixels);
		System.out.println("---------------");
		
		boolean diffX = false;
		boolean diffY = false;
		boolean together = true;
		
		do {
			endPoint = checkPattern(pixels,y,x, endPoint);
			path.add(endPoint);
			
			diffX = false;
			diffY = false;
			if(startPoint.x != endPoint.x) diffX = true;
			if(startPoint.y != endPoint.y) diffY = true;			
			if(diffX == false &&  diffY == false) together = false;
			
		} while (together);

		return path;
	}
	
	private void retrieveInsidePixels(Queue<Point> path, int [][] pixels){

		Queue<Point> collect = new LinkedList<Point>();
		Point lastPoint = path.poll();
		int lastY = lastPoint.y;
		
		collect.add(lastPoint);
		while(!path.isEmpty()){

			Point p = path.poll();
			if(p.y > lastY){
				invertLine(pixels, p.x, lastY);
				
//				invertLine(pixels, p.x, p.y - lastY);
			}
			else if(p.y < lastY){
				invertLine(pixels, p.x, p.y);
			}
			lastY = p.y;
			collect.add(p);
		}		
	}
	
	private void invertLine(int [][] pixels, int x, int y){
		
		for(int i = x; i < pixels[y].length; i++){	
			
			if(pixels[y][i] == 1) pixels[y][i] = 0;
			else pixels[y][i] = 1;
		}
	}
	

	private Point getNextPos(Point [] possiblePoints, PathDirection dir){
		
		if(dir == PathDirection.LEFT) return possiblePoints[0];
		else if(dir == PathDirection.UP) return possiblePoints[1];
		else if(dir == PathDirection.RIGHT) return possiblePoints[2];
		else return possiblePoints[3];
	}
	
	/**BENÖTIGT RANDBEHANDLUNG*/
	private int [][] createPattern(int [][] pixels, Point currentPoint){
		
		int y = currentPoint.y, x = currentPoint.x;
		
		int pattern2D [][] = new int [2][2];
		pattern2D[0][0] = pixels[y-1][x-1];
		pattern2D[0][1] = pixels[y-1][x];
		pattern2D[1][0] = pixels[y][x-1];
		pattern2D[1][1] = pixels[y][x];

		return pattern2D;
	}
	
	
	private int [][] createPattern(int [][] pixels, int y, int x, int dir){
		
		int pattern2D [][] = new int [2][2];

		if(dir == 0){
				pattern2D[0][0] = pixels[y][x-1];
				pattern2D[0][1] = pixels[y][x];
				pattern2D[1][0] = pixels[y+1][x];
				pattern2D[1][1] = pixels[y+1][x+1];			
		}
		else if (dir == 90){
			
			pattern2D[0][0] = pixels[y][x-1];
			pattern2D[0][1] = pixels[y][x];
			pattern2D[1][0] = pixels[y+1][x];
			pattern2D[1][1] = pixels[y+1][x+1];						
		}
		
		return pattern2D;
	}
	
	
	private Point [] createLookupPoints(PathDirection dir, Point currentPoint){
		
		Point [] points = new Point[4];
		
		int x = currentPoint.x;
		int y = currentPoint.y;

		points[0] = new Point(x-1, y); //Links
		points[1] = new Point(x, y-1); //Oben
		points[2] = new Point(x+1, y);// Rechts	
		points[3] = new Point(x, y+1); //Unten


		return points;
	}
	
	
	private Point checkPattern(int[][] pixels, int y, int x, Point lastPoint) {
				
		Point nextPoint = null;
		//Erzeuge Pattern aus Bildpixeln		
		int [][] pattern = createPattern(pixels, lastPoint);		
		printPixels(pattern);
		
		Point[] lookupPoints = createLookupPoints(direction,lastPoint);
		
		//Rotiere Pattern damit Kantenstartpunkt immer unten liegt.		
		int rotateAngle = 0;
		if(direction == PathDirection.RIGHT) rotateAngle = 90;
		else if(direction == PathDirection.DOWN) rotateAngle = 180;
		else if(direction == PathDirection.LEFT) rotateAngle = 270;
		
		//Rotiere das Pattern
		pattern = rotate(rotateAngle, pattern);		
		PathDirection curveAngle = null; //Abbiegerichtung
		
		//--------- Prüfabfragen müssen Randbehandlung (weniger Pixel zum Vergleichen) behandeln
		
		//Rechts abbiegen
		if (pattern[0][0] == 1 & pattern[0][1] == 1 & pattern[1][0] == 1 & pattern[1][1] == 0) {			
			
			curveAngle = PathDirection.RIGHT;			
		} 
		//Geradeaus
		else if (pattern[0][0] == 1 && pattern[0][1] == 0 && pattern[1][0] == 1 && pattern[1][1] == 0) {

			curveAngle = PathDirection.UP;
		}
		//Links abbiegen
		else if (pattern[0][0] == 0 && pattern[0][1] == 0 && pattern[1][0] == 1 && pattern[1][1] == 0) {
		
			curveAngle = PathDirection.LEFT;
		} 		
		else {
//		else if (pattern[0][0] == 0 && pattern[0][1] == 0 && pattern[1][0] == 1 && pattern[1][1] == 0) {
			
			//Wieder rechts abbiegen weil Regel -> Immer rechts
			curveAngle = PathDirection.RIGHT;
		}

		//Rotiere hier die echte Richung heraus
		direction = rotateDirection(360-rotateAngle, curveAngle);

		//Nächster Vector-Punkt
		nextPoint = getNextPos(lookupPoints, direction);
		
		return nextPoint;
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
			rotatedArray[0][1] = input[1][1];
			rotatedArray[1][0] = input[0][0];
			rotatedArray[1][1] = input[1][0];
		} else if (degree == 180) {
			rotatedArray = rotate(90, input);
			rotatedArray = rotate(90, rotatedArray);
		} else if (degree == 270) {
			rotatedArray = rotate(180, input);
			rotatedArray = rotate(90, rotatedArray);
		}else{return input;}

		return rotatedArray;
	}

	
	private PathDirection rotateDirection(int rotate, PathDirection dir){
		
		if(rotate == 90)		return getNextDirection(dir);
		else if (rotate == 180)	return getNextDirection(getNextDirection(dir));
		else if(rotate == 270) return getNextDirection(getNextDirection(getNextDirection(dir)));
		else return dir;
	}
	
	
	private PathDirection getNextDirection(PathDirection dir){
		ArrayList<PathDirection> directions = new ArrayList<PathDirection>() {{ 
			
			add(PathDirection.LEFT);
			add(PathDirection.DOWN);			
			add(PathDirection.RIGHT);
			add(PathDirection.UP);
		}};

		int index = directions.indexOf(dir);
		if(index == 3) index = 0;
		else index++;		
		
		return directions.get(index);
	}
	
	
	private void printPixels(int [][] pix){
		
		for(int h = 0; h < pix.length; h++){
			for(int w = 0; w < pix[h].length; w++){
				System.out.print("" + pix[h][w] + " | ");			
			}
			System.out.println();
		}
		System.out.println("---------------");

	}
	
	public class DirectionPoint extends Point{

		PathDirection lookingDirection;
		public DirectionPoint(int x, int y, PathDirection dir){
			
			
		}
		
	}
}