package com.Entity.Room;

import com.Odessa.utility.vec2;

public class Door {
	public vec2 coordinates;
	public byte orientation;
	public boolean open;
	
	public Door(vec2 pos, byte orientation, boolean open){
		this.coordinates = pos;
		this.orientation = orientation;
		this.open = open;
	}
	
	public void setIsOpen(boolean open){
		this.open = open;
	}
	
}
