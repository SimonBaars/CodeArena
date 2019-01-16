package nl.sandersimon.clonedetection.ast;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class FlattenedList<E> implements List<E> {
	
	private final List<List<E>> listToFlatten;
	private final int[] listSize;
	
	
	public FlattenedList(List<List<E>> listToFlatten) {
		super();
		this.listToFlatten = listToFlatten;
		this.listSize = new int[listToFlatten.size()];
		for(int i = 0; i<listSize.length; i++) {
			listSize[i] = listToFlatten.get(i).size();
		}
	}

	@Override
	public int size() {
		return IntStream.of(listSize).sum();
	}

	@Override
	public boolean isEmpty() {
		return listToFlatten.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return listToFlatten.stream().anyMatch(e -> e.contains(o));
	}

	@Override
	public Iterator<E> iterator() {
		return flatList().iterator();
	}

	private Stream<E> flatList() {
		return listToFlatten.stream().flatMap(List::stream);
	}

	@Override
	public Object[] toArray() {
		return flatList().toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return (T[])toArray();
	}

	@Override
	public boolean add(E e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return flatList().collect(Collectors.toList()).containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public E get(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public E set(int index, E element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void add(int index, E element) {
		// TODO Auto-generated method stub

	}

	@Override
	public E remove(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int indexOf(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int lastIndexOf(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ListIterator<E> listIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		// TODO Auto-generated method stub
		return null;
	}

}
