package com.Entity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import watford.util.quadtree.CircleDef;
import watford.util.quadtree.Rectangle;

import com.Game.ConnectionHandler;
import com.Game.GameServer;
import com.Odessa.utility.PacketHelper;
import com.Odessa.utility.vec2;

public class Player extends Entity {
	private ConnectionHandler conn;
	public float angle = 0;
	public byte color;
	public int name;
	public int gunID=-1;
	public int armour = 100;
	public int maxArmour = 100;
	public int health = 100;
	public int maxHealth = 100;
	public int size = 10;
	public int ammo = 0;
	public int magazine = 0;
	public int magazineSize = 10;
	public int maxAmmo = 250;
	public boolean isInLeaderBoard = false;
	public boolean canFire = true;
	public boolean canCollect = true;
	public boolean canMove = true;
	public boolean isEthereal = false;
	public boolean isDead = false;
	
	public static int spawnProtectionTime = 5000;
	
	class threadedEthereal extends Thread {
		@Override
		public void run(){
			isEthereal = true;
			try {Thread.sleep(spawnProtectionTime);} catch (InterruptedException e) {e.printStackTrace();}
			if (!isDead) isEthereal = false;
		}
	}
	
	public Player(ConnectionHandler conn, GameServer gs, vec2 pos, byte color){
		super(gs, (byte)0, pos, 20);
		this.conn = conn;
		server.addedActiveEntities.put((int) id, this);
		angle = (float) (Math.random()*Math.PI*2);
		server.updatePlaying(1);
		if (!(this instanceof BotPlayer)) server.updatePlayers(1);
		(new threadedEthereal()).start();
		this.color = color;
		health = maxHealth;
		armour = maxArmour;
	}
	
	@Override
	public void SpecificDestroy(){
		try {
			server.activeEntities.remove(id);
			server.updatePlaying(-1);
			if (!(this instanceof BotPlayer)) server.updatePlayers(-1);
			server.nm.RemoveEntry(name);
			conn = null; // don't bug the player with this entity anymore.
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	public void moveTo(vec2 pos) {
		if (!canMove) return;
		this.pos = pos;

		try {
			notifyOthers(getPositionPacket());
		} catch (Exception e) {
			e.printStackTrace();
		}
		onMove();
        
	}

	public void setRotation(float angle) {
		if (!canMove) return;
		this.angle = angle;
	}
	
	@Override
	public void specifictick(int runs){
		if (isDead || health <= 0){
			// Follow top players, perhaps?
			health = 0;
			
			if (this instanceof BotPlayer) kill();
			
			return;
		}
	}
	
	static float movementHalflife = 400f;

	public static float getSpeedMultiplier(int size){
		return (float) Math.pow(.95f, size/movementHalflife);
	}
	public float getSpeedMultiplier(){
		return getSpeedMultiplier(size);
	}
	static float shootHalflife = 1400f;
	public float getshootSpeedMultiplier(){
		return (float) Math.pow(.9f, size/shootHalflife);
		//return 1 / (1+(size / shootHalflife));
	}
	
	
	class threadedDeath extends Thread {
		@Override
		public void run(){
			canCollect = canFire = canMove = false;
			isEthereal = isDead = true;
			try {Thread.sleep(3000);} catch (InterruptedException e) {e.printStackTrace();}
			send((byte)ConnectionHandler.c_gameState, (byte)0);
		}
	}
	
	public void kill(){
		if (conn != null){
			if (isDead) return;
			server.trees[0].remove(this);
			(new threadedDeath()).start();
		} else
			Destroy();
	}
	
	
	public float takeDamage(float damage){
		if (isEthereal) return 0;
		if(armour > 0){
			if(armour - damage < 0){
				damage -= armour;
				armour = 0;
			}
			else
				armour -= damage;
				damage = 0;
		}
		if(health > 0)
			health -= damage;
		
		if(health <=0 ){
			health = 0;
			kill();
		}
		// Tell everyone
		try {
			notifyOthers(getInfoPacket());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return health;
	}
	
	public float hit(Bullet b){
		float damageDone = takeDamage(b.getDamage());
		return damageDone;
	}

	public float hit(Ammo a){
		ammo = (ammo += a.amount) > maxAmmo ? maxAmmo: ammo;
		try {
			conn.Send(getInfoPacket());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ammo;
	}
	
	public void reload(){
		if(ammo == 0) return;
		magazine = ammo > magazineSize ? magazineSize : ammo;
		ammo -= magazine;
		try {
			conn.Send(getInfoPacket());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public float hit(ShotGun g){
		gunID = 1;
		canFire = true;
		//Tell everyone
		magazineSize = 20;
		magazine = magazineSize;
		
		try {
			notifyOthers(getGunPacket());
			conn.Send(getInfoPacket());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return gunID;
	}
	
	public void fire(){
		if(!canFire) return;
		
		switch(gunID){
			case 1:
				//Shotgun
				float spread = 2f;
				float speed = 1f;
				int bulletAmount = magazine -= (magazine >= 8 ? 8 : magazine);
				for( int i=0; i < bulletAmount; i++ ){
		        	vec2 barrelDirection = new vec2((float)Math.cos(angle - spread * bulletAmount/2 + spread * i) , (float)Math.sin(angle - spread * bulletAmount/2 + spread * i));
		        	new Bullet(server, pos.add(new vec2(2,5)), barrelDirection.scale(speed), this.size, this);
				}
				
				break;
		}
		try {
			conn.Send(getInfoPacket());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public float hit(HealthPack hp){
		health = (health += hp.amount) > maxHealth ? maxHealth: health;
		try {
			notifyOthers(getInfoPacket());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return health;
	}

	public float hit(Armour ar){
		armour = (armour += ar.amount) > maxArmour ? maxArmour: armour;
		try {
			notifyOthers(getInfoPacket());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return armour;
	}
	
	@Override
	public byte[] getSpawnPacket() throws Exception{
		return PacketHelper.bytesFromParams(ConnectionHandler.c_entitySpawn, Entity.TYPE_PLAYER, id, color, pos, PacketHelper.getAngleByte(angle), size, name, health, maxHealth, armour, maxArmour, ammo, magazine, magazineSize, gunID);
	}
	public byte[] getPositionPacket() throws Exception{
		return PacketHelper.bytesFromParams(ConnectionHandler.c_setEntityPosition, id, pos, PacketHelper.getAngleByte(angle));
	}
	public byte[] getInfoPacket() throws Exception{
		return PacketHelper.bytesFromParams(ConnectionHandler.c_playerStats,id, health, maxHealth, armour, maxArmour, ammo, magazine, magazineSize);
	}
	public byte[] getGunPacket() throws Exception{
		return PacketHelper.bytesFromParams(ConnectionHandler.c_playerEquip, id, gunID );
	}
	public void sendPositionReset(){
		try {
			conn.Send(getPositionPacket());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void send(byte[] packet) {
		if (conn != null)
			conn.Send(packet);
	}
	public void send(Object... packet) {
		if (conn != null)
			conn.Send(packet);
	}
	
	public int getMVPScore(){
		return size;
	}
	
	@Override
	public CircleDef getBounds() {
		float width = (float) Math.ceil((1f + Math.sqrt(size) * ConnectionHandler.sizeMultiplier)*.74f);
		return new CircleDef(pos.x, pos.y, width/2f);
	}
}
