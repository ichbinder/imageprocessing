package ue04;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class Contoure {

	final private boolean isOutline;
	final private Vector2 [] vectors;
	private Map<Integer, Integer> straightPathVectors;// speichert alle möglichen StraigthPath Vektoren 
	private ArrayList<TreeMap<Integer, Integer>> straightPaths; // speichert alle möglichen StraigthPathsesees
	
	public Contoure(boolean isOut, Vector2 [] ps){
		
		this.isOutline = isOut;
		this.vectors = ps;
		this.straightPathVectors = new TreeMap<Integer, Integer>(); 
		this.straightPaths = new ArrayList<TreeMap<Integer, Integer>>(); 
	}
	
//	Liefert den besten StraigthPath zurück 
	public Map<Integer, Integer> getBestStraigthPath() {
		TreeMap<Integer, Integer> bestSP = new TreeMap<Integer, Integer>();
		if (!straightPaths.isEmpty()) {
			for (TreeMap<Integer, Integer> path : straightPaths) {
				if (path.size() < bestSP.size())
					bestSP = path;
			}
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
	
	public Map<Integer, Integer> getStraightPathVectors() {
		return this.straightPathVectors;
	}
	
	@SuppressWarnings("unchecked")
	public void setStraightPaths(SortedMap<Integer, Integer> tempStraingthPath) {
		this.straightPaths.add((TreeMap<Integer, Integer>) tempStraingthPath);
	}
	
	public ArrayList<TreeMap<Integer, Integer>> getStraightPaths() {
		return this.straightPaths;
	}
	
	public void clearStraigthPaths() {
		this.straightPathVectors.clear();
		this.straightPathVectors.clear();
		
	}
}
