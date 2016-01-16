package ue05;

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
	private ArrayList<Vector2> middlePath; //Speichert alle Mittelpunkte von den gefundenen StraightPaths
	
	
	/**Erzeugt eine Kontur . 
	 * @param isOut Ist es eine Außenkontur?
	 * @param ps Enthält ein Array aus Path-Points.*/
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
	
	/**Fügt einen StraightPath zum Straight-Path Dictionary hinzu.
	 * @param key Der aktuelle betrachtete Punkt (i).
	 * @param value Der entfernteste erreichbare Punkt.*/
	public void addStraightPathVectors(int key, int value) {
		this.straightPathVectors.put(key, value);
	}
	

	/**Gibt das Dictionary zurück indem die weiteste Entfernung von einem Punkt (i) zum Nächsten (k) steht.
	 * @return Dictionary -> Key: i, Value: k*/
	public Map<Integer, Integer> getStraightPathVectors() {
		return this.straightPathVectors;
	}
	
	/**Fügt ein ganzes Straight-Path-Dictionary zu einer Straight-Path - Sammlung hinzu.
	 * @param straightPath ein ganzes StraightPath-Dictionary*/
	@SuppressWarnings("unchecked")
	public void setStraightPaths(SortedMap<Integer, Integer> tempStraingthPath) {
		this.straightPaths.add((TreeMap<Integer, Integer>) tempStraingthPath);
	}
	
	/**Gibt die Sammlung an StraightPath-Dictionaries zurück.
	 * @return Die Straight-Path-Sammlung*/
	public ArrayList<TreeMap<Integer, Integer>> getStraightPaths() {
		return this.straightPaths;
	}
	
	/**Löscht die gefundenen StraightPathVektoren und gefundenen Straight-Path Verbindungen.*/
	public void clearStraigthPaths() {
		this.straightPathVectors.clear();
		this.straightPaths.clear();
	}

	/**Setzt die gefundenenen Mittelpunkte zwischen den gefundenen Straight-Path Verbindungen.*/
	public void setMiddlePath(ArrayList<Vector2> middlePath){
		
		this.middlePath = middlePath;
	}
	
	public void addMiddlePoint(Vector2 vec){
		
		this.middlePath.add(vec);
	}
	
	/**Gibt die Sammlung von gefundenen Mittelpunkten zwischen den Straightpaths zurück.*/
	public ArrayList<Vector2> getMiddlePaths(){
		
		return this.middlePath;
	}
}
