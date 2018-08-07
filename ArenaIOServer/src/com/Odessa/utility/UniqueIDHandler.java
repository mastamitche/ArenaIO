// Odessa 2016

package com.Odessa.utility;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

// Stores data from an id, and can retrieve it. Essentially a hashmap for data which generates the key
public class UniqueIDHandler<T> {

	private Map<T, Integer> fromData = Collections.synchronizedMap(new HashMap<T, Integer>());
	private Map<Integer, T> fromId = Collections.synchronizedMap(new HashMap<Integer, T>());

	int currentlyUpToID = 0;
	
	public UniqueIDHandler(int capacity){
	}
	
	public synchronized int AddEntry(T data){
		while (get(currentlyUpToID) != null)
			currentlyUpToID++;
		int nextID = currentlyUpToID++;
		synchronized(fromData){
			fromData.put(data, nextID);
		}
		synchronized(fromId){
			fromId.put(nextID, data);
		}
		return nextID;
	}
	
	public Iterator<java.util.Map.Entry<Integer, T>> GetIterator(){
		return fromId.entrySet().iterator();
	}

	public synchronized void RemoveEntry(int id){
		int oldSize = fromId.size();
		T data;
		synchronized(fromId){
			data = fromId.remove(id);
		}
		//if (data == null) return;
		synchronized(fromData){
			fromData.remove(data);
		}
		
		if (fromId.size() != oldSize-1) System.out.println("Didn't go down!");
	}
	public synchronized void RemoveEntry(T data){
		if (data == null) return;
		int ID;
		synchronized(fromData){
			ID = fromData.remove(data);
		}
		synchronized(fromId){
			data = fromId.remove(ID);
		}
	}

	public T get(int id){
		return fromId.get(id);
	}

	public int GetID(T data){
		if (data == null) return -1;
		return fromData.get(data);
	}
}
