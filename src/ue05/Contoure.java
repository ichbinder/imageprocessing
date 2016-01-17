package ue05;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class Contoure {

	final private boolean isOutline;
	final private Vector2 [] vectors;
	private Map<Integer, Integer> straightPathVectors;// speichert alle möglichen StraigthPath Vektoren 
	private ArrayList<LinkedHashMap<Integer, Integer>> straightPaths; // speichert alle möglichen StraigthPathsesees
	private ArrayList<Vector2> middlePath; //Speichert alle Mittelpunkte von den gefundenen StraightPaths
	private Map<Integer, Integer> bestStraightPath;
	
	
	/**Erzeugt eine Kontur . 
	 * @param isOut Ist es eine Außenkontur?
	 * @param ps Enthält ein Array aus Path-Points.*/
	public Contoure(boolean isOut, Vector2 [] ps){
		
		this.isOutline = isOut;
		this.vectors = ps;
		this.straightPathVectors = new LinkedHashMap<Integer, Integer>(); 
		this.straightPaths = new ArrayList<LinkedHashMap<Integer, Integer>>(); 
		this.bestStraightPath = new LinkedHashMap<Integer, Integer>();
		this.middlePath = new ArrayList<Vector2>();
	}
	
//	Liefert den besten StraigthPath zurück 
	public void calcBestStraigthPath() {
		LinkedHashMap<Integer, Integer> bestSP = new LinkedHashMap<Integer, Integer>();
		if (!straightPaths.isEmpty()) {
			bestSP = straightPaths.get(0);
			for (LinkedHashMap<Integer, Integer> path : straightPaths) {
				if (path.size() < bestSP.size()) {
					bestSP = path;
					System.out.println("BSP:" + bestSP);
				}
				
			}
		}
		this.bestStraightPath = bestSP;
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
	public void addStraightPaths(LinkedHashMap<Integer, Integer> tempStraingthPath) {
		this.straightPaths.add((LinkedHashMap<Integer, Integer>) tempStraingthPath);
	}
	
	/**Gibt die Sammlung an StraightPath-Dictionaries zurück.
	 * @return Die Straight-Path-Sammlung*/
	public ArrayList<LinkedHashMap<Integer, Integer>> getStraightPaths() {
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
	
	public Map<Integer, Integer> getBestStraigthPath() {
		
//		return this.straightPaths.get(0);
		return this.bestStraightPath;
	}
}
