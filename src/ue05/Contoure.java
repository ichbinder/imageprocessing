package ue05;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import ue05.Vector2;

public class Contoure {

	final private boolean isOutline;
	final private Vector2 [] vectors;
	private Map<Integer, Object> straightPathVectors;// speichert alle möglichen StraigthPath Vektoren 
	private ArrayList<HashMap<Integer, Object>> straightPaths; // speichert alle möglichen StraigthPathsesees	
	private ArrayList<Vector2> middlePath;	
	private ArrayList<Vector2> [] straightPathVectorList; // Möglichkeiten von StraightsPaths als Vector2-ArrayList
	
	
	
	/**Erzeugt eine Kontur . 
	 * @param isOut Ist es eine Außenkontur?
	 * @param ps Enthält ein Array aus Path-Points.*/
	public Contoure(boolean isOut, Vector2 [] ps){
		
		this.isOutline = isOut;
		this.vectors = ps;
		this.straightPathVectors = new HashMap<Integer, Object>(); 
		this.straightPaths = new ArrayList<HashMap<Integer, Object>>(); 
		this.middlePath = new ArrayList<Vector2>();
		
	}
	
	/**Gibt den besten (im aktuellen Fall kürzesten) Pfad zurück.
	 * @return Ein Dictionary worin der kürzeste Pfad enthalten ist.*/
	public Map<Integer, Object> getBestStraigthPath() {
		HashMap<Integer, Object> bestSP = new HashMap<Integer, Object>();
		if (!straightPaths.isEmpty()) {
			bestSP = straightPaths.get(0);
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
	
	
	public void initStraighPathList(int size){
		
		straightPathVectorList = new ArrayList[size]; // Index 0 = Arraylist welche mehere StraightPaths enthält		
	}
	
	public void addStraighPathVector(int startIndex, Vector2 vec){
		
		straightPathVectorList[startIndex].add(vec);
		
	}
	
	/**Gibt das Dictionary zurück indem die weiteste Entfernung von einem Punkt (i) zum Nächsten (k) steht.
	 * @return Dictionary -> Key: i, Value: k*/
	public Map<Integer, Object> getStraightPathVectors() {
		return this.straightPathVectors;
	}
	
	/**Fügt ein ganzes Straight-Path-Dictionary zu einer Straight-Path - Sammlung hinzu.
	 * @param straightPath ein ganzes StraightPath-Dictionary*/
	public void addStraightPaths(HashMap<Integer, Object> straightPath) {
		this.straightPaths.add((HashMap<Integer, Object>) straightPath);
	}
	
	/**Gibt die Sammlung an StraightPath-Dictionaries zurück.
	 * @return Die Straight-Path-Sammlung*/
	public ArrayList<HashMap<Integer, Object>> getStraightPaths() {
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
	
		
	public ArrayList<Vector2> getBestStraighPathAsPoints(){
		
		ArrayList<Vector2> listOfPoints = new ArrayList<Vector2>();
		
		HashMap<Integer, Object> tmpData = (HashMap<Integer, Object>) this.getBestStraigthPath();
    	Set<Integer> key = tmpData.keySet();
    	Iterator it = key.iterator();
    	
    	int counter = 0;
    	while (it.hasNext()) {
        	int hmKey = (int)it.next();
        	int hmData = (int) tmpData.get(hmKey);  
        	
        	Vector2 a = this.getVector(hmKey);
        	Vector2 b = this.getVector(hmData);
        	        	
        	listOfPoints.add(this.getVector(hmKey));
        	listOfPoints.add(this.getVector(hmData));      	
        	
        	System.out.println("k: "+a);
        	System.out.println("d: "+b);        	
        	
//        	counter+=2;
    	}
  //  	System.out.println("Counted: " + counter);
		
		return listOfPoints;
	}  
}



