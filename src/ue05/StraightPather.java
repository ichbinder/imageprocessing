package ue05;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import ue05.Contoure;

public class StraightPather {
	
	private Vector2 constraint0;
	private Vector2 constraint1;
	private Contoure[] contoures;
	
	/**Konstruktor für StraightPather.
	 * @param contoures Ein Array aus gefunden Konturen.*/
	public StraightPather(Contoure[] contoures) {
		this.contoures = contoures;
		this.constraint0 = new Vector2(0, 0);
		this.constraint1 = new Vector2(0, 0);
		createStraighttPaths();
		System.out.println("StraightPaths gefunden");
		
		possibleSegmentsFromJosh();
				
		System.out.println("Polygone gefunden");		
	}
	
	
	/**Erzeugt Straight Paths für alle Konturen.
	 * Jede Punkt wird durchgegangen und geprüft wie weit er gehen kann. 
	 * Dies wird über Contraints und Richtungswechsel geprüft. 
	 * Sollten genau 3 Richtungswechsel auftreten wird der vorherige Punkt genommen.
	 * Genauso verhält es sich wenn der Contraint überschritten wird.<p>
	 * Sollte am Ende kein Contraint gebrochen worden sein und keine 3 Richtungswechsel stattgefunden haben, wird der Startpunkt als weitester Punkt eingetragen.
	 * */
	private void createStraighttPaths() {
		Vector2 vik = new Vector2();
		for (int c = 0; c < contoures.length; c++) {
			Contoure contoure = contoures[c];
			
			for(int i = 0; i < contoure.getVectors().length; i++) {
				this.constraint0.set(0, 0);
				this.constraint1.set(0, 0);
				short countChangedDirections = 0;
				
				Vector2 oldK = contoure.getVector(i);
				boolean oldEqualX = false, oldEqualY = false;
				
				for(int k = i +1; k < contoure.getVectors().length; k++) {
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
						contoure.addStraightPathVectors(i, k - 1);
						break;
					}
					//-------------------- Ende Prüfung Richtungswechsel
					
					if (constraint0.cross(vik) < 0 || constraint1.cross(vik) > 0) {
						contoure.addStraightPathVectors(i, k - 1);
						break;
					}
					
					constaintUpdate(vik);	
					
					//Falls Constraint noch nicht gefunden wurde lasse ihn über den Anfang laufen
					if(k + 1 >= contoure.getVectors().length){
						k = -1;											
					}					
				}
//				if (!contoure.getStraightPathVectors().containsKey(i)) contoure.addStraightPathVectors(i, 0);
			}			
		}
	}
	
	/**Prüft ob ein Richtungswechsel stattgefunden hat. Sollte einer stattgefunden haben wird eine 1 ausgegeben, ansonsten 0.<p> 
	 * Beispiel: P1(0,1) & P2(0,2) = X-Koordinante bleibt konstant, während die Y-Koordinate wandert. 
	 * Ist im 2. Durchlauf die X-Koordinate am wandern und Y-bleibt konstant kam ein Richtungswechsel zustande.*/
	private short changedDirection(boolean oldX, boolean oldY, boolean equalX, boolean equalY){
		
		boolean changed = true;
 		if( (oldX == equalX) || (oldY == equalY)) changed = false;
		if(changed) return 1;
		else return 0;
	}
	
	/**Prüft die Contraints und falls nötig werden diese neu berechnet.
	 * @param a Der gegegebene 2-dimensionale Vektor*/
	private void constaintUpdate(Vector2 a) {
		if (Math.sqrt(a.x * a.x) <= 1 && Math.sqrt(a.y * a.y) <= 1){
//			return false;			
			return;
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
//			return true;
		}
	}
	
	
	/**Jede Kontur wird auf seine einzelnen Straightpaths untersucht. 
	 * Es werden Verbindungen gezogen mit den jeweils berechneten weitesten Pfaden von einem Punkt zum Nächsten und in einem Dictionary abgelegt.*/
	private void possibleSegmentsOld() {
		for (int c = 0; c < contoures.length; c++) {
			Contoure contoure = contoures[c];
			for (int i = 0; i < contoure.getStraightPathVectors().size(); i++) {
				LinkedHashMap<Integer, Integer> tempStraingthPath = new LinkedHashMap<>();
				
				//Gehe alle Einträge der unsortierten Hashmap durch
				Set<Integer> key = contoure.getStraightPathVectors().keySet();
	            Iterator<Integer> it = key.iterator();            
	            int lastData = (int) it.next() + i;
	            int startPoint = lastData;
	            int startData = contoure.getStraightPathVectors().get(startPoint);
	            
	            //Solange Daten im unsortiertem enthalten sind
	            while (it.hasNext()) {
	            	         	
	                int hmKey = lastData;
	                int hmData = (int) contoure.getStraightPathVectors().get(hmKey);
	                
	                if (!tempStraingthPath.isEmpty()) {

	                	
			            if (startPoint > hmKey) {
		                	if (hmData >= startPoint)  {
		                		tempStraingthPath.put(hmKey, startPoint);
		                		break;	
			                } 
		                	//START: 81  -> Data: 1
		                	//Key: 80 -> Data: 1		                	
/*		                	else if (startData >= hmData){
								//key = new value = startPoint
//		                		hmData = startPoint;
		                		tempStraingthPath.put(hmKey, startPoint);
		                		break;
							}
							*/
		                }
	                }
	                
	               	tempStraingthPath.put(hmKey, hmData);

	                lastData = hmData;   
	                it.next();
	            }
	            contoure.addStraightPaths(tempStraingthPath);
			}
			contoure.calcBestStraigthPath();
		}
	}
	
	/**Jede Kontur wird auf seine einzelnen Straightpaths untersucht. 
	 * Es werden Verbindungen gezogen mit den jeweils berechneten weitesten Pfaden von einem Punkt zum Nächsten und in einem Dictionary abgelegt.*/
	private void possibleSegments() {
		for (int c = 0; c < contoures.length; c++) {
			Contoure contoure = contoures[c];
			for (int v = 0; v < contoure.getStraightPathVectors().size(); v++) {
				LinkedHashMap<Integer, Integer> contoureStraingthPath = new LinkedHashMap<>();
				
				//Gehe alle Einträge der unsortierten Hashmap durch
				Set<Integer> key = contoure.getStraightPathVectors().keySet();
	            Iterator<Integer> it = key.iterator();            
	            
            	int i = v, j = 0; 
            	
            		            
	            while (it.hasNext()) {
	            	
	                j = (int) contoure.getStraightPathVectors().get(i);	                
	                if(i <= j) 
	                	contoureStraingthPath.put(i, j);

	                if(j < i){
	                	
	                	int vn = (int) contoure.getStraightPathVectors().size()-1;
	                	int v0 = 0; 
	                	
	                	contoureStraingthPath.put(i, vn);
	                	if(j >= v) j = v;	                	
	                	contoureStraingthPath.put(vn, j);
	                	if(j >= v) break;	                	
	                }
		            i = j;
	                it.next();
	            }
				if (!contoure.getStraightPathVectors().containsKey(i)) contoure.addStraightPathVectors(i, 0);

	            contoure.addStraightPaths(contoureStraingthPath);
			}//v++
			contoure.calcBestStraigthPath();
		}
	}
	
	
	/**Jede Kontur wird auf seine einzelnen Straightpaths untersucht. 
	 * Es werden Verbindungen gezogen mit den jeweils berechneten weitesten Pfaden von einem Punkt zum Nächsten und in einem Dictionary abgelegt.*/
	private void possibleSegmentsFromOldClass() {
		for (int c = 0; c < contoures.length; c++) {
			Contoure contoure = contoures[c];
			for (int i = 0; i < contoure.getStraightPathVectors().size(); i++) {
				LinkedHashMap<Integer, Integer> tempStraingthPath = new LinkedHashMap<>();
				
				//Gehe alle Einträge der unsortierten Hashmap durch
				Set<Integer> key = contoure.getStraightPathVectors().keySet();
	            Iterator<Integer> it = key.iterator();            
	            int lastData = (int) it.next() + i;
	            int startPoint = lastData;
	            
	            //Solange Daten im unsortiertem enthalten sind
	            while (it.hasNext()) {
	            	         	
	                int hmKey = lastData;
	                int hmData = (int) contoure.getStraightPathVectors().get(hmKey);
	                
	                if (!tempStraingthPath.isEmpty()) {
		                if (startPoint > hmKey && 
		                	(int) contoure.getStraightPathVectors().get(startPoint) <= hmData)  {
		                	tempStraingthPath.put(hmKey, startPoint);
		                	break;
		                }
	                }
	                
	               	tempStraingthPath.put(hmKey, hmData);

	                lastData = hmData;   
	                it.next();
	            }
	            contoure.addStraightPaths(tempStraingthPath);
				System.out.println("test");

				contoure.calcBestStraigthPath();

			}
		}
	}
	private void possibleSegmentsFromJosh() {
		for (int c = 0; c < contoures.length; c++) {
			Contoure contoure = contoures[c];			
			for (int startPos = 0; startPos < contoure.getStraightPathVectors().size(); startPos++) {
				
				LinkedHashMap<Integer, Integer> tempStraingthPath = new LinkedHashMap<>();							
				int currPos = startPos;
			
				while(true) {
					// find target position		
					int hmKey = currPos;
	                int maxPath = (int) contoure.getStraightPathVectors().get(hmKey);
	                
					int targetPos = maxPath -1 < 0 ? contoure.getVectors().length-1 : maxPath-1;
				
					// check if we pass the starting position (is this check correct? O_o)
					if((startPos > currPos && startPos < targetPos)
						|| (currPos > targetPos && (startPos < targetPos || startPos > currPos))
						|| targetPos == startPos) {
						
						// add last (or first) position and break out of the loop
						tempStraingthPath.put(currPos, startPos);
						break;
					}
					// add target position to the polygon
//					currPos = targetPos;
					tempStraingthPath.put(currPos, targetPos);
					currPos = targetPos;
				}//while
	            contoure.addStraightPaths(tempStraingthPath);
			}
			contoure.calcBestStraigthPath();
		}
	}
	
	
	
	private void sortStraightPath(SortedMap<Integer, Integer> tempStraingthPath2) {
		SortedMap<Integer, Integer> tempStraingthPath = new TreeMap<>();
		
		//Fülle eine neu sortierte HashMap -> Richtige Reichenfolge
			
			//Gehe alle Einträge der unsortierten Hashmap durch
			Set<Integer> key = tempStraingthPath2.keySet();
            Iterator<Integer> it = key.iterator();            
            
            int lastData = (int) it.next();
            
            //Solange Daten im unsortiertem enthalten sind
            while (it.hasNext()) {
            	            	
                int hmKey = lastData;
                int hmData = (int) tempStraingthPath2.get(hmKey);	
                
               	tempStraingthPath.put(hmKey, hmData);

                lastData = hmData;   
                it.next();
                                              

		}
        System.out.println(tempStraingthPath);
                
        
            
	}
}
