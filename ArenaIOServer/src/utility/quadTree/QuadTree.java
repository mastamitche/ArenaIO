package utility.quadTree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.*;

import watford.util.quadtree.CircleDef;
import watford.util.quadtree.Rectangle;

import com.Entity.Entity;
import com.Game.GameServer;
import com.Odessa.utility.vec2;

public class QuadTree {
	private QuadTree[] subTrees;
	
	//private ArrayList<Entity> entries = new ArrayList<Entity>();
	private List<Entity> entries = Collections.synchronizedList(new ArrayList<Entity>());
	
	Rectangle bounds;
	
	QuadTree parent;
	
	int minSize = 2;
	
	vec2 midPoint;
	
	int numContains = 0;
	
	Lock lock = null;
	
	final static private int idealObjects = 3;
	
	private QuadTree rootTree = null;
	
	//boolean debug = false;
	
	public QuadTree(Rectangle bounds){//, boolean debug){
		this(bounds, null, null);
		//this.debug = debug;
	}
	public QuadTree(float x, float y, float width, float height){//, boolean debug){
		this(new Rectangle(x, y, width, height), null, null);
		//this.debug = debug;
	}
	
	// constructor
	private QuadTree(Rectangle bounds, QuadTree parent, QuadTree rootTree) {
		this.bounds = bounds;
		this.parent = parent;
		midPoint = bounds.getCenter();
		this.rootTree = rootTree;
		if (parent == null){
			this.rootTree = this;
			lock = new ReentrantLock();
		}
	}

	// get rect position
	private int getPointPosition(vec2 point){
		int toReturn = 0;
		if (point.x >= midPoint.x) toReturn ++;
		if (point.y >= midPoint.y) toReturn +=2;
		return toReturn;
	}
	private int getRectPosition(Rectangle bounds){
		if (subTrees == null) return -1;
		int topLeftIndex = getPointPosition(bounds.getTopLeft());
		int bottomRightIndex = getPointPosition(bounds.getBottomRight());
		if (topLeftIndex == bottomRightIndex) return topLeftIndex;
		return -1;
	}
	private int getEntryPosition(Entity entry){
		return getRectPosition(entry.getBoundsRect());
	}

	// split
	public void split(){
		rootTree.lock.lock();
		subTrees = new QuadTree[4];
		subTrees[0] = new QuadTree(new Rectangle(bounds.x, bounds.y, bounds.width/2, bounds.height/2), this, rootTree); // top-left
		subTrees[1] = new QuadTree(new Rectangle(midPoint.x, bounds.y, bounds.width/2, bounds.height/2), this, rootTree); // top-right
		subTrees[2] = new QuadTree(new Rectangle(bounds.x, midPoint.y, bounds.width/2, bounds.height/2), this, rootTree); // bottom-left
		subTrees[3] = new QuadTree(new Rectangle(midPoint.x, midPoint.y, bounds.width/2, bounds.height/2), this, rootTree); // bottom-right
		int i = 0;
		while (i < entries.size()){
			int position = getEntryPosition(entries.get(i));
			if (position != -1)
				subTrees[position].insert(entries.remove(i));
			else
				i++;
		}
		//if (debug) System.out.println("split!");
		rootTree.lock.unlock();
	}

	// combine
	public void attemptCombine(){
		rootTree.lock.lock();
		if (subTrees != null && numContains <= idealObjects/2){
			//System.out.println("combining "+x+" "+y+" "+width+" "+height);
			ArrayList<Entity> newEntries = new ArrayList<Entity>();
			getEntries(newEntries);
			entries = newEntries;
			for (Entity e: entries)
				e.parentTree = this;
			subTrees = null;
			//if (debug) System.out.println("Combined!");
		}
		rootTree.lock.unlock();
	}

	public void add(Entity entry){
		insert(entry);
	}
	
	// insert
	public void insert(Entity entry){
		rootTree.lock.lock();
		int position = getEntryPosition(entry);
		if (position != -1)
			subTrees[position].insert(entry);
		else{
			entries.add(entry);
			entry.parentTree = this;
			if (subTrees == null && entries.size() >= idealObjects*1.5 && bounds.width >= minSize*2 && bounds.height >= minSize*2)
				split();
			//if (rootTree == GameServer.trees[0])
			//	System.out.println("Inserting entry " + entries.size() + " " + entry);
		}
		//if (debug) System.out.println("Inserted, position " + position);
		numContains ++;
		rootTree.lock.unlock();
	}

	// remove
	public void remove(Entity entry){
		rootTree.lock.lock();
		if (entry == null){
			System.out.println("Unable to remove null entry");
			return;
		}
		QuadTree parent = entry.parentTree;
		if (parent == null){
			System.out.println("Entity has no parent tree");
			return;
		}
		parent.entries.remove(entry);

		//if (rootTree == GameServer.trees[0])
		//	System.out.println("Removed entry " + entries.size() + " " + entry);
		
		//if (debug) System.out.println("Removed");
		while (parent != null){
			parent.numContains--;
			parent = parent.parent;
		}
		if (entry.parentTree.parent != null)
			entry.parentTree.parent.attemptCombine();
		rootTree.lock.unlock();
	}

	// refresh
	public void refresh(Entity entry){
		rootTree.lock.lock();
		QuadTree parent = entry.parentTree;
		if (parent == null) return;
		// Inside subtree
		int pos = parent.getEntryPosition(entry);
		/*if (){
			parent.entries.remove(entry);
			parent.subTrees[pos].insert(entry);
		// outside bounds
		}else*/ if ((parent.subTrees != null && pos != -1) || !parent.bounds.contains(entry.getBoundsRect())){
			//System.out.println("Entry: " + entry.id + " moved out of it's square! " + getEntryPosition(entry));
			parent.remove(entry);
			rootTree.insert(entry);
			
		}
		//System.out.println("Refreshing");
		rootTree.lock.unlock();
	}

	// Get
	// used
	/*
	public ArrayList<Entity> getEntriesInRegion(){
		return getEntriesInRegion(new ArrayList<Entity>(), bounds);
	}
	public ArrayList<Entity> getEntriesInRegion(Rectangle area){
		return getEntriesInRegion(new ArrayList<Entity>(), area);
	}
	public ArrayList<Entity> getEntriesInRegion(ArrayList<Entity> toReturn, Rectangle area){
		rootTree.lock.lock();
		toReturn.addAll(entries);
		if (subTrees != null){
			int position = getRectPosition(area);
			if (position == -1)
				for (QuadTree q:subTrees)
					q.getEntriesInRegion(toReturn, area);
			else{
				subTrees[position].getEntriesInRegion(toReturn, area);
			}
		}
		rootTree.lock.unlock();
		return toReturn;
	}
	*/
	/*
	public synchronized LinkedList<Entity> getEntriesInRegionLinked(LinkedList<Entity> toReturn, Rectangle area){
		toReturn.addAll(entries);
		if (subTrees != null){
			int position = getRectPosition(area);
			if (position == -1)
				for (QuadTree q:subTrees)
					q.getEntriesInRegionLinked(toReturn, area);
			else{
				subTrees[position].getEntriesInRegionLinked(toReturn, area);
			}
		}
		return toReturn;
	}
	public synchronized ConcurrentHashMap<Integer, Entity> getEntitiesInRegion(ConcurrentHashMap<Integer, Entity> toReturn, Rectangle area){
		try {
			//if (debug) System.out.println("Getting in region: " + this.x + " " + this.y+ " "+(this.x+this.width)+" "+(this.y+this.height) + " Entries: " + entries.size());
			for (Entity q : entries){
				if (q != null)
					toReturn.put(q.id, q);
			}
			if (subTrees != null){
				int position = getRectPosition(area);
				if (position == -1)
					for (QuadTree q:subTrees){
						//if (debug) System.out.println("Looking in sub tree: " + q.x + " " + q.y);
						if (q != null)
							q.getEntitiesInRegion(toReturn, area);
					}
				else{
					subTrees[position].getEntitiesInRegion(toReturn, area);
				}
			}//else
				//if (debug) System.out.println("No sub trees");
			return toReturn;
		} catch (ConcurrentModificationException e){
			return toReturn;
		}
	}
	*/
	// used
	public HashMap<Integer, Entity> getEntitiesInRegion(HashMap<Integer, Entity> toReturn, Entity e, float radius) {
		return getEntitiesInRegion(toReturn, new CircleDef(e.pos.x, e.pos.y, radius), false);
	}
	public HashMap<Integer, Entity> getEntitiesInRegion(HashMap<Integer, Entity> toReturn, float x, float y, float radius) {
		return getEntitiesInRegion(toReturn, new CircleDef(x, y, radius), false);
	}
	public HashMap<Integer, Entity> getEntitiesInRegion(HashMap<Integer, Entity> toReturn, CircleDef c, boolean debug) {
		rootTree.lock.lock();
		try {
			
			if (debug) System.out.println("Getting in region: " + bounds.x + " " + bounds.y+ " "+(bounds.x+bounds.width)+" "+(bounds.y+bounds.height) + " Entries: " + entries.size());
			for (Entity q : entries){
				if (q != null && q.getBounds().partialContain(c))
					toReturn.put(q.id, q);
			}
			
			if (subTrees != null){
				int position = getRectPosition(c.getBounds());
				if (position == -1)
					for (QuadTree q:subTrees){
						if (debug) System.out.println("Looking in sub tree: " + q.bounds.x + " " + q.bounds.y);
						if (q != null)
							q.getEntitiesInRegion(toReturn, c, debug);
					}
				else{
					subTrees[position].getEntitiesInRegion(toReturn, c, debug);
				}
			}else
				if (debug) System.out.println("No sub trees");
			rootTree.lock.unlock();
			return toReturn;
		} catch (ConcurrentModificationException e){
			rootTree.lock.unlock();
			return toReturn;
		}
	}
	
	protected void getEntries(ArrayList<Entity> toReturn){
		toReturn.addAll(entries);
		if (subTrees != null)
			for (QuadTree q:subTrees)
				q.getEntries(toReturn);
	}
	
	public synchronized void printRundown(){
		System.out.println("====================================================================================================");
		printRundown(0);
	}
	private void printRundown(int depth){
		System.out.println(depth + "\t" + bounds.x + "\t" + bounds.y + "\t" + bounds.width+"\t" + bounds.height+"\tEntries: " + entries.size());
		if (subTrees != null){
			for (QuadTree qt : subTrees)
				qt.printRundown(depth+1);
		}
		if (depth == 0)
			System.out.println("Total: " + numContains);
		
	}
	
}
