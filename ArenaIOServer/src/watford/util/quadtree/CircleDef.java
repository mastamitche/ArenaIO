package watford.util.quadtree;

import java.awt.Point;

public class CircleDef {
	public float x;
	public float y;
	public float rad;
	
	public CircleDef(float x, float y, float rad) {
		this.x = x;
		this.y = y;
		this.rad = rad;
	}

	public boolean contains(Point p) {
		return (p.x-x)*(p.x-x) + (p.y-y)*(p.y-y) < rad*rad;
	}

	public boolean contains(CircleDef r) {
		return (r.x-x)*(r.x-x) + (r.y-y)*(r.y-y) < (rad-r.rad)*(rad-r.rad);
	}

	public boolean intersects(CircleDef r) {
		return !contains(r) && partialContain(r);
	}

	public boolean partialContain(CircleDef r) {
		return (r.x-x)*(r.x-x) + (r.y-y)*(r.y-y) < (rad+r.rad)*(rad+r.rad);
	}
	public Rectangle getBounds(){
		return new Rectangle(x-rad, y-rad, rad*2, rad*2);
	}
	
	@Override
	public String toString() {
		return "[" + x + ", " + y + ", " + rad + "]";
	}
}
