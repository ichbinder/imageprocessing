package ue06;

public class Contoure {

	final private int [][] mutherImage;
	final private boolean isOutline;
	final private Vector2 [] vectors;
	final private Vector2 [] boundingBox;
	private int blackPixelCount;
	final private Vector2 mainEmphasi;
	private float countX;
	private float countY;
	
	/**Erzeugt eine Kontur . 
	 * @param isOut Ist es eine Außenkontur?
	 * @param ps Enthält ein Array aus Path-Points.*/
	public Contoure(boolean isOut, Vector2 [] ps, int [][] mutherImage){
		
		this.isOutline = isOut;
		this.vectors = ps;
		this.mutherImage = mutherImage;
		this.boundingBox = new Vector2[2];
		this.blackPixelCount = 0;
		this.mainEmphasi = new Vector2();
		this.countX = 0;
		this.countY = 0;
		calcBoundingBox();
		calcHowManyBlackPixels();	
		calcMainEmphasi();
	}
	
	private void calcBoundingBox() {
		Vector2 bbMax = new Vector2();
		Vector2 bbMin = new Vector2(Float.MAX_VALUE, Float.MAX_VALUE);
		for (int i = 0; i < this.vectors.length; i++) {
			if (this.vectors[i].x > bbMax.x)
				bbMax.setX(this.vectors[i].x);
			if (this.vectors[i].y > bbMax.y)
				bbMax.setY(this.vectors[i].y);
			if (this.vectors[i].x < bbMin.x)
				bbMin.setX(this.vectors[i].x);
			if (this.vectors[i].y < bbMin.y)
				bbMin.setY(this.vectors[i].y);
		}
		this.boundingBox[0] = bbMin;
		this.boundingBox[1] = bbMax;
	}
	
	private void calcHowManyBlackPixels() {
		for (int y = 0; y < this.mutherImage[0].length; y++) {
			for (int x = 0; x < this.mutherImage[1].length; x++) {
				if (x >= this.boundingBox[0].x && y >= this.boundingBox[0].y
						&& x < this.boundingBox[1].x && y < this.boundingBox[1].y) {
					if (this.mutherImage[y][x] == 1) {
						this.blackPixelCount += 1;
						this.countX = this.countX + x;
						this.countY = this.countY + y;
					}
				}
			}			
		}
	}
	
	private void calcMainEmphasi() {
		this.mainEmphasi.x = this.countX / blackPixelCount;
		this.mainEmphasi.y = this.countY / blackPixelCount;
	}
		
	/**Gibt die enthaltenen Punkte (2D-Vektoren) zurück
	 * @return Ein Array aus 2D-Vektoren.*/
	public Vector2 [] getVectors() {
		return this.vectors;
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
		return this.vectors[index];
	}
	
	public Vector2[] getBoundingBox() {
		return this.boundingBox;
	}
	
	public Vector2 getMainEmphasi() {
		return this.mainEmphasi;
	}
}
