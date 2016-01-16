package ue05;

import java.util.HashMap;

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
		possibleSegments();
		System.out.println("alles gut!");		
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
//				contoure.addStraighPathVector(i, contoure.getVector(i));				
				
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
//						contoure.addStraighPathVector(i, contoure.getVector(k-1));						
						break;
					}
					//-------------------- Ende Prüfung Richtungswechsel
					
					if (constraint0.cross(vik) < 0 || constraint1.cross(vik) > 0) {
						contoure.addStraightPathVectors(i, k - 1);												
//						contoure.addStraighPathVector(i, contoure.getVector(k-1));						
						break;
					}
					constaintUpdate(vik);	
					
					//Falls Constraint noch nicht gefunden wurde lasse ihn über den Anfang laufen
					/*
					if(k >= contoure.getVectors().length-1){
						k = -1;											
					}*/
				}
				if (!contoure.getStraightPathVectors().containsKey(i)) contoure.addStraightPathVectors(i, 0);				
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
				contoure.addStraightPaths(tempStraingthPath);
			}
		}
	}
}
