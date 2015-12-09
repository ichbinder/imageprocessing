package ue04;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Contoure {

	final private boolean isOutline;
	final private Vector2 [] vectors;
	private Map<Integer, Object> straightPathVectors;// speichert alle möglichen StraigthPath Vektoren 
	private ArrayList<HashMap<Integer, Object>> straightPaths; // speichert alle möglichen StraigthPathsesees
	
	public Contoure(boolean isOut, Vector2 [] ps){
		
		this.isOutline = isOut;
		this.vectors = ps;
		this.straightPathVectors = new HashMap<Integer, Object>(); 
		this.straightPaths = new ArrayList<HashMap<Integer, Object>>(); 
	}
	
//	Liefert den besten StraigthPath zurück 
	public Map<Integer, Object> getBestStraigthPath() {
		HashMap<Integer, Object> bestSP = new HashMap<Integer, Object>();
		if (!straightPaths.isEmpty()) {
			bestSP = straightPaths.get(0);
//			for (int i = 0; i < straightPaths.size(); i++) 
//				if (straightPaths.get(i).size() < bestSP.size()) 
//					bestSP = straightPaths.get(i);
		}
		return bestSP;
	}
		
	public Vector2 [] getVectors() {
		return this.vectors;
	}
	
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
	
	public void setStraightPathVectors(int key, int value) {
		this.straightPathVectors.put(key, value);
	}
	
	public Map<Integer, Object> getStraightPathVectors() {
		return this.straightPathVectors;
	}
	
	@SuppressWarnings("unchecked")
	public void setStraightPaths(HashMap<Integer, Object> straightPath) {
		this.straightPaths.add((HashMap<Integer, Object>) straightPath);
	}
	
	public ArrayList<HashMap<Integer, Object>> getStraightPaths() {
		return this.straightPaths;
	}
	
	public void clearStraigthPaths() {
		this.straightPathVectors.clear();
		this.straightPathVectors.clear();
		
	}
}
