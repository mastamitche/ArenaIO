// Odessa 2016

package com.Odessa.utility;

public class vec2 {
	public float x;
	public float y;

	public vec2(float x, float y){
		this.x = x;
		this.y = y;
	}
	//public vec2(shortvec2 shortVec){
	//	x = shortVec.x;
	//	y = shortVec.y;
	//}
	public vec2(float angle){
		x = (float) Math.cos(angle);
		y = (float) Math.sin(angle);
	}
	
	public vec2 minus(vec2 other){
		return new vec2(x-other.x, y-other.y);
	}
	
	public vec2 scale(float val){
		return new vec2(x*val, y*val);
	}

	public vec2 add(vec2 other){
		return new vec2(x+other.x, y+other.y);
	}
	public float dot(vec2 other){
		return x*other.x + y*other.y;
	}
	

	public float distSquared(vec2 other){
		return (x-other.x)*(x-other.x)+(y-other.y)*(y-other.y);
	}
	public float dist(vec2 other){
		return (float) Math.sqrt(distSquared(other));
	}
	public float angleTo(vec2 other){
		return (float) Math.atan2(other.y-y, other.x-x);
	}
	public float angle(){
		return (float) Math.atan2(y, x);
	}
	public vec2 rotate(float angle){
		return new vec2(angle()+angle).scale(length());
	}
	@Override
	public vec2 clone(){
		return new vec2(x,y);
	}
	//public shortvec2 toShortVec(){
	//	return new shortvec2((short)x,(short)y);
	//}
	public float length(){
		return (float) Math.sqrt(x*x + y*y);
	}
	public float lengthSquared(){
		return (float) (x*x + y*y);
	}
	public vec2 normalize(){
		float length = length();
		return new vec2(x/length, y/length);
	}
	
	public boolean equals(vec2 obj) {
		return x == obj.x && y == obj.y;
	};
	
	@Override
	public String toString() {
		return "["+x+","+y+"]";
	}
}
