package com.Entity.BotModules;

import com.Entity.BotPlayer;
import com.Entity.Player;
import com.Odessa.utility.XSRandom;

public abstract class Module implements Comparable<Module>{
	private int utility;
	BotPlayer owner;
	Module(BotPlayer owner){
		this.owner = owner;
	}
	abstract int assessUtility();
	public void setUtility(){
		utility = assessUtility();
		//if (utility>100) utility = 100;
		if (utility<0)   utility = 0;
	}
	public int getUtility(){
		return utility;
	}
	public abstract boolean execute();
	public int compareTo(Module other){
		return Integer.compare(other.utility,utility);
	}

	static float rndRange(float min, float max){
		return (float)(XSRandom.random()*(max-min) + min);
	} 
}


class enemyDistancePair{
	Player entity;
	float dist;
	enemyDistancePair(Player entity, float distSq){
		this.entity = entity;
		this.dist = distSq;
	}
}