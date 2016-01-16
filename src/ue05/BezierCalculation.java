package ue05;

import java.awt.geom.Line2D;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import ue05.Vector2;


public class BezierCalculation {

	
	
	/**Gibt Punkte (Vector2) zum Zeichnen einer Bezierkurve zurück.
	 * @param points Vector2-Array. 1. Originalpunkt, 2. erster Mittelpunkt, 3. zweiter Mittelpunkt
	 * @return Vector2-Array. Indicies: Anfangspunkt, 2. erster Kontrollpunkt, 3. zweiter Kontrollpunkt, 4. Endpunkt.*/
	static public Vector2[] calcBezierCurve(Vector2[] points, float alphaFactor, float radius){
	
		float d = calcDistance(points);
		float alpha = calcAlphaAngle(alphaFactor, d, radius);

		Vector2 [] bezierPoints = new Vector2[4];
		
		bezierPoints[0] = points[1]; //z0
		
		Vector2 sideB = points[1].clone(); 
		sideB.addVector(points[0].clone()); //		
		sideB.setLength(sideB.length() * alpha);		
		bezierPoints[1] = sideB; //z1		
		
		Vector2 sideA = points[2].clone(); 
		sideA.addVector(points[0].clone()); //
		sideA.setLength(sideA.length() * alpha);
		bezierPoints[2] = sideA; //z2

		bezierPoints[3] = points[2]; //z3
		
		return bezierPoints;			
	}
	
//	h_c =  \sqrt{2(a^2 b^2 + b^2 c^2 + c^2 a^2) - (a^4 + b^4 + c^4)} / (2 c) 
	
	
	/**1. Punkt ist der betrachtete (ai) C
	 * 2. Punkt ist Vorgänger (bi-1) A
	 * 3. Punkt ist Nachfolger (bi+1) B */
	static float calcDistance(Vector2[] points){
		
		//A = bi-1
		//B = bi+1
		//C = ai
				
		Vector2 sideC = points[1].clone();  
		sideC.addVector(points[2].clone()); //

		Vector2 sideB = points[1].clone(); 
		sideB.addVector(points[0].clone()); //
		
		
		Vector2 sideA = points[2].clone(); 
		sideA.addVector(points[0].clone()); //
		
		float c = sideC.length();		
		float b = sideB.length();
		float a = sideA.length();
		float leftCalc, rightCalc;
/*
		float		c = 14.99f;
		float		b = 5;
		float		a = 10;
*/
		float beta = (float) Math.acos((b * b - c * c - a * a) / (-2 * c * a));
		float hc = (float) (a * Math.sin(beta));
				
		return hc;
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
