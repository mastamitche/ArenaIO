package com.Entity.BotModules;
import java.util.ArrayList;

import com.Entity.BotPlayer;
import com.Entity.Entity;
import com.Entity.Player;
import com.Odessa.utility.vec2;

public class RunAway extends Module {
	public RunAway(BotPlayer owner) {
		super(owner);
	}

	float randomOffset = rndRange(-10f, 10f);
	int direction = 1;

	ArrayList<enemyDistancePair> closeEntities = new ArrayList<enemyDistancePair>();
	
	@Override
	int assessUtility() {
		/*
		closeEntities.clear();
		
		float lowestDist = 999999999f;
		Entity closest = null;
		synchronized(owner.relevantEntities){
			for (Entity s : owner.relevantEntities.values()){
				if (!(s instanceof Player) || s == owner) continue;
				
				float dist = owner.localPos.minus(s.pos).length() - owner.getRadius() - s.getRadius();
				
				Player s2 = (Player)s;
				
				closeEntities.add(new enemyDistancePair(s2, dist));
				if (dist < lowestDist){
					closest = s;
					lowestDist = dist;
				}
			}
		}

		if (closest == null)
			return -1;
		
		return (int) (randomOffset + 100f - (lowestDist * 20f));
		*/
		return -1;
	}

	@Override
	public boolean execute() {
		if (rndRange(0f, 10f) < 1f) direction *= -1;
		
		vec2 moveOffset = new vec2(0, 0);
		
		for (enemyDistancePair ddp : closeEntities){
			float influence = (float) (100f- ddp.dist * 35f);
			if (influence > 0){
				Entity e = (Entity)ddp.entity;
				vec2 diff = e.pos.minus(owner.localPos);
				moveOffset = moveOffset.minus(diff.scale(influence/5f));
			}
		}
		if (moveOffset.lengthSquared() < 1f){
			return false;
		}
		
		owner.targetPos = owner.localPos.add(moveOffset.rotate((float) (Math.PI * direction / 2)));
		
		
		//if (owner.isDebugBot) System.out.println(owner.id + " " +owner.localPos + " " + owner.targetPos);
		return true;
	}
}
