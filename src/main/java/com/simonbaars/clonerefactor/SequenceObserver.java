package com.simonbaars.clonerefactor;

import com.simonbaars.clonerefactor.metrics.ProblemType;
import com.simonbaars.clonerefactor.model.Sequence;

public interface SequenceObserver {
	public void update(ProblemType problem, Sequence sequence);
}
