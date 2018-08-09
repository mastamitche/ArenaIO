package com.Entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.kohsuke.randname.RandomNameGenerator;

import com.Entity.BotModules.*;
import com.Game.ConnectionHandler;
import com.Game.GameServer;
import com.Odessa.utility.MathFunctions;
import com.Odessa.utility.PacketHelper;
import com.Odessa.utility.XSRandom;
import com.Odessa.utility.vec2;


public class BotPlayer extends Player {
	List<Module> Modules = Collections.synchronizedList(new ArrayList<Module>());
	
	public static RandomNameGenerator rng = new RandomNameGenerator();
	
	int framesBetweenCalculation = 25;

	public vec2 localPos;
	public vec2 targetPos;
	public Entity shootingTarget;
	public float gunAngle = 0f;
	
	static boolean taken = false;
	public boolean isDebugBot = false;
	public boolean botExists = true;
	
	Module oldModule;
	
	String myName = null;
	
	@Override
	public void specifictick(int runs) {
		super.specifictick(runs);
		
		if (!taken)
			isDebugBot = taken = true;
		
		
		// Collect 
		/*if (runs % 10 == 0){
			HashMap<Integer, Entity> entities = getCollidingEntities(new byte[]{Entity.TYPE_SHOTGUN});
			Iterator<Entry<Integer, Entity>> iterator = entities.entrySet().iterator();
	        while(iterator.hasNext()){
				Ammo entry = (Ammo)iterator.next().getValue();
	            if (entry != null){
        			byte[] packet;
					try {
						packet = this.getInfoPacket();
	        			notifyOthers(packet);
					} catch (Exception e) {
						e.printStackTrace();
					}
        			entry.Destroy();
	            }
	        }
		}*/
		try{
			if ((id + runs) % framesBetweenCalculation == 0){
	
					if (localPos == null || Float.isNaN(localPos.x)){
						//System.out.println("Rebooting " + id);
						localPos = pos.clone();
						targetPos = localPos.clone();
					}
	
					//update all utility values.
					synchronized (Modules){
						for (Module r:Modules)
							r.setUtility();
	
						//sort the modules by utility
						Collections.sort(Modules);
					}
			}
		} catch (java.lang.ArithmeticException e){
			// Dunno. Ignore this.
		} catch (Exception e){
			e.printStackTrace();
		}


		if ((id + runs) % 5 == 0){
			shootingTarget = null;

			if (Modules != null)
				synchronized (Modules){
					for (Module m:Modules){
						//if (isDebugBot)System.out.println(id + ": Running " + m.getUtility() + " " + m);
						try{
							if (m.execute()){
								oldModule = m;
								break;
							}
						} catch (Exception e){
							e.printStackTrace();
						}
						//if (isDebugBot)System.out.println("Failed!");
					}
				}
		}


		if (localPos == null || targetPos == null)
			return;
		
		// internally move
		if ((id + runs) % 2 == 0){
			float usedMovespeed = (getSpeedMultiplier() * 5) * GameServer.timeScale;
			if (!localPos.equals(targetPos)){
				float angle = (float) (pos.angleTo(targetPos));
				
				while (angle < curAngle - Math.PI) angle += Math.PI*2;
		        while (angle > curAngle + Math.PI) angle -= Math.PI*2;
		        
		        if (Math.abs(angle - curAngle) < turnSpeed*2)
		        	curAngle = angle;
		        else
		        	curAngle += turnSpeed*2f * MathFunctions.sign(angle - curAngle);
				
		        float movementMultiplier = (float) (1f-(Math.abs(curAngle - angle) / (Math.PI/2)));
		        vec2 movementDir = new vec2(angle).scale(movementMultiplier * usedMovespeed * (1/12.5f));
				localPos = localPos.add(movementDir);
				/*
				if (localPos.lengthSquared() > server.worldRadius*server.worldRadius){
					localPos = localPos.normalize().scale(server.worldRadius);
				}*/
			}
			
			
			if (shootingTarget != null){
				float angle = (float) (pos.angleTo(shootingTarget.pos));
				//System.out.println(angle);
		        while (angle < gunAngle - Math.PI) angle += Math.PI*2;
		        while (angle > gunAngle + Math.PI) angle -= Math.PI*2;
		        
		        if (Math.abs(angle - gunAngle) < gunTurnSpeed*2)
		        	gunAngle = angle;
		        else
		        	gunAngle += gunTurnSpeed*2f * MathFunctions.sign(angle - gunAngle);
				
			}
		}
		
		// Show move 5 times per second
		if ((id + runs) % 5 == 0){
			if (localPos == null || targetPos == null)
				return;
			setRotation(curAngle);
			moveTo(localPos);
			localPos = pos.clone();
		}
		
		
		long now = System.currentTimeMillis();
		if (now - lastShotTime > ((2000f/getshootSpeedMultiplier())/GameServer.timeScale)){
			float speed = /*7.5f*/6.5f * getSpeedMultiplier();
        	/*vec2 barrelDirection = new vec2((float)Math.cos(barrelAngle), (float)Math.sin(barrelAngle));
        	vec2 barrelOffset = barrelDirection.scale(1f + (float) Math.sqrt(size) * ConnectionHandler.sizeMultiplier * .55f);
        	// new Bullet(server, pos.add(barrelOffset), barrelDirection.scale(speed), 5*(450+size), size, this);
        	lastShotTime = System.currentTimeMillis();*/
		}
		
	}
	
	float curAngle = 0;
	float turnSpeed = (float) (2f * Math.PI / 180);
	
	long lastShotTime = System.currentTimeMillis();
	
	float gunTurnSpeed = (float) (2f/*3f*/ * Math.PI / 180);

	public static String lastDeadBotName = null;
	public static byte lastDeadBotColor = 0;
	
	public BotPlayer(GameServer server, vec2 pos){
		super(null, server, pos, (byte)(XSRandom.random()*256));
		
		if (isDead){
			Destroy();
			return;
		}
		
		String s = null;
		float rnd = XSRandom.random();

		if (lastDeadBotName != null && rnd < 0.5f){
			s = lastDeadBotName;
			lastDeadBotName = null;
			color = lastDeadBotColor;
		}
		
		if (s == null){
			s = rng.next();
			if (rnd < 0.8f){
				
				if (rnd < 0.2f)
					s = s.substring(0, s.indexOf(" "));
				else if (rnd < 0.4f)
					s = s.substring(s.indexOf(" ")+1);
				else if(rnd < 0.79f){
					String[] s1 = s.split(" ");
					s = s1[0].substring(0, (int) (XSRandom.random()*s1[0].length())) + s1[1].substring((int) (XSRandom.random()*(s1[1].length())));
				}else
					s = "";
			}
		}
		
		
		myName = s;
		this.name = server.nm.AddEntry(myName);
		
		localPos = pos.clone();
		//targetPos = localPos;
		targetPos = new vec2(0,0);

		Modules.add(new Eat(this));
		//Modules.add(new RunAway(this));
		Modules.add(new AttackNear(this));
	}

	@Override
	public void send(byte[] packet) {
	}
	@Override
	public void send(Object... packet) {
	}
	
	@Override
	public void SpecificDestroy() {
		super.SpecificDestroy();
		lastDeadBotName = myName;
		lastDeadBotColor = color;
		try {
			if (Modules != null)
				Modules.clear();
		} catch (Exception e){
			e.printStackTrace();
		}
	}
}
