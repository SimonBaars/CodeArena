package com.simonbaars.clonerefactor.datatype;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SetMap<K, V> extends HashMap<K, Set<V>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SetMap() {
	}

	public SetMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	public SetMap(int initialCapacity) {
		super(initialCapacity);
	}

	public SetMap(Map<? extends K, ? extends Set<V>> m) {
		super(m);
	}
	
	public Set<V> addTo(K key, V value) {
		Set<V> l;
		if(super.containsKey(key)) {
			l = super.get(key); 
		} else l = new HashSet<>();
		l.add(value);
        return super.put(key, l);
    }
	
	@SuppressWarnings("unchecked")
	@Override 
	public Set<V> get(Object key){
		if(!super.containsKey(key))
			super.put((K) key, new HashSet<>());
		return super.get(key);
	}
}
