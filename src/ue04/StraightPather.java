package ue04;

import java.util.HashMap;

public class StraightPather {

//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//
//	}
	
	private Vector2 constraint0;
	private Vector2 constraint1;
	private Contoure[] contoures;
	
	public StraightPather(Contoure[] contoures) {
		this.contoures = contoures;
		this.constraint0 = new Vector2(0, 0);
		this.constraint1 = new Vector2(0, 0);
		createStraighttPath();
		possibleSegments();
		System.out.println("alles gut!");		
	}
	
//	finde alle Vektoren die später einen oder mehrer StraighttPath bilden können
	private void createStraighttPath() {
		Vector2 vik = new Vector2();
		for (int c = 0; c < contoures.length; c++) {
			Contoure contoure = contoures[c];
			
			for(int i = 0; i < contoure.getVectors().length; i++) {
				this.constraint0.set(0, 0);
				this.constraint1.set(0, 0);
				short countChangedDirections = 0;
				
				Vector2 oldK = contoure.getVector(i);
				boolean oldEqualX = false, oldEqualY = false;
				
				for(int k = i + 1; k < contoure.getVectors().length; k++) {
					vik.set(contoure.getVector(k).x, contoure.getVector(k).y);
					vik.subtractVector(contoure.getVector(i));
					
					//-------------------- Prüfe Richtungswechsel
					Vector2 newK = contoure.getVector(k);										
					boolean equalX = false, equalY = false;
					
					if(oldK.x == newK.x) equalX = true;
					if(oldK.y == newK.y) equalY = true;					
					countChangedDirections += changedDirection(oldEqualX, oldEqualY, equalX, equalY);
					
					oldK = newK;
					oldEqualX = equalX;
					oldEqualY = equalY;
					
					if(countChangedDirections == 3){
						contoure.setStraightPathVectors(i, k - 1);
						break;
					}
					//-------------------- Ende Prüfung Richtungswechsel
					
					if (constraint0.cross(vik) < 0 || constraint1.cross(vik) > 0) {
						contoure.setStraightPathVectors(i, k - 1);
						break;
					}
					constaintUpdate(vik);					
				}
				if (!contoure.getStraightPathVectors().containsKey(i))
					contoure.setStraightPathVectors(i, 0);
					//contoure.setStraightPathVectors(i, contoure.getVectors().length - 1);
			}
		}
	}
	
	private short changedDirection(boolean oldX, boolean oldY, boolean equalX, boolean equalY){
		
		boolean changed = true;

//		if((oldX != equalX  && oldY == equalY) || (oldY != equalY && oldX == equalX)) changed = false;
//		if((oldX != equalX && oldY == equalY) || (oldY != equalY && oldX == equalX)) changed = false;

 		if( (oldX == equalX) || (oldY == equalY)) changed = false;
//		if((oldX != equalX && oldY == equalY) || (oldY != equalY && oldX == equalX)) changed = false;
		if(changed) return 1;
		else return 0;
	}
	
//	Berechenen den neuen Constaint0 und Constaint1
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
	
//	Berechene aus den gefinden StraighttPath Vektoren alle möglichen StraighttPathsesess
	private void possibleSegments() {
		for (int c = 0; c < contoures.length; c++) {
			Contoure contoure = contoures[c];
			for (int i = 0; i < contoure.getStraightPathVectors().size(); i++) {
				int maxIndex = i;
				int maxValue = 0;
				int start = i;
				HashMap<Integer, Object> tempStraingthPath = new HashMap<Integer, Object>();
				for (int j = 0; j < contoure.getStraightPathVectors().size(); j++) {
					if (!contoure.getStraightPathVectors().containsKey(maxIndex)) {
						tempStraingthPath.put(maxIndex, start);
						break;
					}
					maxValue = (int) contoure.getStraightPathVectors().get(maxIndex);
					tempStraingthPath.put(maxIndex, maxValue);						
					maxIndex = maxValue;
				}
				contoure.setStraightPaths(tempStraingthPath);
			}
		}
	}
	
//	private int[][] possibleSegments(int [] straightPaths) {
//		
//		int [][] possibleSegments = new int [straightPaths.length][];
//		
//		// ist immer der neue Startpunkt
//		for(int i = 0; i < possibleSegments.length; i++) {
//			
//			int maxIndex = i;
//			int maxValue = 0;
//			
//			ArrayList<Integer> arrList = new ArrayList<Integer>();
//			
//			//Ablaufen der nächsten Punkte
//			for(int j = 0; j < possibleSegments.length; j++) {
//
//				maxValue = straightPaths[maxIndex];	
//				arrList.add(maxValue);
//				maxIndex = maxValue;				
//			}
//			//ArrayList to Integer->Array
//			possibleSegments[i] =  new int [arrList.size()];
//			
//			for(int j = 0; j < arrList.size(); j++){
//				possibleSegments[i][j] = (int) arrList.get(j);
//			}		
//		}		
//		return possibleSegments;
//	}
}
