package com.Entity.Room;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.Entity.Entity;
import com.Game.ConnectionHandler;
import com.Odessa.utility.PacketHelper;
import com.Odessa.utility.vec2;

public class Room{
	public static byte TYPE_BASIC = 0;
	
	private static final long serialVersionUID = 1L;

	public static String[] ORENTAION_TO_WORD = new String[]{"Up","Right","Down","Left"};
	
	// All rooms 30 * 30 because they are 9 x 9 tiles so 10 each
	public static int ROOM_SIZE = 2000;
	public static int roomIDs = 0;
	public List<Door> doors = Collections.synchronizedList(new ArrayList<Door>());
	public Tile[] tiles;
	public byte type;
	public vec2 coordinate;
	public int ID = roomIDs++;
	public byte orientation;
	
	public Room (vec2 coordinate, byte orientation, byte type ){
		this.coordinate = coordinate;
		this.orientation = orientation;
		this.type = type;
	}
	
	public boolean canMatchTo(Room r){
		for(int i =0 ; i < doors.size(); i ++){
			Door myDoor = doors.get(i);
			if(!myDoor.open) continue;
			for(int j =0 ; j < r.doors.size(); j ++){
				Door theirDoor = r.doors.get(j);
				if(!theirDoor.open)continue;
				if(myDoor.orientation+2%4 == theirDoor.orientation && 
						oppositeVector(myDoor.coordinates,myDoor.orientation) == theirDoor.coordinates){
					return true;
				}
			}
		}
		return false;
	}
	
	public static vec2 oppositeVector(vec2 pos, byte orientation){
		vec2 ret = new vec2(0,0);
		if(orientation == 0){
			//up
			ret = new vec2(pos.x,-1);
		}else if(orientation == 1){
			//right
			ret = new vec2(-1,pos.y);
		}else if(orientation == 2){
			//down
			ret = new vec2(pos.x,1);
		}else if(orientation == 3){
			//left
			ret = new vec2(1,pos.y);
		}
		return ret;
	}
	
	public static Room LoadRoom(File f){
		try {
			Files.readAllBytes(f.toPath());
		} catch (IOException e) {
			System.out.println("Failed to load File " + f.getName());
			e.printStackTrace();
		}
	}


	byte[] getSpawnPacket() throws Exception {	
		ArrayList<Byte> tileArr = new ArrayList<Byte>();
		for(int i = 0; i < 9 ; i ++){
			byte[] pack = tiles[i].getSpawnPacket();
			for(int j = 0; j < pack.length; j++ )
				tileArr.add(pack[j]);
		}
		return PacketHelper.bytesFromParams(ConnectionHandler.c_room, ID, type,orientation,tileArr); 
	}

}
