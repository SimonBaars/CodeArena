package com.simonbaars.clonerefactor.detection.type2;

import com.simonbaars.clonerefactor.detection.interfaces.CalculatesPercentages;

public class WeightedPercentage implements CalculatesPercentages {
	private final double percentage;
	private final int weight;
	
	public WeightedPercentage(double percentage, int weight) {
		super();
		this.percentage = percentage;
		this.weight = weight;
	}

	public double getPercentage() {
		return percentage;
	}

	public int getWeight() {
		return weight;
	}

	public WeightedPercentage mergeWith(WeightedPercentage weighedPercentage) {
		int total = weight+weighedPercentage.getWeight();
		double combinedPercentage = unweighted(total) + weighedPercentage.unweighted(total);
		return new WeightedPercentage(combinedPercentage, total);
	}

	private double unweighted(int total) {
		return total == 0 ? 0D : percentage/total*weight;
	}

	@Override
	public String toString() {
		return "WeightedPercentage [percentage=" + percentage + ", weight=" + weight + "]";
	}
}
