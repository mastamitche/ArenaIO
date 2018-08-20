package com.Entity.Room;

import com.Odessa.utility.vec2;

public class Tile {
	public boolean[] walls;
	public Door[] doors;
	public vec2 coordinates;
	
	public Tile(boolean[] walls, Door[] doors, vec2 coordinates){
		this.walls = walls;
		this.doors = doors;
		this.coordinates = coordinates;
	}
	
}
