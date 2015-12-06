package ue04;

import java.util.HashMap;
import java.util.Map;

public class Contoure {

	final private boolean isOutline;
	final private Vector2 [] vectors;
	private Map<Integer, Object> straightPath;
	
	public Contoure(boolean isOut, Vector2 [] ps){
		
		this.isOutline = isOut;
		this.vectors = ps;
		this.straightPath = new HashMap<Integer, Object>();
	}
		
	public Vector2 [] getVectors() {
		return this.vectors;
	}
	
	public boolean isOutline() {
		return this.isOutline;
	}
	
	public Vector2 getVector(int index){
		return this.vectors[index];
	}
	
	public void setStraightPath(int key, int value) {
		this.straightPath.put(key, value);
	}
	
	public Map getStraightPath() {
		return this.straightPath;
	}
}
