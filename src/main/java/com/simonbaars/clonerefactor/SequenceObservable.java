package com.simonbaars.clonerefactor;

import java.util.ArrayList;
import java.util.List;

import com.simonbaars.clonerefactor.metrics.ProblemType;
import com.simonbaars.clonerefactor.model.Sequence;

public class SequenceObservable {
	private final List<SequenceObserver> observers = new ArrayList<>();
	private static final SequenceObservable singleton = new SequenceObservable();
	
	public static SequenceObservable get() {
		return singleton;
	}
	
	public void sendUpdate(ProblemType problem, Sequence sequence) {
		observers.forEach(e -> e.update(problem, sequence));
	}
	
	public boolean isActive() {
		return !observers.isEmpty();
	}
}
