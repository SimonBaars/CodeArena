package com.simonbaars.clonerefactor.util;

import java.util.function.BooleanSupplier;

public class Wait {
	private final int timeout;
	private final int interval;
	
	public Wait(int timeout) {
		this.timeout=timeout;
		this.interval=400;
	}
	
	public Wait(int timeout, int interval) {
		this.timeout=timeout;
		this.interval=interval;
	}

	public boolean until(BooleanSupplier t) {
		long endTime = System.currentTimeMillis()+(timeout*1000);
		while(System.currentTimeMillis()<endTime) {
			if(t.getAsBoolean()) return true;
			FileUtils.sleep(interval);
		}
		return false;
	}
}
