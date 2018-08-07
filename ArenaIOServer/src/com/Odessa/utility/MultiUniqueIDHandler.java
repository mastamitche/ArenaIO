package com.Odessa.utility;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// Stores data and provides it an id, using the id/data you can get the other. Will also store the same data
// under the same key. Good for storing strings as ints, when many strings are the same
public class MultiUniqueIDHandler<T> {

	private Map<T, Entry> fromData = Collections.synchronizedMap(new ConcurrentHashMap<T, Entry>());
	private Map<Integer, Entry> fromId = Collections.synchronizedMap(new ConcurrentHashMap<Integer, Entry>());
	
	int currentlyUpToID = 0;
	
	public MultiUniqueIDHandler(){
	}
	
	public int AddEntry(T data){
		Entry e = fromData.get(data);
		if (e != null){
			e.count++;
			return e.id;
		}else{
			while (GetEntry(currentlyUpToID) != null)
				currentlyUpToID++;
			
			int nextID = currentlyUpToID++;
			Entry e2 = new Entry(data, nextID);
			fromData.put(data, e2);
			fromId.put(nextID, e2);
			return nextID;
		}
	}

	public int RemoveEntry(int id){
		Entry e = fromId.get(id);
		if (e != null){
			e.count--;
			if (e.count <= 0){
				fromData.remove(e.data);
				fromId.remove(e.id);
				return e.id;
			}
		}
		return -1;
	}

	public T GetEntry(int id){
		Entry e = fromId.get(id);
		if (e != null)
			return e.data;
		return null;
	}

	public void SetEntry(int id, T data){
		Entry e = fromId.get(id);
		if (e != null)
			e.data = data;
	}

	public int GetID(T data){
		Entry e = fromData.get(data);
		if (e != null)
			return e.id;
		return -1;
	}
	
	private class Entry{
		int count = 1;
		int id;
		T data;
		public Entry(T data, int id){
			this.data = data;
			this.id = id;
		}
	}
}
