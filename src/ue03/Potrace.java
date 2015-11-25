package ue03;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class Potrace {
	
	private int[][] pixels;
	private int WIDTH, HEIGHT;

	public enum PathDirection {LEFT, RIGHT, UP, DOWN};	
	private PathDirection direction;
	
	private ArrayList<Point[]> collectedPaths;
	private Contoure[] contoures;
	
	public Potrace(){
		direction = PathDirection.RIGHT;		
		collectedPaths = new ArrayList<Point[]>();
	}
	
	
/*	
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
		main.FindContoures(pixels, pg.getWidth(), pg.getHeight());
	}
*/
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

	/**Sucht Konturen in einem Bild. Der erste schwarze Pixel sucht dann nach einer Kontur.
	 * Alle Punkte werden in einem Array gespeichert (Point []). 
	 * Danach werden alle gefundenen Pixel invertiert. Alle Punkt-Konturen werden in einer ArrayList gespeichert (ArrayList<Point[]>).
	 * Alle gefundenen Konturen werden als Contoure-Objekte gespeichert.
	 * @param input Das gesamte Bild in Pixeln
	 * @param width Breite des Bildes
	 * @param height Höhe des Bildes
	 * */
	public void FindContoures(int[] input, int width, int height) {

		WIDTH = width;
		HEIGHT = height;
		pixels = prepareBinaryImage(input, width, height);
		
		//Create a copy
		int [][] pixelCopy = copyPixels(pixels, height, width);		

		//1. Finde einen schwarzen Pixel, bilde Außenkontur und invertiere Kontur
		for (int h = 0; h < height; h++) {
			for (int w = 0; w < width; w++) {
				if (pixelCopy[h][w] == 1) {
					
					potrace(pixelCopy, h, w);
				}
			}
		}
		
		//2.  Ordne zu ob Innen / Außenkante -> Baue Contoure-Object
		createContoures();
	}

	/**Erstellung der Konturen als Contoure-Objekte. Der erste Punkt wird als Anhaltspunkt genommen um zu prüfen ob es eine 
	 * Außen- oder Innenkontur ist. Es wird ein Pattern gebildet und geprüft ob es eine Außen- oder Innenkontur ist.
	 * */
	private void createContoures(){
		
		contoures = new Contoure[collectedPaths.size()];
		
		int index = 0;
		for(Point [] points : collectedPaths){
			
			int [][] checkPattern = createPattern(pixels, points[0], WIDTH, HEIGHT);
			Contoure contoure = createConture(checkPattern, points);

			contoures[index] = contoure;
			index++;
		}
		
	}

	/**Erzeugt ein Kontur-Objekt (Contoure-Object).
	 * Es wird hierbei geprüft ob es eine innere oder äußere Kontur ist.
	 * Das Pattern in Form eines 2x2 Quadrats wird auf die erste Kontur untersucht. 
	 * Ist die schwarze Seite links ist es eine äußere Kontur. Ist sie rechts liegt sie innen.
	 * 
	 * @param pattern Quadrat mit betrachteten Pixeln
	 * @param points Alle Punkte für eine Kontur
	 * @return eine Kontur */
	private Contoure createConture(int [][] pattern, Point [] points){
		
		boolean isOutline = true;		
		Point v = points[0];
		Point w = points[1];
		
		int left;
		//Richtung zeigt nach unten
		if(v.y < w.y){
//			int right = pattern[1][0]; //Unterer -> Linker Pixel
			left = pattern[1][1]; //Unterer -> Rechter Pixel
		}
		//Richtung nach Rechts
		else if(v.x < w.x){
			
			left = pattern[0][1]; //Oberer -> Rechter Pixel
//			int right = pattern[1][1]; //Unterer -> Rechter Pixel			
		}
		//Richtung nach Links
		else if(w.x < v.x){
			
//			int right = pattern[0][0]; // Oberer-> Linker Pixel
			left = pattern[1][0]; //Unterer -> Linker Pixel	
		}
		//Richtung nach oben
		else {
			
			left = pattern[0][0]; // Oberer-> Linker Pixel
//			int right = pattern[0][1]; //Oberer -> Linker Pixel
		}
		if(left == 1) isOutline = true;
		else isOutline = false;			

		return new Contoure(isOutline, points);
	}
	
	/**Einstiegspunkt für das Finden einer einzelnen Kontur.
	 * 
	 * @param pixels alle Pixel des Bildes (auch schon bearbeitete)
	 * @param y aktueller schwarzer Punkt in Y-Richtung
 	 * @param x aktueller schwarzer Punkt in X-Richtung
	 * */
	public void potrace(int[][] pixels, int y, int x) {

		Point [] points = findPath(pixels, x, y);		
		collectedPaths.add(points);	
		removeOutline(points, pixels);
	}
	
	/**Kopiert ein zwei-dimensionales Pixel-Array
	 * 
	 * @param original Original-Pixel-Array
	 * @param h y-Koordinate
	 * @param w x-Koordinate
	 * */
	private int [][] copyPixels(int [][] original, int h, int w){
		
		int [][] copy = new int [h][w];
		
		for(int i = 0; i < h; i++){
			
			System.arraycopy(original[i], 0, copy[i], 0, copy[i].length);
		}
		return copy;
	}
		
	/**Sucht einen Konturpfad.
	 * 
	 * @param pixels Alle Pixel vom Bild
	 * @param x X-Koordinate des ersten schwarzen Pixels
	 * @param y Y-Koordinate des ersten schwarzen Pixels
	 * */
	private Point [] findPath(int [][] pixels, int x, int y){	
		ArrayList<Point> path = new ArrayList<Point>();
		
		Point startPoint = new Point(x, y);
		Point endPoint = new Point(x, y);		
		path.add(startPoint);
		
		boolean diffX = false;
		boolean diffY = false;
		boolean together = true;
		
		do {
			endPoint = nextPointForPath(pixels, endPoint);
			path.add(endPoint);
			
			diffX = false;
			diffY = false;
			if(startPoint.x != endPoint.x) diffX = true;
			if(startPoint.y != endPoint.y) diffY = true;			
			if(diffX == false &&  diffY == false) together = false;
			
		} while (together);

		return path.toArray(new Point[0]);
	}
	
	/**Invertiert die schwarzen Pixel der schon gefundenen Kontur und innere Konturen werden sichtbar (weiß zu schwarz) 
	 * 
	 * @param path Alle Punkte einer Kontur
	 * @param pixels Alle Pixel (auch die bearbeiteten) des Bildes
	 * */
	private void removeOutline(Point[] path, int [][] pixels){

		Point lastPoint = path[0];
		int lastY = lastPoint.y;
		
		for(int i = 1; i < path.length; i++){

			Point p = path[i];
			if(p.y > lastY){
				invertLine(pixels, p.x, lastY);				
			}
			else if(p.y < lastY){
				invertLine(pixels, p.x, p.y);
			}
			lastY = p.y;
		}		
	}

	/**Invertiert eine Zeile ab dem gegebenen X-Koordinatenpunkt.
	 * 
	 * @param pixels Pixel-Array
	 * @param x X-Koordinate
	 * @param y Y-Koordinate
	 * */
	private void invertLine(int [][] pixels, int x, int y){
		
		for(int i = x; i < pixels[y].length; i++){	
			
			if(pixels[y][i] == 1) pixels[y][i] = 0;
			else pixels[y][i] = 1;
		}
	}
	
	/**Gibt die nächste Richtungs zurück welche "Gegen-den-Uhrzeigersinn" läuft.
	 * 
	 * @param possiblePoints Alle 4 Richtungen (Links, Oben, Rechts, Unten)
	 * @param dir aktuelle nächste Richtung (Links -> Unten)
	 * */
	private Point getNextPos(Point [] possiblePoints, PathDirection dir){
		
		if(dir == PathDirection.LEFT) return possiblePoints[0];
		else if(dir == PathDirection.UP) return possiblePoints[1];
		else if(dir == PathDirection.RIGHT) return possiblePoints[2];
		else return possiblePoints[3];
	}
	
	/**Erzeugt ein Pattern 2x2 Pixel-Quadrat.
	 * Es wird ebenso Randbehandlung berücksichtigt.
	 * 
	 * @param pixels Alle Pixel des Bildes
	 * @param currentPoint aktueller Betrachtungspunkt
	 * @param width Breite des Bildes für Randbehandlung
	 * @param height Höhe des Bildes für Randbehandlung
	 * @return 2x2 Pixel-Array mit betrachteten Pixeln
	 * */
	private int [][] createPattern(int [][] pixels, Point currentPoint, int width, int height){
		
		int y = currentPoint.y, x = currentPoint.x;
		
		int pattern2D [][] = new int [2][2];
		
		if(y-1 >= 0 && x-1 >= 0 && y-1 < height && x-1 < width) pattern2D[0][0] = pixels[y-1][x-1];
		if(y-1 >= 0 && y-1 < height && x < width) pattern2D[0][1] = pixels[y-1][x];
		if (x-1 >= 0 && x-1 < width && y < height) pattern2D[1][0] = pixels[y][x-1];
		if (x < width && y < height) pattern2D[1][1] = pixels[y][x];

		return pattern2D;
	}

	/**Gibt alle Punkte die betrachtet werden können von einem Bezugspunkt aus. Die Reihenfolge ist Links|Oben|Rechts|Unten.
	 * 
	 * @param currentPoint der aktuelle Bezugspunkt 
	 * @return Alle vier Punkte die erreicht werden können.
	 * */
	private Point [] createLookupPoints(Point currentPoint){
		
		Point [] points = new Point[4];
		
		int x = currentPoint.x;
		int y = currentPoint.y;

		points[0] = new Point(x-1, y); //Links
		points[1] = new Point(x, y-1); //Oben
		points[2] = new Point(x+1, y);// Rechts	
		points[3] = new Point(x, y+1); //Unten

		return points;
	}
	
	/**Prüft in welche Richtung als nächstes gegangen werden soll.
	 * 1. Pattern wird an aktueller Stelle erzeugt
	 * 2. Erzeugte alle 4 möglichen Richtungspunkte 
	 * 3. Durch die aktuelle Laufrichtung wird das Pattern so gedreht das immer die Betrachtete Richtung von Unten nach Oben zeigt.
	 * 4. Es wird geprüft welche Richtung im gedrehten Pattern genommen wird (Links, Oben, Rechts)
	 * 5. Die gefundene Richtung aus 4 wird entgegengesetzt gedreht zur Drehung des Patterns (Bsp: Patterndrehung 90°, Gegendrehung 270°).
	 * 6. Der nächste Punkt im Bezug zur Abbiege-Richtung aus Schritt 5 wird aus dem Array vom Schritt 2 zurückgegeben.
	 * 
	 * @param pixels Alle Pixel des Bildes
	 * @param lastPoint der aktuelle Betrachtungspunkt
	 * @return Den nächsten Betrachtungspunkt (zum Bilden der Kontur)*/
	private Point nextPointForPath(int[][] pixels, Point lastPoint) {
				
		Point nextPoint = null;
		//Erzeuge Pattern aus Bildpixeln		
		int [][] pattern = createPattern(pixels, lastPoint, pixels[0].length, pixels.length);		
//		printPixels(pattern);
		
		Point[] lookupPoints = createLookupPoints(lastPoint);
		
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

	/**Gibt ein rotiertes 2-dimensionales Array zurück.
	 * 
	 * @param degree Die Drehung in Grad (Wertebereich liegt bei: 90,180,270)
	 * @param input das zu rotierende 2-dimensionale Array
	 * @return Ein rotiertes 2-dimensionales Array*/
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

	/**Rotiert eine gegebene Richtung gegen den Uhrzeigersinn.
	 * 
	 * @param rotate Rotierungswinkel (Wertebereich: 90,180,270)
	 * @param dir Die Richtung die gedreht werden soll
	 * @return Die gedrehte Richtung.*/
	private PathDirection rotateDirection(int rotate, PathDirection dir){
		
		if(rotate == 90)		return getNextDirection(dir);
		else if (rotate == 180)	return getNextDirection(getNextDirection(dir));
		else if(rotate == 270) return getNextDirection(getNextDirection(getNextDirection(dir)));
		else return dir;
	}
	
	/**Gibt die nächste Richtung zurück, welche gegen den Uhrzeigersinn läuft.
	 * 
	 * @param dir Aktuelle Richtung
	 * @return Nächste Richtung */
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
	
	/**Gibt die Pixel für die Konsole aus.
	 * @param pix Die auszugebenen Pixel*/
	private void printPixels(int [][] pix){
		
		for(int h = 0; h < pix.length; h++){
			for(int w = 0; w < pix[h].length; w++){
				System.out.print("" + pix[h][w] + " | ");			
			}
			System.out.println();
		}
		System.out.println("---------------");
	}		
	
	/**Gibt die gefundenen Konturen zurück.
	 * @return gefundene Konturen*/
	public Contoure[] getContoures(){
		
		return contoures;
	}
	
	/**Setzt die gefundenen Konturen, 
	 * die letzte Laufrichtung 
	 * und die gesammelten Pfade zurück.*/
	public void reset(){		
		contoures = null;				
		direction = PathDirection.RIGHT;		
		collectedPaths = new ArrayList<Point[]>();
	}
}
