package ue06;

import ue06.Potrace.PathDirection;

public class Vector2{

	public double x,y;
	public PathDirection lookingDirection;
	
	public Vector2(double x, double y){
		this.x = x;
		this.y = y;
	}

	public Vector2() {
		x=y=0;
	}

	public void set(double x, double y){
		this.x = x;
		this.y = y;
	}
	
	public void setX(double x){
		this.x = x;
	}
	
	public void setY(double y){
		this.y = y;
	}
	
	public double getX(){
		return this.x;
	}
	
	public double getY(){
		return this.y;
	}
	
	public void get (double[] dest){
		dest[0] = x;
		dest[1] = y;
	}

	public void addVector(Vector2 b) {
		this.x += b.x; this.y += b.y;
	}
	public void subtractVector(Vector2 b) {
		this.x -= b.x; this.y -= b.y;
	}

	public double length() {
		return (double)Math.sqrt(this.x*this.x +this.y*this.y);
	}

	public void mult(double s){
		this.x = this.x *s; this.y = this.y * s;
	}
	
	public void divide(double s){
		
		this.x = this.x / s; this.y = this.y / s;
	}
	
	public double cross(Vector2 b){
		return this.x * b.y - this.y * b.x;
	}

	public double scalarProduct(Vector2 other){
		
		return this.x * other.x + this.y * other.y;
	}

	static public Vector2 normal(Vector2 a, Vector2 b){
		
		Vector2 normal = null;
		Vector2 sh = new Vector2(b.y - a.y, -(b.x - a.x)); // s^		
		normal = sh.clone();
		normal.mult(1/sh.length());
		
		return normal;
	}

	
	public void setLength(double f) {
		double factor = f / this.length();
		x *= factor;
		y *= factor;
	}

	public Vector2 clone() {
		return new Vector2(x,y);
	}
		
	public String toString() {
		return "( " + x + ", " + y + ")";
	}	
}
