package com.simonbaars.clonerefactor.datatype;

import java.util.HashMap;

public class DoubleMap<K1,K2,V> extends HashMap<K1, HashMap<K2,V>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DoubleMap() {
	}

	public DoubleMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	public DoubleMap(int initialCapacity) {
		super(initialCapacity);
	}
	
	public V get(K1 key1, K2 key2){
		return super.get(key1).get(key2);
	}
	
	public boolean containsKey(K1 key1, K2 key2){
		if(!super.containsKey(key1))
			super.put(key1, new HashMap<>());
		return super.get(key1).containsKey(key2);
	}
}
