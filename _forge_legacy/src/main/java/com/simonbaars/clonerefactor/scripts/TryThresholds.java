package com.simonbaars.clonerefactor.scripts;

import com.simonbaars.clonerefactor.settings.Settings;

public class TryThresholds {
	public static void main(String[] args) {
		for(int i = 1; i<40; i++) {
			Settings.get().setMinAmountOfTokens(i);
			RunOnCorpus.main(args);
		}
	}
}
