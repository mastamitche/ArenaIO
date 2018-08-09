package com.Entity;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import utility.quadTree.QuadTree;
import watford.util.quadtree.CircleDef;
import watford.util.quadtree.ISpatialObject;
import watford.util.quadtree.QuadTreeNode;
import watford.util.quadtree.Rectangle;

import com.Game.ConnectionHandler;
import com.Game.GameServer;
import com.Odessa.utility.PacketHelper;
import com.Odessa.utility.vec2;

public abstract class Entity implements ISpatialObject {
	public static final byte TYPE_PLAYER = 0;
	public static final byte TYPE_SHOTGUN = 1;
	public static final byte TYPE_BULLET = 2;
	public static final byte TYPE_AMMO = 3;
	public static final byte TYPE_ARMOUR = 4;
	public static final byte TYPE_HEALTHPACK = 5;
	
	static byte[] allEntityTypes = new byte[]{Entity.TYPE_PLAYER, Entity.TYPE_SHOTGUN, Entity.TYPE_BULLET, Entity.TYPE_AMMO, Entity.TYPE_ARMOUR, TYPE_HEALTHPACK};
	
	public byte entityType;
	@VariableReplication(maxBits=32)
	public int id;
	public vec2 pos;
	public int size;
	public GameServer server;
	// Entities relevant to me
	public Map<Integer, Entity> relevantEntities = Collections.synchronizedMap(new HashMap<Integer, Entity>());
	// Entities I am relevant to
	public Map<Integer, Entity> entitiesRelevantTo = Collections.synchronizedMap(new HashMap<Integer, Entity>());
	public boolean isValid = true;
	boolean caresAboutNearby = false;
	int nearbyRadius = 0;
	int relevancyUpdateFrequency = 500;
	
	public Entity(GameServer server, byte entityType, vec2 pos, int nearbyRadius){
		this.server = server;
		this.entityType = entityType;
		this.pos = pos;
		caresAboutNearby = nearbyRadius > 0;
		this.nearbyRadius = nearbyRadius;

		id = server.idHandler.AddEntry(this);
		server.trees[entityType].add(this);
		
		if (caresAboutNearby)
			updateNearby(nearbyRadius);
	}
	
	public boolean isVisibleTo(Player p){return true;}

	public static HashMap<Integer, Entity> getEntitiesinRangeMap(vec2 pos, GameServer server, float radius, byte[] types){
		return getEntitiesinRangeMap(pos, server, radius, types, false);
	}
	

	public static HashMap<Integer, Entity> getEntitiesinRangeMap(vec2 pos, GameServer server, float radius, byte[] types, boolean debug){
		HashMap<Integer, Entity> entries = new HashMap<Integer, Entity>();
		for (byte b : types)
			server.trees[b].getEntitiesInRegion(entries, pos.x, pos.y, radius/*, debug*/);
		
		Iterator<Entry<Integer, Entity>> listIterator = entries.entrySet().iterator();
        while (listIterator.hasNext()) {
            Entity e = listIterator.next().getValue();
            if (e.isValid /*&& e.pos.distSquared(pos) <= radius*radius*/){
            }else
            	listIterator.remove();
        }
        
		return entries;
	}

	public HashMap<Integer, Entity> getEntitiesinRangeMap(float radius, byte[] types){
		return getEntitiesinRangeMap(pos, server, radius, types, false);
	}
	public HashMap<Integer, Entity> getEntitiesinRangeMap(float radius, byte[] types, boolean debug){
		return getEntitiesinRangeMap(pos, server, radius, types, debug);
	}
	
	public HashMap<Integer, Entity> getCollidingEntities(byte[] types){
		return getCollidingEntities(types, false);
	}
	
	public HashMap<Integer, Entity> getCollidingEntities(byte[] types, boolean debug){
		HashMap<Integer, Entity> entries = new HashMap<Integer, Entity>();
    	//float radius = getRadius();
		CircleDef me = getBounds();
		//me.x -= 2;
		//me.y -= 2;
		//me.width += 4;
		//me.height += 4;
		for (byte b : types)
			server.trees[b].getEntitiesInRegion(entries, me, debug);

		if (debug) System.out.println("Entries length: " + entries.size());
		
		Iterator<Entry<Integer, Entity>> listIterator = entries.entrySet().iterator();
        while (listIterator.hasNext()) {
            Entity e = listIterator.next().getValue();
        	if (/*e == this || */!e.isValid){
        		listIterator.remove();
        		continue;
        	}
        	
        	//float radius2 = getRadius() + e.getRadius();
            //if (!me.intersects(e.getBounds()))
            //	listIterator.remove();
            
        }
		if (debug) System.out.println("Entries length after: " + entries.size());
        
		return entries;
	}
	
	public void checkRelevancyToOthers(float radius){
		HashMap<Integer, Entity> entries = getEntitiesinRangeMap(radius, new byte[]{Entity.TYPE_PLAYER});
		
		Iterator<Entry<Integer, Entity>> listIterator = entries.entrySet().iterator();
        while (listIterator.hasNext()) {
            Player e = (Player) listIterator.next().getValue();
            if (pos.distSquared(e.pos) < e.nearbyRadius * e.nearbyRadius)
            	e.onStartReplicate(this);
        }
	}

	public void clearRelevent(){
		synchronized(relevantEntities){
			Iterator<Entry<Integer, Entity>> iterator = relevantEntities.entrySet().iterator();
	        while(iterator.hasNext()){
	        	Entry<Integer, Entity> entry = iterator.next();
	        	onStopReplicate(entry.getValue(), true, false);
	        	iterator.remove();
	        }
		}
		
        relevantEntities.clear();
	}
	
	public void clearReleventTo(){
		synchronized(entitiesRelevantTo){
			Iterator<Entry<Integer, Entity>> iterator = entitiesRelevantTo.entrySet().iterator();
	        while(iterator.hasNext()){
	        	Entry<Integer, Entity> entry = iterator.next();
	        	entry.getValue().onStopReplicate(this, false, true);
	        	iterator.remove();
	        }
		}
        entitiesRelevantTo.clear();
	}
	
	public void Destroy(){
		server.destroyedEntities.put(id, this);
	}
	
	// Entity destroyed
	public void ActuallyDestroy(){
		if (!isValid) return;
		isValid = false;
		
		server.idHandler.RemoveEntry(id); //recycle this ID
		
		try{
			server.trees[entityType].remove(this);
			SpecificDestroy();
		}catch(Exception e){e.printStackTrace();}
		
		clearRelevent();
		clearReleventTo();
	}
	void SpecificDestroy(){}
	
	// Enter view of player
	public void onStartReplicate(Entity p){
		if (!p.isValid || !isValid) return;
		try {
			send(p.getSpawnPacket());
		} catch (Exception e) {
			e.printStackTrace();
		}
		relevantEntities.put(p.id, p);
		p.entitiesRelevantTo.put(id, this);
		specificOnInView(p);
	}
	void specificOnInView(Entity p){};

	abstract byte[] getSpawnPacket() throws Exception;
	abstract void send(byte[] packet);

	// Exit view of player
	public void onStopReplicate(Entity p, boolean deletedForMe, boolean deletedForThem){
		try {
			send(p.getDespawnPacket());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!deletedForMe)
			relevantEntities.remove(p.id);
		if (!deletedForThem)
			p.entitiesRelevantTo.remove(id);
		specificOnOutOfView(p);
	}
	void specificOnOutOfView(Entity p){};


	public void notifyOthers(byte[] toSend){
		synchronized (entitiesRelevantTo){
			Iterator<Entry<Integer, Entity>> iterator = entitiesRelevantTo.entrySet().iterator();
	        while(iterator.hasNext()){
	        	iterator.next().getValue().send(toSend);
	        }
		}
	}
	public void notifyOthersAndSelf(byte[] toSend){
		notifyOthers(toSend);
        send(toSend);
	}
	
	public void tick(int runs){
		if (!isValid){
			Destroy();
			return;
		}
		if (caresAboutNearby) updateNearby(nearbyRadius);
		specifictick(runs);
	};
	void specifictick(int runs){}
	
	byte[] getDespawnPacket() throws Exception{return PacketHelper.bytesFromParams(ConnectionHandler.c_entityDespawn, id);}

	public vec2 getPos() {return pos;}
	
	long lastRelevancyUpdateTime = 0;
	public void updateNearby(int radius){
		long time = System.currentTimeMillis();
		if (time - lastRelevancyUpdateTime > relevancyUpdateFrequency / GameServer.timeScale){
			setRelevantEntities(getEntitiesinRangeMap(/*this instanceof BotPlayer ?*/ radius /*: 10000*/, allEntityTypes, this instanceof BotPlayer));
			lastRelevancyUpdateTime = time;
		}
	}
	
	
    public void setRelevantEntities(HashMap<Integer, Entity> newM){
    	try{
			//if (this instanceof Player && !(this instanceof BotPlayer) && (Math.abs(newM.size() - relevantEntities.size()) > 20)){
	    	//	System.out.println("New size: " + newM.size() + " old size: " + relevantEntities.size());
			//}
			
    		
    		// Look to add (go through new entries, and if it isn't in old list, add it)
    		Iterator<Entry<Integer, Entity>> iterator = newM.entrySet().iterator();
	        while(iterator.hasNext()){
				Entity entry = iterator.next().getValue();
	            if (!relevantEntities.containsKey(entry.id) && entry != null && entry != this)
	            	onStartReplicate(entry);
	        }

	        synchronized(relevantEntities) {
				// Look to remove
	    		iterator = relevantEntities.entrySet().iterator();
		        while(iterator.hasNext()){
					Entity entry = iterator.next().getValue();
					if (!newM.containsKey(entry.id) && entry != null){
						iterator.remove();
						onStopReplicate(entry, true, false);
					}
		        }
	        }
	        
	        relevantEntities = Collections.synchronizedMap(newM);
	        
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    public void onMove(){
    	//if (lastParent != null)
    	//	lastParent.update(this);
    	parentTree.refresh(this);
    }
    
    public static boolean byteArrayContains(byte[] arr, byte b){
    	for (byte o : arr)
    		if (b == o) return true;
    	return false;
    }

    public abstract CircleDef getBounds();

    public Rectangle getBoundsRect(){
    	float size = getBounds().rad;
    	return new Rectangle(pos.x-size, pos.y-size, size*2,size*2);
    };
	
	
	public boolean containedFullyBy(Rectangle rect) {
		return rect.contains(getBounds());
	}
	public boolean containedPartiallyBy(CircleDef me) {
		return me.partialContain(getBounds());
	}
	public boolean containedPartiallyBy(Rectangle me) {
		return me.partialContain(getBounds());
	}
	//public boolean intersects(GeneralPath path);
	public boolean intersects(Rectangle rect) {
		return rect.intersects(getBounds());
	}
	
	private transient Hashtable<QuadTreeNode, QuadTreeNode> parents;
	
	public boolean intersects(Point pnt) {
		return getBounds().contains(pnt);
	}
	
	public boolean isPoint(){
		return false;
	}
	
	public Point centroid(){
		return new Point((int)pos.x, (int)pos.y);
	}
	
	ArrayList<QuadTreeNode> knownParents = new ArrayList<QuadTreeNode>();
	QuadTreeNode lastParent = null;
	
	public final void deleteParent(QuadTreeNode p) {
		this.parents.remove(p);
		knownParents.remove(p);
		lastParent = knownParents.size() > 0 ? knownParents.get(0) : null;
	}
	
	public final void addParent(QuadTreeNode p) {
		this.parents.put(p,p);
		knownParents.add(0, p);
		lastParent = p;
	}
	
	public final boolean hasParent(QuadTreeNode p) {
		if(parents == null)
			parents = new Hashtable();
		return parents.containsKey(p);
	}
	
	public QuadTree parentTree = null;
	
}
