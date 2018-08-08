package com.Entity;

import watford.util.quadtree.CircleDef;

import com.Game.ConnectionHandler;
import com.Game.GameServer;
import com.Odessa.utility.PacketHelper;
import com.Odessa.utility.XSRandom;
import com.Odessa.utility.vec2;

public class Armour extends Entity {

	public static int count = 0;
	public static int maxCount = 10;
	public static Object countLock = new Object();

	public static int maxAmount =50;
	public int amount = 10;

	public Armour(GameServer server, vec2 pos, int amount) {
		super(server, Entity.TYPE_ARMOUR, pos, 0);
		this.amount = amount;
		synchronized (countLock) {
			count++;
		}
	}

	public Armour(GameServer server, vec2 pos) {
		super(server, Entity.TYPE_ARMOUR, pos, 0);
		amount = (int) (XSRandom.random() * maxAmount);
		synchronized (countLock) {
			count++;
		}
	}
	@Override
	byte[] getSpawnPacket() throws Exception {
		return PacketHelper.bytesFromParams(ConnectionHandler.c_entitySpawn, Entity.TYPE_ARMOUR, id, pos, amount);
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
