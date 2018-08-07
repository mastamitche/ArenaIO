package com.Entity.BotModules;


import com.Entity.BotPlayer;
import com.Entity.Bullet;
import com.Entity.Entity;
import com.Entity.Player;
import com.Odessa.utility.MathFunctions;
import com.Odessa.utility.XSRandom;
import com.Odessa.utility.vec2;

public class AttackNear extends Module {
	public AttackNear(BotPlayer owner) {
		super(owner);
	}
	float lowestDistSq = 999999999f;
	Entity closest = null;

	@Override
	int assessUtility() {
		lowestDistSq = 999999999f;
		closest = null;
		synchronized(owner.relevantEntities){
			for (Entity s : owner.relevantEntities.values()){
				if (s == owner) continue;
				// if (s instanceof Pickupable) continue;
				if (s instanceof Bullet) continue;
				float distSq = (float) (s.pos.distSquared(owner.localPos) + XSRandom.random()*4f);
	
				if (distSq < lowestDistSq){
					closest = s;
					lowestDistSq = distSq;
				}
			}
		}
		if (closest == null)
			return -1;
		
		return 9999999;
	}

	@Override
	public boolean execute() {
		if (closest != null){
			owner.shootingTarget = closest;
			/*
			if (closest.pos.distSquared(owner.localPos) < 3.50f*3.50f)
				owner.targetPos = owner.localPos.clone();
			else
				owner.targetPos = closest.pos.clone();
			*/
			//if (owner.isDebugBot) System.out.println("Attacking id: " + closest.id + ", type: " + closest.entityType);
			//return true;
		}
		
		return false;
	}
}
