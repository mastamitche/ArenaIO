package com.Entity.Room;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.Entity.Entity;
import com.Entity.Player;
import com.Game.GameServer;
import com.Odessa.utility.XSRandom;
import com.Odessa.utility.vec2;

public class RoomManager {
	// Coordinates to rooms which are unique
	public static ArrayList<Room> ROOMS = new ArrayList<Room>();
	public Map<vec2, Room> rooms = Collections
			.synchronizedMap(new HashMap<vec2, Room>());
	// All rooms without all siblings (up,right,down,left)
	public Map<Integer, Room> roomsNotCompleted = Collections
			.synchronizedMap(new HashMap<Integer, Room>());
	// ID's lookup table so i don't have to send two floats up and down
	public Map<Integer, Room> roomIDMap = Collections
			.synchronizedMap(new HashMap<Integer, Room>());

	public static int NEW_PLAYER_THRESHOLD = 2;
	public static int MINIMUM_ROOMS = 5;
	public GameServer server;

	XSRandom rnd;

	public RoomManager(GameServer server) {
		this.server = server;

	}

	void listFilesForFolder(final File folder) {
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				listFilesForFolder(fileEntry);
			} else {
				ROOMS.add(Room.LoadRoom(fileEntry));
			}
		}
	}

	void LoadRooms() {
		System.out.println("Loading Rooms...");
		listFilesForFolder(new File("rooms"));
	}

	public void tick() {
		int playerCount = Player.players.size();
		if (rooms.size() < MINIMUM_ROOMS
				|| rooms.size() < playerCount / NEW_PLAYER_THRESHOLD) {
			try {
				Room room =createNext();
			} catch (Exception e) {
				System.err.println("Trouble create new rooms");
			}
		}
	}

	Room createRoom(int type, vec2 pos) {
		Room rm= ROOMS.get(type).clone();
		rm.coordinate = pos;
		return rm;
	}

	ArrayList<Room> getCandidates(Room rm){
		ArrayList<Room> ret = new ArrayList<Room>();
		for( int i= 0 ; i <  ROOMS.size();i++){
			if(ROOMS.get(i).canMatchTo(rm)){
				ret.add(ROOMS.get(i));
			}
		}
		return ret;
	}

	public Room createNext() throws Exception {
		Room room = null;
		ArrayList<Room> candidates = new ArrayList<Room>();
		int tries = 0;
		while(tries < 1000 && candidates.size() < 1 ){
			int pos = rnd.nextInt(roomsNotCompleted.size());
			room = roomsNotCompleted.get(pos);
			candidates = getCandidates(room);
		}
		if(tries == 10000){
			throw new Exception("Could not find suitable candidate");
		}
		room = candidates.get(rnd.nextInt(candidates.size()));
		int start = rnd.nextInt(4);
		for (int i = 0; i < 4; i++) {
			int currPos = start++ % 4;
			if (currPos == 0) {
				vec2 up = new vec2(room.coordinate.x, room.coordinate.y + 1);
				if (!rooms.containsKey(up))
					room.coordinate = up;
			}
			if (currPos == 1) {
				vec2 right = new vec2(room.coordinate.x + 1, room.coordinate.y);
				if (!rooms.containsKey(right))
					room.coordinate = right;
			}
			if (currPos == 2) {
				vec2 down = new vec2(room.coordinate.x, room.coordinate.y - 1);
				if (!rooms.containsKey(down))
					room.coordinate = down;
			}
			if (currPos == 3) {
				vec2 left = new vec2(room.coordinate.x, room.coordinate.y - 1);
				if (!rooms.containsKey(left))
					room.coordinate = left;
			}
		}
		return room;
	}

	// Retuns room array up:0, right:1, down:2, left:3; clockwise
	public Room[] getNeighbourRooms(Room r) {
		Room[] retArr = new Room[4];
		float x = r.coordinate.x;
		float y = r.coordinate.y;
		Room up = rooms.get(new vec2(x, y + 1));
		Room down = rooms.get(new vec2(x, y - 1));
		Room left = rooms.get(new vec2(x - 1, y));
		Room right = rooms.get(new vec2(x + 1, y));
		if (up != null)
			retArr[0] = up;
		if (down != null)
			retArr[1] = down;
		if (left != null)
			retArr[0] = left;
		if (right != null)
			retArr[1] = right;
		return retArr;
	}
}
