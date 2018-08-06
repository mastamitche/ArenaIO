// Odessa 2016

package utility;

public class Vector3 {
	public float x;
	public float y;
	public float z;

	public Vector3(float x, float y, float z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vector3 minus(Vector3 other){
		return new Vector3(x-other.x, y-other.y, z-other.z);
	}
	
	public Vector3 scale(float val){
		return new Vector3(x*val, y*val, z*val);
	}

	public Vector3 add(Vector3 other){
		return new Vector3(x+other.x, y+other.y,z+other.z);
	}
	public float dot(Vector3 other){
		return x*other.x + y*other.y + z*other.z;
	}
	

	public float distSquared(Vector3 other){
		return (x-other.x)*(x-other.x)+(y-other.y)*(y-other.y)+(z-other.z)*(z-other.z);
	}
	public float dist(Vector3 other){
		return (float) Math.sqrt(distSquared(other));
	}
	public float angleTo(Vector3 other){
		return (float) Math.atan2(other.y-y, other.x-x);
	}
	@Override
	public Vector3 clone(){
		return new Vector3(x,y,z);
	}
	//public shortvec2 toShortVec(){
	//	return new shortvec2((short)x,(short)y);
	//}
	public float length(){
		return (float) Math.sqrt(x*x + y*y + z*z);
	}
	public float lengthSquared(){
		return (float) (x*x + y*y + z*z);
	}
	public Vector3 normalize(){
		float length = length();
		return new Vector3(x/length, y/length, z/length);
	}
	
	public boolean equals(Vector3 obj) {
		return x == obj.x && y == obj.y && z == obj.z;
	};
	
	@Override
	public String toString() {
		return "["+x+","+y+","+z+"]";
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj != null && (obj instanceof Vector3) && equals((Vector3)obj);
	}

	public float getValue(int index){
		switch (index){
			case 0: return x;
			case 1: return y;
			case 2: return z;
		}
		return 0;
	}
}
