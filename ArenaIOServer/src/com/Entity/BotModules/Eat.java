package com.Entity.BotModules;

import java.util.HashMap;

import com.Entity.BotPlayer;
import com.Entity.Entity;
import com.Odessa.utility.XSRandom;

public class Eat extends Module {
	public Eat(BotPlayer owner) {
		super(owner);
	}

	float randomOffset = rndRange(-5f, 10f);

	@Override
	int assessUtility() {
		return 20;
	}

	Entity target = null;
	
	@Override
	public boolean execute() {
		
		// Scan for nearby food
		if (target == null || !target.isValid){
			
			/*HashMap<Integer, Entity> entities = owner.getEntitiesinRangeMap(10, new byte[]{Entity.TYPE_FOOD});
			
			float lowestDistSq = 999999999f;
			for (Entity s : entities.values()){
				float distSq = (float) (((Pickupable)s).pos.distSquared(owner.localPos) + XSRandom.random()*3f*3f);//*400f*400f);
				if (distSq < lowestDistSq){
					target = s;
					lowestDistSq = distSq;
				}
			}*/
		}
		
		if (target != null && target.isValid){
			owner.targetPos = target.pos.add(target.pos.minus(owner.localPos).normalize().scale(4));
			return true;
		}
		return false;
	}
}
