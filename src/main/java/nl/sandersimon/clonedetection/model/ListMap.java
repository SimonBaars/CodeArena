package nl.sandersimon.clonedetection.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListMap<K, V> extends HashMap<K, List<V>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ListMap() {
	}

	public ListMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	public ListMap(int initialCapacity) {
		super(initialCapacity);
	}

	public ListMap(Map<? extends K, ? extends List<V>> m) {
		super(m);
	}
	
	public List<V> addTo(K key, V value) {
		List<V> l;
		if(super.containsKey(key)) {
			l = super.get(key); 
		} else l = new ArrayList<>();
		l.add(value);
        return super.put(key, l);
    }
	
	
}
