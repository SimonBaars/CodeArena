package com.simonbaars.clonerefactor.thread;

import java.io.File;
import java.nio.file.Paths;
import java.util.concurrent.TimeoutException;

import com.simonbaars.clonerefactor.Main;
import com.simonbaars.clonerefactor.model.DetectionResults;

public class CorpusThread extends Thread {
	private final File file;
	public DetectionResults res;
	public final long creationTime;
	public Exception error;
	
	public CorpusThread(File file) {
		this.file=file;
		this.creationTime = System.currentTimeMillis();
		start();
	}
	
	public void run() {
		try {
			res = Main.cloneDetection(file.toPath(), Paths.get(file.getAbsolutePath()+"/src/main/java"));
			res.sorted();
		} catch(Exception e) {
			error = e;
		}
	}

	@SuppressWarnings("deprecation")
	public void timeout() {
		stop();
		error = new TimeoutException("Thread has exceeded its timeout!");
	}
	
	public File getFile() {
		return file;
	}
}
