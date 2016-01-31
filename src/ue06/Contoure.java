package ue06;

public class Contoure {

	final private int [][] motherImage;
	final private boolean isOutline;
	final private Vector2 [] potraceContoure; 
	final private Vector2 [] boundingBox;
	final private Vector2 mainEmphasis;
	private double [][] momente;
	private double [][] centralMoment;
	private double [][] normCentralMoment;

	
	/**Erzeugt eine Kontur . 
	 * @param isOut Ist es eine Außenkontur?
	 * @param ps Enthält ein Array aus Path-Points.*/
	public Contoure(boolean isOut, Vector2 [] ps, int [][] motherImage){
		
		this.isOutline = isOut;
		this.potraceContoure = ps;
		this.motherImage = motherImage;
		this.boundingBox = new Vector2[2];
		
		this.mainEmphasis = new Vector2();
		
		this.momente = new double[4][4];
		this.centralMoment = new double[4][4];
		this.normCentralMoment = new double[4][4];
		

		
		//Start der Berechnung für die Momente und den Schwerpunkt des Objektes (der Contoure)
		if (this.isOutline) {
			
			calcBoundingBox();
			
			double m00 = calcMoment(0, 0);
			this.mainEmphasis.x = calcMoment(1, 0) / m00;
			this.mainEmphasis.y = calcMoment(0, 1) / m00;
			
			for (int q = 0; q < this.momente[0].length; q++) {
				for (int p = 0; p < this.momente[1].length; p++) {
					this.momente[p][q] = calcMoment(p, q);
					this.centralMoment[p][q] = calcCentralMoment(p, q);
					this.normCentralMoment[p][q] = calcNormCentralMoment(m00, p, q);
				}
			}
			
			
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
					System.out.printf("Zentral Moment(%d,%d) =%25.3f%n", i, j, this.centralMoment[i][j]);
				}
			}
			for (int i = 0; i < this.momente[0].length; i++) {
				for (int j = 0; j < this.momente[1].length; j++) {
					if (this.isOutline)
					System.out.printf("Norm Zentral Moment(%d,%d) =%25.4f%n", i, j, this.normCentralMoment[i][j]);
				}
			}
			System.out.println("Schwerpunkt x: " + this.mainEmphasis.x);
			System.out.println("Schwerpunkt y: " + this.mainEmphasis.y);
			System.out.println("BoundingBox MIN: " + this.boundingBox[0]);
			System.out.println("BoundingBox MAX: " + this.boundingBox[1]);
			System.out.println("-------------- End Contoure------------------");
			System.out.println(" ");
		} else {
			this.mainEmphasis.x = 0.0;
			this.mainEmphasis.y = 0.0;
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
	 * Berechnung des Momentes
	 */
	private double calcMoment(int p, int q) {
		double Mpq = 0.0;
		for (int y = (int) this.boundingBox[0].y; y < (int ) this.boundingBox[1].y; y++) 
			for (int x = (int) this.boundingBox[0].x; x < (int) this.boundingBox[1].x; x++) 
				if (this.motherImage[y][x] == 1)  // ist der aktuelle Pixel ein Schwarzer Pixel
					Mpq += Math.pow(x, p) * Math.pow(y, q);	
		return Mpq;
	}
	
	/**
	 * Berechnung des Zentralen Momentes
	 */
	private double calcCentralMoment(int p, int q) {	
		double cMpq = 0.0;
		for (int y = (int) this.boundingBox[0].y; y <= (int ) this.boundingBox[1].y; y++) 
			for (int x = (int) this.boundingBox[0].x; x <= (int) this.boundingBox[1].x; x++) 
				if (this.motherImage[y][x] == 1)
					cMpq += Math.pow(x - this.mainEmphasis.x, p) * Math.pow(y - this.mainEmphasis.y, q);

		return cMpq;
	}
	
	private double calcNormCentralMoment(double m00, int p, int q) {
		double norm = Math.pow(m00,  0.5 * (p + q + 2));
		return calcCentralMoment(p, q) / norm;
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
