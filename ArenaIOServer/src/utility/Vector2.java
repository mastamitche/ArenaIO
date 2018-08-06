// Odessa 2016

package utility;

public class Vector2 {
	public float x;
	public float y;
	
	static XSRandom rnd = new XSRandom();

	public Vector2(float x, float y){
		this.x = x;
		this.y = y;
	}
	//public vec2(shortvec2 shortVec){
	//	x = shortVec.x;
	//	y = shortVec.y;
	//}
	public Vector2(float angle){
		x = (float) Math.cos(angle);
		y = (float) Math.sin(angle);
	}
	public static Vector2 inRadius(float radius, XSRandom rnd){
		Vector2 startPos = new Vector2(rnd.random()*radius*2-radius,rnd.random()*radius*2-radius);
		while(startPos.lengthSquared() > radius*radius){
			startPos = new Vector2(rnd.random()*radius*2-radius,rnd.random()*radius*2-radius);
		}
		return startPos;
	}
	public static Vector2 inRadius(float radius){
		Vector2 startPos = new Vector2(rnd.random()*radius*2-radius,rnd.random()*radius*2-radius);
		while(startPos.lengthSquared() > radius*radius){
			startPos = new Vector2(rnd.random()*radius*2-radius,rnd.random()*radius*2-radius);
		}
		return startPos;
	}
	
	public Vector2 clamp(float min, float max){
		float lenSq = lengthSquared();
		float adjLength = -1;
		if (lenSq < min * min) adjLength = min;
		if (lenSq > max * max) adjLength = max;
		if (adjLength == -1) return clone();
		return normalize().scale(adjLength);
	}
	
	public Vector2 minus(Vector2 other){
		return new Vector2(x-other.x, y-other.y);
	}
	
	public Vector2 scale(float val){
		return new Vector2(x*val, y*val);
	}

	public Vector2 add(Vector2 other){
		return new Vector2(x+other.x, y+other.y);
	}
	public float dot(Vector2 other){
		return x*other.x + y*other.y;
	}

	public float distSquared(Vector2 other){
		return (x-other.x)*(x-other.x)+(y-other.y)*(y-other.y);
	}
	public float dist(Vector2 other){
		return (float) Math.sqrt(distSquared(other));
	}
	public float angleTo(Vector2 other){
		return (float) Math.atan2(other.y-y, other.x-x);
	}
	public float angle(){
		return (float) Math.atan2(y, x);
	}
	public Vector2 rotate(float angle){
		return new Vector2(angle()+angle).scale(length());
	}
	
	float PI = (float)Math.PI;
	public Vector2 rotateTowards(float angle, float amount){
		float myAngle = angle();
		while (angle > myAngle + PI) angle -= PI*2;
		while (angle < myAngle - PI) angle += PI*2;
		
		if (Math.abs(myAngle-angle) < amount)
			return new Vector2(angle).scale(length());
		
		return new Vector2(myAngle + Math.copySign(amount, angle-myAngle)).scale(length());
	}
	@Override
	public Vector2 clone(){
		return new Vector2(x,y);
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
	public Vector2 normalize(){
		float length = length();
		if (length == 0)
			return new Vector2(0, 0);
		return new Vector2(x/length, y/length);
	}

	public Vector2 randomizeAngle(float randomization){
		return new Vector2(angle() + (XSRandom.random() * randomization * 2) - randomization);
	}
	
	public boolean equals(Vector2 obj) {
		return x == obj.x && y == obj.y;
	};
	
	@Override
	public String toString() {
		return "["+x+","+y+"]";
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj != null && (obj instanceof Vector2) && equals((Vector2)obj);
	}
	
	public float getValue(int index){
		switch (index){
			case 0: return x;
			case 1: return y;
		}
		return 0;
	}
	
}
