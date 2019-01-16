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
		return combinedList().containsAll(c);
	}

	private List<E> combinedList() {
		return flatList().collect(Collectors.toList());
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
		int total = 0;
		for(int i = 0; i<listSize.length; i++) {
			int size = listSize[i];
			total+=size;
			if(index<total) {
				return listToFlatten.get(i).get(index-(total-size));
			}
		}
		throw new IndexOutOfBoundsException();
	}

	@Override
	public E set(int index, E element) {
		int total = 0;
		for(int i = 0; i<listSize.length; i++) {
			int size = listSize[i];
			total+=size;
			if(index<total) {
				return listToFlatten.get(i).set(index-(total-size), element);
			}
		}
		throw new IndexOutOfBoundsException();
	}

	@Override
	public void add(int index, E element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public E remove(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int indexOf(Object o) {
		return combinedList().indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return combinedList().lastIndexOf(o);
	}

	@Override
	public ListIterator<E> listIterator() {
		return combinedList().listIterator();
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		return combinedList().listIterator(index);
	}

	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		return combinedList().subList(fromIndex, toIndex);
	}

}
