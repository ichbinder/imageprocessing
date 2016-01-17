package ue05;

import java.awt.Color;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Line2D;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import ue05.Vector2;


public class BezierCalculation {
		
	static public void calculateBezierPoints(Contoure contoures[], float alpha, float minimum, float maximum){
		
		for(int c = 0; c < contoures.length; c++){
			
			Contoure contoure = contoures[c];
			contoure.clearBezierPoints();
			for(int j = 0; j < contoure.getMiddlePaths().size(); j+=2){
				
	    		int prev = j -1; //  erste Mittelpunkteintrag		            		
	    		int next = j +1; // nächste Mittelpunkt		            		
	    		
	    		if(prev < 0)  prev = contoure.getMiddlePaths().size()-1;		            		
	    		if(next > contoure.getMiddlePaths().size() -1) next = 0;
	    		
	    		Vector2 b0 = contoure.getMiddlePaths().get(prev);							
	    		Vector2 a  = contoure.getMiddlePaths().get(j);
	    		Vector2 b1 = contoure.getMiddlePaths().get(next);
	    		
	    		Vector2 [] oriPoints = {b0, b1, a};//ABC		            				            		
	    		Vector2 [] bezierPoints = BezierCalculation.calcBezierCurve(oriPoints, alpha, minimum, maximum, 0.5f);
	    		contoure.addBezierPoints(bezierPoints);
	    	}
		}
	}
	
	
	/**Gibt Punkte (Vector2) zum Zeichnen einer Bezierkurve zurück.
	 * @param points Vector2-Array. 1. erster Mittelpunkt, 2. zweiter Mittelpunkt  3.Originalpunkt  (A-B-C)
	 * @param alphaFactor Rundungsfaktor
	 * @param minimum Minimum um Alpha einzugrenzen
	 * @param maximum Maximum um Alpha einzugrenzen
	 * @param radius Radius die im Durschnitt 0.5 beträgt
	 * @return Vector2-Array. Indicies: Anfangspunkt, 2. erster Kontrollpunkt, 3. zweiter Kontrollpunkt, 4. Endpunkt.*/
	static public Vector2[] calcBezierCurve(Vector2[] points, float alphaFactor,float minimum, float maximum, float radius){
	/*
		float dOld = calcDistance(points);
		
		Vector2 b = points[0];
		Vector2 a = points[1];
		Vector2 v = points[2];
		
		Vector2 normal = Vector2.normal(a, b);
		Vector2 va = a.clone();
		va.subtractVector(v);
			
		float d = Math.abs( normal.scalarProduct(va));
		*/
		float d = calcDistance(points);
		float alpha = calcAlphaAngle(alphaFactor, d, radius);
		
		if(alpha < minimum) alpha = minimum;
		if(alpha > maximum) alpha = maximum;
		
		Vector2 [] bezierPoints = new Vector2[4];				
		bezierPoints[0] = points[0].clone(); //z0
						
		Vector2 z1 = points[2].clone(); //b0 - a = ab0
		z1.subtractVector(points[0].clone());			
		z1.mult(alpha);
		z1.addVector(points[0].clone()); // Wieder zurück zum Zeichnen
		bezierPoints[1] = z1; //z1
				
		
		Vector2 z2 = points[2].clone(); //a - b1 = ab0
		z2.subtractVector(points[1].clone());			
		z2.mult(alpha);
		z2.addVector(points[1].clone()); // Wieder zurück zum Zeichnen
		bezierPoints[2] = z2; //z1
		
		
		bezierPoints[3] = points[1].clone(); //z3		
		return bezierPoints;			
	}
	
//	h_c =  \sqrt{2(a^2 b^2 + b^2 c^2 + c^2 a^2) - (a^4 + b^4 + c^4)} / (2 c) 
	
	
	/**1. Punkt ist Vorgänger (bi-1) A
	 * 2. Punkt ist der betrachtete (ai) C
	 * 3. Punkt ist Nachfolger (bi+1) B */
	static float calcDistanceTriangle(Vector2[] points){
		
		//A = bi-1
		//B = bi+1
		//C = ai
		
		//AB = c
		//BC = a
		//AC = b
				
		//AB = c
		Vector2 sideC = points[1].clone();  //Klone B
		sideC.subtractVector(points[0].clone()); // B - A = c

		//AC = b
		Vector2 sideB = points[2].clone();  //Klone C
		sideB.subtractVector(points[0].clone()); // C - A = b

		//BC = a
		Vector2 sideA = points[2].clone();  //Klone C 
		sideA.subtractVector(points[1].clone()); // C - B = a
		
		float c = sideC.length();		
		float b = sideB.length();
		float a = sideA.length();
		
		float beta = (float) Math.acos((b * b - c * c - a * a) / (-2 * c * a));
		float hc = (float) (a * Math.sin(beta));
				
		return hc;
	}

	static float calcDistance(Vector2 points[]){
				
		Vector2 b = points[0];
		Vector2 a = points[1];
		Vector2 v = points[2];
		
		Vector2 normal = Vector2.normal(a, b);
		Vector2 va = a.clone();
		va.subtractVector(v);
			
		float distance = Math.abs( normal.scalarProduct(va));
		return distance;
	}
	
	static private float calcAlphaAngle(float alphaFactor, float distance, float radius){
		
		float alpha = alphaFactor *( (distance - radius) / distance);

		return alpha;
	}

	/**Gibt alle Straightpath Punkte sowie die gefundenen Zwischenpunkte (Mittelpunkte) aus.
	 * @param contoure Die betroffene Kontur*/
	static public void getMiddlePointsOnStraightPaths(Contoure [] contoures){
		
		for(int c = 0; c < contoures.length; c++){
			
			Contoure contoure = contoures[c];
			System.out.println("-- Alle StraightPathpunkte --");
								
			LinkedHashMap<Integer, Integer> tmpData = (LinkedHashMap<Integer, Integer>) contoure.getBestStraigthPath();
        	Set<Integer> key = tmpData.keySet();
        	Iterator it = key.iterator();
        	while (it.hasNext()) {
            	int hmKey = (int)it.next();
            	int hmData = (int) tmpData.get(hmKey);
				
            	Vector2 pointA = contoure.getVector(hmKey);
            	Vector2 pointB = contoure.getVector(hmData);
            	
            	Vector2 middle = pointA.clone();
            	middle.addVector(pointB);
            	middle.setLength(middle.length() * 0.5f);
            	
            	contoure.addMiddlePoint(pointA);
				contoure.addMiddlePoint(middle); //Füge gefundenen Mittelpunkt hinzu
//				contoure.addMiddlePoint(pointB);
        	}	
	    	
			System.out.println("-- Ende StraightPathpunkte --");
		}
	}
}
