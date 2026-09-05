package com.simonbaars.clonerefactor.thread;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.simonbaars.clonerefactor.metrics.Metrics;
import com.simonbaars.clonerefactor.model.DetectionResults;
import com.simonbaars.clonerefactor.util.FileUtils;
import com.simonbaars.clonerefactor.util.SavePaths;

public class ThreadPool implements WritesErrors {
	private final File OUTPUT_FOLDER = new File(SavePaths.getFullOutputFolder());
	private final File FULL_METRICS = new File(OUTPUT_FOLDER.getParent()+"/metrics.txt");
	private final int NUMBER_OF_THREADS = 4;
	private final int THREAD_TIMEOUT = 600000;
	private final Metrics fullMetrics = new Metrics();
	
	private final CorpusThread[] threads;
	
	public ThreadPool () {
		threads = new CorpusThread[NUMBER_OF_THREADS];
		OUTPUT_FOLDER.mkdirs();
	}

	public void waitForThreadToFinish() {
		if(allNull())
			return;
		while(Arrays.stream(threads).filter(e -> e!=null).noneMatch(e -> !e.isAlive())) {
			try {
				Thread.sleep(100);
				nullifyThreadIfStarved();
			} catch (InterruptedException e1) {
				Thread.currentThread().interrupt();
			}
		}
	}

	private void nullifyThreadIfStarved() {
		IntStream.range(0,size()).filter(i -> threads[i]!=null && threads[i].creationTime+THREAD_TIMEOUT<System.currentTimeMillis()).forEach(i -> {
			threads[i].timeout();
		});
	}

	private int size() {
		return NUMBER_OF_THREADS;
	}

	public void addToAvailableThread(File file) {
		for(int i = 0; i<threads.length; i++) {
			if(threads[i]==null || !threads[i].isAlive()) {
				writePreviousThreadResults(i);
				threads[i] = new CorpusThread(file);
				break;
			}
		}
	}
	
	public void finishFinalThreads() {
		while(Arrays.stream(threads).anyMatch(e -> e!=null)) {
			waitForThreadToFinish();
			for(int i = 0; i<threads.length; i++) {
				if(threads[i] != null && !threads[i].isAlive()) {
					writePreviousThreadResults(i);
					threads[i] = null;
				}
			}
		}
	}

	private void writePreviousThreadResults(int i) {
		if(threads[i]!=null && !threads[i].isAlive()) {
			if(threads[i].res != null)
				writeResults(threads[i].getFile(), threads[i].res);
			else writeError(i);
			if(freeMemoryPercentage()<15) JavaParserFacade.clearInstances();
			threads[i]=null;
		}
	}

	private void writeError(int i) {
		writeError(SavePaths.createDirectoryIfNotExists(SavePaths.getErrorFolder())+threads[i].getFile().getName(), threads[i].error);
	}

	private void writeResults(File file, DetectionResults res) {
		fullMetrics.add(res.getMetrics());
		try {
			FileUtils.writeStringToFile(new File(OUTPUT_FOLDER.getAbsolutePath()+"/"+file.getName()+"-"+res.getClones().size()+".txt"), res.toString());
			FileUtils.writeStringToFile(FULL_METRICS, fullMetrics.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public double freeMemoryPercentage() {
		return (double)Runtime.getRuntime().freeMemory() / (double)Runtime.getRuntime().totalMemory() * 100D;
	}

	public String showContents() {
		return Arrays.stream(threads).filter(e -> e!=null).map(e -> e.getFile().getName()).collect(Collectors.joining(", "));
	}
	
	public boolean anyNull() {
		return Arrays.stream(threads).anyMatch(e -> e==null);
	}
	
	public boolean allNull() {
		return Arrays.stream(threads).allMatch(e -> e==null);
	}
}
