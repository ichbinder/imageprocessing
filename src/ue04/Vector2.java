package ue04;

import ue04.Potrace.PathDirection;

public class Vector2{

	public int x,y;
	public PathDirection lookingDirection;
	
	public Vector2(int x, int y){
		this.x = x;
		this.y = y;
	}

	public Vector2() {
		x=y=0;
	}

	public void set(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public void get (int[] dest){
		dest[0] = x;
		dest[1] = y;
	}

	public void addVector(Vector2 b) {
		this.x += b.x; this.y += b.y;
	}
	public void subtractVector(Vector2 b) {
		this.x -= b.x; this.y -= b.y;
	}

	public float length() {
		return (float)Math.sqrt(this.x*this.x +this.y*this.y);
	}

	public void mult(float s){
		x*=s;y*=s;
	}
	
	public int cross(Vector2 b){
		return this.x * b.y - this.y * b.x;
	}

	public float scalarProduct(Vector2 other){
		
		return this.x * other.x + this.y * other.y;
	}

	public void setLength(float f) {
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
