package com.Game;

import com.Entity.BotPlayer;
import com.Entity.Entity;
import com.Odessa.utility.vec2;

public class BotManager extends Thread {
	GameServer server;
	
	public BotManager(GameServer server){
		this.server = server;
	}
	
	public void run(){
		while (true){
			try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
			if (server.getPlayerCount() < GameServer.minPlayerCount)
				for (int i = 0; i < 10; i ++)
					if (server.getPlayerCount() < GameServer.minPlayerCount) addBot();
			//System.out.println(server.getPlayerCount() + "/" + minPlayerCount);
		}
	}
	
	public void addBot(){
		vec2 pos = new vec2(0,0);
    	for (int i = 0; i < 100; i ++){
			pos = server.rndPosInWorld();
			if (Entity.getEntitiesinRangeMap(pos, server, 7, new byte[]{Entity.TYPE_PLAYER, Entity.TYPE_BULLET}).size() > 0) continue;
		}
    	new BotPlayer(server, pos);
	}
}
