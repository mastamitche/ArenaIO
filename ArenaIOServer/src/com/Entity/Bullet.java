package com.Entity;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import watford.util.quadtree.CircleDef;
import watford.util.quadtree.Rectangle;

import com.Game.ConnectionHandler;
import com.Game.GameServer;
import com.Odessa.utility.PacketHelper;
import com.Odessa.utility.vec2;

public class Bullet extends Entity {

	long lastUpdateMS;
	vec2 velocity;
	Player owner;
	boolean tellClientsOfOwner;
	
	public Bullet(GameServer server, vec2 pos, vec2 velocity, int size, Player owner) {
		super(server, Entity.TYPE_BULLET, pos, 0);
		this.velocity = velocity;
		lastUpdateMS = System.currentTimeMillis();
		this.size = size;
		this.owner = owner;
		server.addedActiveEntities.put((int) id, this);
		checkRelevancyToOthers(10);
	}
	
	@Override
	public void SpecificDestroy(){
		server.activeEntities.remove(id);
		owner = null;
	}


	public byte[] getPositionPacket() throws Exception{
		return PacketHelper.bytesFromParams(ConnectionHandler.c_setEntityPosition, id, pos);
	}
	@Override
	byte[] getSpawnPacket() throws Exception {
		return PacketHelper.bytesFromParams(ConnectionHandler.c_entitySpawn, Entity.TYPE_BULLET, id, pos, velocity.scale(GameServer.timeScale));
	}

	@Override
	void send(byte[] spawnPacket) {
		
	}
	
	public float getDamage(){
		return 10;
	}

	int updateDelay = 1;
	int currentDelay = 0;
	long lastUpdate;
	@Override
	public void specifictick(int runs){
		
		if (++currentDelay == updateDelay){
			long time = System.currentTimeMillis();
			
			lastUpdateMS = time;
			onMove();
			
			// Hit players
			Map<Integer, Entity> entities = getCollidingEntities(new byte[]{Entity.TYPE_PLAYER});
			Iterator<Entry<Integer, Entity>> iterator = entities.entrySet().iterator();
	        while(iterator.hasNext()){
				Player entry = (Player)iterator.next().getValue();
	            if (entry != null && entry != owner){
	            	entry.hit(this);
	            	// Could send back amount hit
	            	break;
	            }
	        }
		}
		
	}
	
	@Override
	public CircleDef getBounds() {
		float width = (float) (1 + Math.sqrt(size) * ConnectionHandler.sizeMultiplier) / 7.5f;
		return new CircleDef(pos.x, pos.y, width/2f);
	}
	
}
