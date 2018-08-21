package com.Entity.Room;

import java.util.ArrayList;

import com.Game.ConnectionHandler;
import com.Odessa.utility.PacketHelper;
import com.Odessa.utility.vec2;

public class Tile {
	public boolean[] walls = new boolean[4];
	public Door[] doors = new Door[4];
	public vec2 coordinates;
	
	public Tile(boolean[] walls, Door[] doors, vec2 coordinates){
		this.walls = walls;
		this.doors = doors;
		this.coordinates = coordinates;
	}

	byte[] getSpawnPacket() throws Exception {
		// Don't need to send the coords because we will send them in order
		// ORDER: TL TM TR, ML MM MR, BL BM BR
		ArrayList<Byte> doorArr = new ArrayList<Byte>();
		for(int i = 0; i < 4 ; i ++){
			byte[] pack = doors[i].getSpawnPacket();
			for(int j = 0; j < pack.length; j++ )
				doorArr.add(pack[j]);
		}
		return PacketHelper.bytesFromParams(
				walls,
				doorArr
				); 
	}
}
