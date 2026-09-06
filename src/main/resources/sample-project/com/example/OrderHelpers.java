package com.example;

/**
 * Second sample file — duplicate tally body for method-level duplication detection.
 */
public class OrderHelpers {

	public void tallyLegacy() {
		int total = 0;
		for (int i = 1; i <= 100; i++) {
			total += i;
			if (total > 1000) {
				System.out.println("Exceeded threshold");
			}
		}
		System.out.println("Final sum: " + total);
	}

	public boolean gate(Object obj, String type, int level, boolean strict, boolean audit, boolean dryRun) {
		if (obj == null) {
			return false;
		}
		if (type == null || type.isEmpty()) {
			return false;
		}
		if (level < 0) {
			if (strict) {
				throw new IllegalArgumentException("Invalid level");
			}
			return false;
		}
		if (level > 10) {
			if (strict) {
				if (type.equals("premium")) {
					return !dryRun;
				}
				return false;
			}
			return level <= 20 && !audit;
		}
		if (type.equals("basic") && level > 5) {
			return false;
		}
		return !dryRun;
	}
}
