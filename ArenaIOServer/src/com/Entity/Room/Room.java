package com.Entity.Room;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
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
	public static int VERSION = 1;
	public static String[] ORENTAION_TO_WORD = new String[]{"Up","Right","Down","Left"};
	
	// All rooms 30 * 30 because they are 9 x 9 tiles so 10 each
	public static int ROOM_SIZE = 2000;
	public Tile[] tiles;
	public byte type;
	public vec2 coordinate;
	public int ID = 0;
	public byte orientation;
	
	public Room (vec2 coordinate, byte orientation, byte type , int ID){
		this.coordinate = coordinate;
		this.orientation = orientation;
		this.type = type;
		this.ID = ID;
	}
	public Room (byte orientation, byte type ){
		this.coordinate = new vec2(0,0);
		this.orientation = orientation;
		this.type = type;
	}
	
	Wall[] getAllWalls(){
		ArrayList<Wall> walls = new ArrayList<Wall>();
		for(int i = 0 ;  i < tiles.length;i++){
			for(int j =0 ; j < tiles[j].walls.length ;j++){
				walls.add(tiles[j].walls[j]);
			}
		}
		return (Wall[]) walls.toArray();
	}
	
	byte vec2Orientation(vec2 v){
		return (byte)(this.coordinate.x == v.x ? v.y > this.coordinate.y ? 0 : 2: v.x > this.coordinate.x ? 1: 3  );
	}
	
	public boolean canMatchTo(Room r){
		Wall[] walls = getAllWalls();
		for(int i =0 ; i < walls.length; i ++){
			Wall myDoor = walls[i];
			if(!myDoor.open) continue;
			Wall[] theirWalls = r.getAllWalls();
			for(int j =0 ; j < theirWalls.length; j ++){
				Wall theirDoor = theirWalls[j];
				if(!theirDoor.open)continue;
				if(myDoor.atPoint != (byte)4 && myDoor.orientation+2%4 == theirDoor.orientation && 
						oppositeVector(myDoor.atPoint,myDoor.orientation) == theirDoor.atPoint){
					return true;
				}
			}
		}
		return false;
	}
	
	public static byte oppositeVector(byte pos, byte orientation){
		byte ret = 0;
		if(orientation == 0){
			//up
			ret =(byte) (pos+6);
		}else if(orientation == 1){
			//right
			ret =(byte) (pos-2);
		}else if(orientation == 2){
			//down
			ret =(byte) (pos-6);
		}else if(orientation == 3){
			//left
			ret =(byte) (pos+2);
		}
		return ret;
	}
	
	public static Room LoadRoom(File f){
		try {
			ByteBuffer arr = ByteBuffer.wrap( Files.readAllBytes(f.toPath()));
			byte version = arr.get();
			if(version == 1){
				Tile[] tiles = new Tile[9];
				for(byte i = 0; i <9 ; i++){
					Wall[] walls = new Wall[]{
						new Wall (i,(byte)0, byteIntepreter(arr.get())) ,
						new Wall (i,(byte)1, byteIntepreter(arr.get())) ,
						new Wall (i,(byte)2, byteIntepreter(arr.get())) ,
						new Wall (i,(byte)3, byteIntepreter(arr.get())) 
					};
					tiles[i] = new Tile(walls,i);
				}
				Room room = new Room((byte)0,(byte)Integer.parseInt(f.getName()));
				room.tiles = tiles;
				return room;
			}
		} catch (IOException e) {
			System.out.println("Failed to load File " + f.getName());
			e.printStackTrace();
		}
		return null;
	}
	
	static boolean byteIntepreter(byte b){
		return b == 0 ? false : b == 1? true : null;
	}
	
	public Room clone(){
		Room retRoom = new Room(this.orientation, this.type);
		return retRoom;
	}


	byte[] getSpawnPacket() throws Exception {	
		return PacketHelper.bytesFromParams(ConnectionHandler.c_room, ID, true, type, orientation, coordinate); 
	}

}
