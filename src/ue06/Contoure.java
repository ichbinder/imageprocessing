package ue06;

public class Contoure {

	final private boolean isOutline;
	final private Vector2 [] vectors;
	final private Vector2 [] boundingBox;
	
	/**Erzeugt eine Kontur . 
	 * @param isOut Ist es eine Außenkontur?
	 * @param ps Enthält ein Array aus Path-Points.*/
	public Contoure(boolean isOut, Vector2 [] ps){
		
		this.isOutline = isOut;
		this.vectors = ps;
		this.boundingBox = new Vector2[2];
		calcBoundingBox();
		
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
}
