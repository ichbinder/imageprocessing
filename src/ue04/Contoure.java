package ue04;


public class Contoure {

	final private boolean isOutline;
	final private Vector2 [] vectors;
	
	public Contoure(boolean isOut, Vector2 [] ps){
		
		this.isOutline = isOut;
		this.vectors = ps;
	}
		
	public Vector2 [] getVectors(){
		
		return this.vectors;
	}
	
	public boolean isOutline(){
		
		return this.isOutline;
	}
	
	public Vector2 getVector(int index){
		return this.vectors[index];
	}
}
