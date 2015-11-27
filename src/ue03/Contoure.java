package ue03;

import java.awt.Point;

public class Contoure {

	final private boolean isOutline;
	final private Point [] points;
	
	public Contoure(boolean isOut, Point [] ps){
		
		this.isOutline = isOut;
		this.points = ps;
	}
		
	public Point [] getPoints(){
		
		return this.points;
	}
	
	public boolean isOutline(){
		
		return this.isOutline;
	}
	
	public Point getPoint(int index){
		return this.points[index];
	}
}
