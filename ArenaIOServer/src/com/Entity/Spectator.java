package com.Entity;

import com.Game.ConnectionHandler;
import com.Game.GameServer;
import com.Odessa.utility.vec2;

public class Spectator extends Player {
	
	public Spectator(ConnectionHandler conn, GameServer gs, byte color, vec2 pos){
		super(conn, gs, pos, color);
		lastParent.remove(this);
		canFire = false;
		canCollect = false;
		isEthereal = true;
	}
	
	@Override
	public void onMove() {} // nothing

}
