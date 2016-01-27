package ue06;

public class Contoure {

	final private int [][] motherImage;
	final private boolean isOutline;
	final private Vector2 [] potraceContoure; 
	final private Vector2 [] boundingBox;
	private int blackPixelCount;
	final private Vector2 mainEmphasis;
	private double countX;
	private double countY;
	private double [][] momente;
	private double [][] zentralMomente;

	
	/**Erzeugt eine Kontur . 
	 * @param isOut Ist es eine Außenkontur?
	 * @param ps Enthält ein Array aus Path-Points.*/
	public Contoure(boolean isOut, Vector2 [] ps, int [][] motherImage){
		
		this.isOutline = isOut;
		this.potraceContoure = ps;
		this.motherImage = motherImage;
		this.boundingBox = new Vector2[2];
		
		this.blackPixelCount = 0;
		this.mainEmphasis = new Vector2();
		this.countX = 0;
		this.countY = 0;
		
		this.momente = new double[4][4];
		this.zentralMomente = new double[4][4];
		
		//Start der Berechnung für die Momente und den Schwerpunkt des Objektes (der Contoure)
		if (this.isOutline) {
			
			calcBoundingBox();
			calcHowManyBlackPixels();
			calcMainEmphasi();
			momente();
			zentralMoment();
			
			// Ausgabe der einzelenen Daten: Moment, Zentral Moment, Schwerpunkt, Anzahl Schwarzerpixel, BoundingBox
			System.out.println("-------------- Start Contoure---------------");
			for (int i = 0; i < this.momente[0].length; i++) {
				for (int j = 0; j < this.momente[1].length; j++) {
					if (this.isOutline)
					System.out.printf("Moment(%d,%d) =%25.0f%n", i, j, this.momente[i][j]);
				}
			}
			for (int i = 0; i < this.momente[0].length; i++) {
				for (int j = 0; j < this.momente[1].length; j++) {
					if (this.isOutline)
					System.out.printf("Zentral Moment(%d,%d) =%25.0f%n", i, j, this.zentralMomente[i][j]);
				}
			}
			System.out.println("Schwerpunkt x: " + this.mainEmphasis.x);
			System.out.println("Schwerpunkt y: " + this.mainEmphasis.y);
			System.out.println("Anzahl ScharzerPixel: " + this.blackPixelCount);
			System.out.println("BoundingBox MIN: " + this.boundingBox[0]);
			System.out.println("BoundingBox MAX: " + this.boundingBox[1]);
			System.out.println("-------------- End Contoure------------------");
			System.out.println(" ");
		}
		
	}
	
	/**
	 * Berechnung der BoundingBox aus den Kuntur-Pixeln 
	 */
	private void calcBoundingBox() {
		Vector2 bbMax = new Vector2();
		Vector2 bbMin = new Vector2(Float.MAX_VALUE, Float.MAX_VALUE);
		for (int i = 0; i < this.potraceContoure.length; i++) {
			if (this.potraceContoure[i].x > bbMax.x)
				bbMax.setX(this.potraceContoure[i].x);
			if (this.potraceContoure[i].y > bbMax.y)
				bbMax.setY(this.potraceContoure[i].y);
			if (this.potraceContoure[i].x < bbMin.x)
				bbMin.setX(this.potraceContoure[i].x);
			if (this.potraceContoure[i].y < bbMin.y)
				bbMin.setY(this.potraceContoure[i].y);
		}
		this.boundingBox[0] = bbMin;
		this.boundingBox[1] = bbMax;
	}
	
	/**
	 * Berechnung wie viel Schwarze Pixel enthält das komplette Objekt
	 * und das Addieren von x-Werten und y-Werten für den Schwerpunkt
	 */
	private void calcHowManyBlackPixels() {
//		int count = 0;
		for (int y = (int) this.boundingBox[0].y; y < (int ) this.boundingBox[1].y; y++) {
			for (int x = (int) this.boundingBox[0].x; x < (int) this.boundingBox[1].x; x++) {							
				if (this.motherImage[y][x] == 1) { // ist der aktuelle Pixel ein Schwarzer Pixel
					this.blackPixelCount += 1;
					this.countY = this.countY + (double)y;
					this.countX = this.countX + (double)x;	
//					count++;
				}

				
			}	
//			System.out.println("Count: "+count);
		}
	}	

	/**
	 * Berechnung des Schwerpunktes 
	 */
	private void calcMainEmphasi() {
		this.mainEmphasis.x =  this.countX / (double)this.blackPixelCount;
		this.mainEmphasis.y =  this.countY / (double)this.blackPixelCount;
	}
	
	/**
	 * Berechnung des Momentes
	 */
	private void momente() {
		for (int y = (int) this.boundingBox[0].y; y < (int ) this.boundingBox[1].y; y++) {
			for (int x = (int) this.boundingBox[0].x; x < (int) this.boundingBox[1].x; x++) {
				if (this.motherImage[y][x] == 1) { // ist der aktuelle Pixel ein Schwarzer Pixel
					for (int p = 0; p < this.momente[0].length; p++) {
						for (int q = 0; q < this.momente[1].length; q++) {
							this.momente[p][q] = (this.momente[p][q] + (Math.pow(x, p) * Math.pow(y, q)));
						}
					}
				}
			}
		}
	}
	
	/**
	 * Berechnung des Zentralen Momentes
	 */
	private void zentralMoment() {	
		for (int y = (int) this.boundingBox[0].y; y < (int ) this.boundingBox[1].y; y++) {
			for (int x = (int) this.boundingBox[0].x; x < (int) this.boundingBox[1].x; x++) {
				for (int p = 0; p < this.zentralMomente[0].length; p++) {
					for (int q = 0; q < this.zentralMomente[1].length; q++) {
						this.zentralMomente[p][q] = (Math.pow(((double)x - this.mainEmphasis.x), (double)p) * Math.pow(((double)y - this.mainEmphasis.y), (double)q));
					}
				}
			
			}
		}
	}
	
	/**Gibt die enthaltenen Punkte (2D-Vektoren) zurück
	 * @return Ein Array aus 2D-Vektoren.*/
	public Vector2 [] getVectors() {
		return this.potraceContoure;
	}
	
	/**Gibt an ob es sich um eine Außen- oder Innenkontur handelt.
	 * @return Ist es eine Außenkontur?*/
	public boolean isOutline() {
		return this.isOutline;
	}
	
	/**Beschreibung
	 * @param index Ist der Index im Vektorarray.
	 * @return Den gesuchten Vektor
	 * */
	public Vector2 getVector(int index){
		return this.potraceContoure[index];
	}
	
	public Vector2[] getBoundingBox() {
		return this.boundingBox;
	}
	
	public Vector2 getMainEmphasi() {
		return this.mainEmphasis;
	}
}
