package watford.util.quadtree;

import java.awt.Point;

import com.Odessa.utility.vec2;

public class Rectangle {
	public float x;
	public float y;
	public float width;
	public float height;
	
	public Rectangle(float x, float y, float w, float h) {
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;
	}

	public vec2 getTopLeft(){
		return new vec2(x, y);
	}
	public vec2 getBottomRight(){
		return new vec2(x+width, y+height);
	}
	
	public vec2 getCenter(){
		return new vec2(x+width/2, y+height/2);
	}

	// Point
	
	public boolean contains(Point p) {
		return p.x > x && p.x < x+width && p.y > y && p.y < y+height;
	}
	
	public boolean contains(vec2 p) {
		return p.x > x && p.x < x+width && p.y > y && p.y < y+height;
	}
	
	// Rectangle

	public boolean contains(Rectangle r) {
		return (r.x > x && r.x + r.width < x + width && r.y > y && r.y + r.height < y + height);
	}

	public boolean intersects(Rectangle r) {
		return !contains(r) && partialContain(r);
	}

	public boolean partialContain(Rectangle r) {
		return x+width > r.x && x < r.x+r.width && y+height > r.y && y < r.y+r.height;
	}
	
	// CircleDef
	public boolean contains(CircleDef c) {
		return c.x-c.rad > x && c.x+c.rad < x+width && c.y-c.rad > y && c.y+c.rad < y+height;
	}
	public boolean intersects(CircleDef c) {
		return !contains(c) && partialContain(c);
	}
	public boolean partialContain(CircleDef c) {
		// Not even partially within x
		float halfWidth = width/2;
		float cx = Math.abs(c.x - (x+halfWidth));
	    float xDist = halfWidth + c.rad;
	    if (cx > xDist) return false;
	    
	    // Not even partially within y
		float halfHeight = height/2;
	    float cy = Math.abs(c.y - (y+halfHeight));
	    float yDist = halfHeight + c.rad;
	    if (cy > yDist) return false;
	    
	    // Completely within both x and y
	    if (cx <= halfWidth || cy <= halfHeight)
	        return true;
	    
	    // Intercepting
	    return (cx - halfWidth) * (cx - halfWidth) + (cx - halfWidth) * (cx - halfWidth) <= c.rad * c.rad;
	}
	

	public java.awt.Rectangle toIntRect(){
		return new java.awt.Rectangle((int)x, (int)y, (int)width, (int)height);
	}
	
	@Override
	public String toString() {
		return "[" + x + ", " + y + ", " + width + ", " + height + "]";
	}
}
