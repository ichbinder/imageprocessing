package ue04;

import java.util.ArrayList;

public class StraightPather {

//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//
//	}
	
	private Vector2 constraint0;
	private Vector2 constraint1;
	private Contoure[] contoures;
	private ArrayList<Integer> straightPath;
	private int[] bestStraightPath;
	
	public StraightPather(Contoure[] contoures) {
		this.contoures = contoures;
		this.constraint0 = new Vector2(0, 0);
		this.constraint1 = new Vector2(0, 0);
		this.straightPath = new ArrayList<Integer>();
		this.bestStraightPath = createStraighttPath();
	}
	
	private int[] createStraighttPath() {
		Vector2 vik = new Vector2();
		for (int c = 0; c < contoures.length; c++) {
			Contoure contoure = contoures[c];
			for(int i = 0; i < contoure.getVectors().length; i++) {
				this.constraint0.set(0, 0);
				this.constraint1.set(0, 0);
				for(int k = i + 1; k < contoure.getVectors().length; k++) {
					vik.set(contoure.getVector(k).x, contoure.getVector(k).y);
					vik.subtractVector(contoure.getVector(i));
					if (constraint0.cross(vik) < 0 || constraint1.cross(vik) > 0) {
						contoure.setStraightPath(i, k - 1);
						this.straightPath.add(k - 1);
						break;
					}
					constaintUpdate(vik);
				}				
			}
		}
		int[][] paths = possibleSegments(getStraightPaths());
		//Berechnung des besten Polygons 
		int lengthOfBestPath = paths.length;
		int indexOfBestPath = 0;
		for (int i = 0; i < paths.length; i++) {
			if (lengthOfBestPath > paths[i].length) {
				System.out.println(paths[i].length);
				lengthOfBestPath = paths[i].length;
				indexOfBestPath = i;
			}
		}
		return paths[indexOfBestPath];
	}
	
	private boolean constaintUpdate(Vector2 a) {
		if (Math.sqrt(a.x * a.x) <= 1 && Math.sqrt(a.y * a.y) <= 1){
//			(Math.abs(a.x) <= 1 && Math.abs(a.y) <= 1){
			return false;			
		}
		else {
			// Berechne constraint0 neu oder nicht
			Vector2 d0 = new Vector2(); 
			if (a.y >= 0 && (a.y > 0 || a.x < 0))
				d0.x = a.x + 1;
			else
				d0.x = a.x - 1;
			
			if (a.x <= 0 && (a.x < 0 || a.y < 0))
				d0.y = a.y + 1;
			else
				d0.y = a.y - 1;
			
			if (this.constraint0.cross(d0) >= 0)
				this.constraint0 = d0.clone();
			
			// Berechne constraint1 neu oder nicht
			Vector2 d1 = new Vector2();
			if (a.y <= 0 && (a.y < 0 || a.x < 0))
				d1.x = a.x + 1;
			else
				d1.x = a.x - 1;
			
			if (a.x >= 0 && (a.x > 0 || a.y < 0))
				d1.y = a.y + 1;
			else
				d1.y = a.y - 1;
			
			if (this.constraint1.cross(d1) <= 0)
				this.constraint1 = d1.clone();
				
			return true;
		}
	}
	
	private int[][] possibleSegments(int [] straightPaths) {
		
		int [][] possibleSegments = new int [straightPaths.length][];
		
		// ist immer der neue Startpunkt
		for(int i = 0; i < possibleSegments.length; i++) {
			
			int maxIndex = i;
			int maxValue = 0;
			
			ArrayList<Integer> arrList = new ArrayList<Integer>();
			
			//Ablaufen der nächsten Punkte
			for(int j = 0; j < possibleSegments.length; j++) {

				maxValue = straightPaths[maxIndex];	
				arrList.add(maxValue);
				maxIndex = maxValue;				
			}
			//ArrayList to Integer->Array
			possibleSegments[i] =  new int [arrList.size()];
			
			for(int j = 0; j < arrList.size(); j++){
				possibleSegments[i][j] = (int) arrList.get(j);
			}		
		}		
		return possibleSegments;
	}

	public int[] getStraightPaths() {
		int[] tmp = new int[straightPath.size()];
		for (int i = 0; i < straightPath.size(); i++) {
			tmp[i] = straightPath.get(i);
		}
		return tmp;
	}
	
	public int[] getStraightPath() {
		return this.bestStraightPath;
	}
}
