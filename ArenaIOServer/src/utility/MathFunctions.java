package utility;


// Provides a few useful static functions for math, distance, etc
public class MathFunctions {
	
	public static double distance(Vector2 vec1, Vector2 vec2){
		return Math.sqrt((vec1.x-vec2.x)*(vec1.x-vec2.x)+(vec1.y-vec2.y)*(vec1.y-vec2.y));
	}
	
	public static double angleTo(Vector2 vec1, Vector2 vec2){
		return Math.atan2(vec2.y-vec1.y, vec2.x-vec1.x);
	}
	
	public static double angleDifference(double targetAngle, double currentAngle){
		targetAngle = targetAngle %(2*Math.PI);
		currentAngle = currentAngle %(2*Math.PI);
		if (targetAngle<0)targetAngle += 2*Math.PI;
		if (currentAngle<0)currentAngle += 2*Math.PI;
		
		while (Math.abs(targetAngle-(currentAngle+(2*Math.PI))) < Math.abs(targetAngle-currentAngle)) currentAngle+=(2*Math.PI);
		while (Math.abs(targetAngle-(currentAngle-(2*Math.PI))) < Math.abs(targetAngle-currentAngle)) currentAngle-=(2*Math.PI);
		
		return (targetAngle-currentAngle) % (2*Math.PI);
	}
	
	public static double sign(double number){
		return number<0?-1:number==0?0:1;
	}
	
}
