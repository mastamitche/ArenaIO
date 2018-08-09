package com.Game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.WebSocketImpl;

import utility.quadTree.QuadTree;

import com.Entity.Ammo;
import com.Entity.Armour;
import com.Entity.BotPlayer;
import com.Entity.Entity;
import com.Entity.HealthPack;
import com.Entity.Player;
import com.Entity.ShotGun;
import com.Odessa.utility.MultiUniqueIDHandler;
import com.Odessa.utility.PacketHelper;
import com.Odessa.utility.UniqueIDHandler;
import com.Odessa.utility.XSRandom;
import com.Odessa.utility.vec2;

public class GameServer {
	
	public int worldSize = 20;
	
	public static float timeScale = 1;

	public int getPlayerCount(){
		return playing;
	}
	
	public static QuadTree[] trees = new QuadTree[]{
		new QuadTree(-4096, -4096, 8192, 8192), // Players
		new QuadTree(-4096, -4096, 8192, 8192), // Shotgun
		new QuadTree(-4096, -4096, 8192, 8192), // Bullet
		new QuadTree(-4096, -4096, 8192, 8192), // Ammo
		new QuadTree(-4096, -4096, 8192, 8192), // Armour
		new QuadTree(-4096, -4096, 8192, 8192), // Health Pack
	};
	

	GameServer self = this;


	// Entity cap: 15k
	public UniqueIDHandler<Entity> idHandler = new UniqueIDHandler<Entity>(15000);
	public MultiUniqueIDHandler<String> nm = new MultiUniqueIDHandler<String>();
	public Map<WebSocket, ConnectionHandler> playerFromConnection = Collections.synchronizedMap(new HashMap<WebSocket, ConnectionHandler>());
	public Map<Integer, Entity> activeEntities = Collections.synchronizedMap(new HashMap<Integer, Entity>());
	public Map<Integer, Entity> destroyedEntities = Collections.synchronizedMap(new HashMap<Integer, Entity>());
	public Map<Integer, Entity> addedActiveEntities = Collections.synchronizedMap(new HashMap<Integer, Entity>());
	
	int playing = 0;
	Object playingLock = new Object();
	public synchronized void updatePlaying(int increment){
		synchronized(playingLock){
			playing += increment;
		}
	}
	
	int playersPlaying = 0;
	public synchronized void updatePlayers(int increment){
		synchronized(playingLock){
			playersPlaying += increment;
		}
	}
	

	ConnectionHandler[] debugPlayers = null;
	public void getPlayers(){
		debugPlayers = new ConnectionHandler[playerFromConnection.size()];
		
		int o = 0;
		Iterator<ConnectionHandler> i = playerFromConnection.values().iterator();
        while (i.hasNext())
        	debugPlayers[o++] = i.next();
	}
	
	XSRandom rnd;
	public BotManager botManager;
	public playerUpdater playerUpdater;
	
	public serverDistributer distributer;
	
	public GameServer(serverDistributer distributer) {
		this.distributer = distributer;
		rnd = new XSRandom();
		
		RefillPickupables(maxPickupables);
		//nm = new MultiUniqueIDHandler<String>();

		(playerUpdater = new playerUpdater()).start();
		(botManager = new BotManager(this)).start();
	}
	
	public void sendToAll(byte[] byteList){
		synchronized (playerFromConnection){
			Iterator<ConnectionHandler> i = playerFromConnection.values().iterator();
	        while (i.hasNext()){
	        	ConnectionHandler p = i.next();
				try{
					p.Send(byteList);
				}catch (ConcurrentModificationException e){
					e.printStackTrace();
				}
			}
		}
	}
	
	long lastConnect = 0;
	String lastConnectIP = "";
	public void onNewConnection(WebSocket conn, ClientHandshake handshake) {
		// Here, we can veto the connection if it looks like a bot mass-joining
		
		if (System.currentTimeMillis() - lastConnect < 2000/timeScale && lastConnectIP.equals(conn.getRemoteSocketAddress())){
			conn.close(1008);
			lastConnect = System.currentTimeMillis();
			return;
		}
		lastConnect = System.currentTimeMillis();
		lastConnectIP = ""+conn.getRemoteSocketAddress();
		
		try{
			ConnectionHandler p = ((WebSocketImpl)conn).connectionHandler;
			playerFromConnection.put(conn, p);
			System.out.println("New connection " + conn.getRemoteSocketAddress());
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	public void removePlayer(WebSocket conn){
		Player p = playerFromConnection.remove(conn).player;
		if (p == null) p = ((WebSocketImpl)conn).connectionHandler.player;
		((WebSocketImpl)conn).connectionHandler.player = null;
		((WebSocketImpl)conn).connectionHandler = null;
		if (p != null){
			
			p.Destroy();
		}
	}
	
	public void onClose( WebSocket conn, int code, String reason, boolean remote ) {
		if (conn == null) return;
		removePlayer(conn);
		System.out.println("Closed: Reason: " + reason);
	}

	public void onError( WebSocket conn, Exception ex ) {
		System.out.println( "Socket Error, inside!");
		ex.printStackTrace();
		if (conn == null) return;
		onClose(conn, 520, "Errored", false);
	}

	public void onMessage( WebSocket conn, String message ) {
		System.out.println( "Got Game Server Message");
		conn.send( message );
	}
	
	public vec2 rndPosInWorld(){
		while (true){
			float x = rnd.nextFloat()*worldSize * 2 - worldSize;
			float y = rnd.nextFloat()*worldSize * 2 - worldSize;
			return new vec2(x, y);
		}
	}

	public void GC(){
		System.gc();
	}
	
	public void RefillPickupables(int maxAmount) {
		for (int i = 1; i <= maxAmount; i++) {
			if(Ammo.count < Ammo.maxCount){
				new Ammo(this, rndPosInWorld());
			}else if(ShotGun.count < ShotGun.maxCount){
				new ShotGun(this, rndPosInWorld());
			}else if(Armour.count < Armour.maxCount){
				new Armour(this, rndPosInWorld());
			}else if(HealthPack.count < HealthPack.maxCount){
				new HealthPack(this, rndPosInWorld());
			}else{
				break;
			}
		}
	}
	

	public Player getPlayer() {
		ConnectionHandler handler = (ConnectionHandler)playerFromConnection.values().iterator().next();
		return handler.player;
	}
	public Player getPlayerByName(String name) {
		synchronized (activeEntities){
			Iterator<Entry<Integer, Entity>> iterator = activeEntities.entrySet().iterator();
	        while(iterator.hasNext()){
	            Entity e = iterator.next().getValue();
	            if (e.entityType == Entity.TYPE_PLAYER){
	            	Player p = (Player) e;
	            	if (nm.GetEntry(p.name).toLowerCase().startsWith(name.toLowerCase()))
	            		return p;
	            }
	        }
		}
		return null;
	}

	int maxPickupables = 50;
	public static int minPlayerCount = 20;
	public static int botDecay = 0;
	public static float botDecayChance = 0.0001f;
	public static boolean printServerLag = false;
	public byte[] lastLeaderboard;
	
	long startTime = System.currentTimeMillis();
	long startFrame = 0;
	long frameDuration = 40;
	long curFrame = 0;
	float oldTimeScale = timeScale;
	
	class playerUpdater extends Thread{
		
		@Override
		public void run() {
			Thread.currentThread().setPriority(Thread.NORM_PRIORITY);
			int runs = 0;

			while (true){
				runs++;
				curFrame++;
				long time = System.currentTimeMillis();

				if (oldTimeScale != timeScale){
					startFrame = curFrame;
					startTime = time;
					oldTimeScale = timeScale;
				}
				
				long frameAddition = frameDuration*(curFrame-startFrame);
				long nextTime = startTime+(long)(frameAddition/timeScale);
				
				int timeToWait = (int) (nextTime - time);
				//if (printServerLag)System.out.println(timeToWait + "ms");
				
				if (printServerLag && timeToWait < -40)
					System.out.println("Server having difficulty keeping up! " + (-(timeToWait+40)) + "ms behind!");
				
				
				if (timeToWait > 0){
					try {Thread.sleep(timeToWait);} catch (InterruptedException e) {e.printStackTrace();}
				}
				try {
					// Spawn food up to 30 per frame
					RefillPickupables(30);

					synchronized (addedActiveEntities){
						Iterator<Entry<Integer, Entity>> iterator = addedActiveEntities.entrySet().iterator();
				        while(iterator.hasNext()){
				            Entry<Integer, Entity> e = iterator.next();
				            activeEntities.put(e.getKey(), e.getValue());
				            iterator.remove();
				        }
					}
					
					synchronized (activeEntities){
						Iterator<Entry<Integer, Entity>> iterator = activeEntities.entrySet().iterator();
				        while(iterator.hasNext()){
				        	try {
					            Entity e = iterator.next().getValue();
					            if (runs % 10 == 0 && botDecay != 0 && e instanceof BotPlayer && (Math.random() < botDecayChance)){
					            	((BotPlayer)e).takeDamage(botDecay);
					            }
								try {  e.tick(runs);  } catch (Exception e2){e2.printStackTrace();}
				        	} catch (Exception e3){
				        		e3.printStackTrace();
				        	}
				        }
					}

					synchronized (destroyedEntities){
						Iterator<Entry<Integer, Entity>> iterator = destroyedEntities.entrySet().iterator();
				        while(iterator.hasNext()){
				        	try {
					            Entry<Integer, Entity> e = iterator.next();
					            e.getValue().ActuallyDestroy();
					            iterator.remove();
				        	} catch (Exception e3){
				        		e3.printStackTrace();
				        	}
				        }
					}
					// Send player info
					synchronized (playerFromConnection){
						Iterator<ConnectionHandler> i = playerFromConnection.values().iterator();
				        while (i.hasNext()){
				        	ConnectionHandler p = i.next();
							try{
								p.ActuallySend();
							}catch (ConcurrentModificationException e){
								e.printStackTrace();
							}
						}
					}

		        } catch (java.lang.OutOfMemoryError e){
		        	System.exit(1);
		        } catch (Exception e){
		        	e.printStackTrace();
		        }
			}
		}
	}

	public Player getPlayer(int id){
		Entity e = idHandler.get(id);
		if (e == null) return null;
		return (Player) e;
	}
}