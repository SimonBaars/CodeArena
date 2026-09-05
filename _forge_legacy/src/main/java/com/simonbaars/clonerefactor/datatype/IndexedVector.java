package com.simonbaars.clonerefactor.datatype;

import java.util.Vector;

public class IndexedVector<E> extends Vector<E> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9150447915872304886L;
	
	public synchronized int getIndex(E element) {
		int size = size();
		add(element);
		if(indexOf(element)!=size)
			throw new IllegalStateException("Synchronization error!");
		return size;
	}
	
}