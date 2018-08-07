package com.Entity;

import watford.util.quadtree.CircleDef;

import com.Game.ConnectionHandler;
import com.Game.GameServer;
import com.Odessa.utility.PacketHelper;
import com.Odessa.utility.vec2;

public class ShotGun extends Entity {

	public static int count = 0;
	public static int maxCount = 3;
	public static Object countLock = new Object();
	
	public ShotGun(GameServer server, vec2 pos) {
		super(server, Entity.TYPE_SHOTGUN, pos, 0);
		synchronized (countLock) {
			count ++;
		}
	}

	@Override
	byte[] getSpawnPacket() throws Exception {
		return PacketHelper.bytesFromParams(ConnectionHandler.c_entitySpawn, Entity.TYPE_SHOTGUN, id, pos);
	}

	@Override
	void send(byte[] spawnPacket) {}

	
	@Override
	void SpecificDestroy() {
		synchronized (countLock) {
			count --;
		}
	}
	
	@Override
	public CircleDef getBounds() {
		return new CircleDef(pos.x, pos.y, .5f);
	}

}
