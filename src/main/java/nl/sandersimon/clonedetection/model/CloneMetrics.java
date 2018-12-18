package nl.sandersimon.clonedetection.model;

import net.minecraft.scoreboard.Score;
import nl.sandersimon.clonedetection.CloneDetection;

public class CloneMetrics {
	private CloneScore totalAmountOfClonedLinesInProject;
	private CloneScore totalAmountOfLinesInProject;
	private CloneScore percentageOfProjectCloned;
	private CloneScore totalNumberOfClones;
	private CloneScore totalNumberOfCloneClasses;
	private CloneScore mostLinesCloneClass;
	private CloneScore mostOccurrentClone;
	private CloneScore biggestCloneClass;
	private CloneScore totalCloneVolume;

	public CloneMetrics() {
		this.totalAmountOfClonedLinesInProject = new CloneScore();
		this.totalAmountOfLinesInProject = new CloneScore();
		this.percentageOfProjectCloned = new CloneScore();
		this.totalNumberOfClones = new CloneScore();
		this.totalNumberOfCloneClasses = new CloneScore();
		this.mostLinesCloneClass = new CloneScore();
		this.mostOccurrentClone = new CloneScore();
		this.biggestCloneClass = new CloneScore();
		this.totalCloneVolume = new CloneScore();
	}
	
	public CloneMetrics(Score totalAmountOfClonedLinesInProject, Score totalAmountOfLinesInProject, Score percentageOfProjectCloned, Score totalNumberOfClones, Score totalNumberOfCloneClasses, Score mostLinesCloneClass, Score mostOccurrentClone, Score biggestCloneClass, Score totalCloneVolume) {
		this.totalAmountOfClonedLinesInProject = new CloneScore(totalAmountOfClonedLinesInProject);
		this.totalAmountOfLinesInProject = new CloneScore(totalAmountOfLinesInProject);
		this.percentageOfProjectCloned = new CloneScore(percentageOfProjectCloned);
		this.totalNumberOfClones = new CloneScore(totalNumberOfClones);
		this.totalNumberOfCloneClasses = new CloneScore(totalNumberOfCloneClasses);
		this.mostLinesCloneClass = new CloneScore(mostLinesCloneClass);
		this.mostOccurrentClone = new CloneScore(mostOccurrentClone);
		this.biggestCloneClass = new CloneScore(biggestCloneClass);
		this.totalCloneVolume = new CloneScore(totalCloneVolume);
	}

	public CloneScore getTotalAmountOfClonedLinesInProject() {
		return totalAmountOfClonedLinesInProject;
	}

	public CloneScore getTotalAmountOfLinesInProject() {
		return totalAmountOfLinesInProject;
	}

	public CloneScore getPercentageOfProjectCloned() {
		return percentageOfProjectCloned;
	}

	public CloneScore getTotalNumberOfClones() {
		return totalNumberOfClones;
	}

	public CloneScore getTotalNumberOfCloneClasses() {
		return totalNumberOfCloneClasses;
	}

	public CloneScore getMostLinesCloneClass() {
		return mostLinesCloneClass;
	}

	public CloneScore getMostOccurrentClone() {
		return mostOccurrentClone;
	}

	public CloneScore getBiggestCloneClass() {
		return biggestCloneClass;
	}

	public CloneScore getTotalCloneVolume() {
		return totalCloneVolume;
	}

	public void setTotalAmountOfClonedLinesInProject(CloneScore totalAmountOfClonedLinesInProject) {
		this.totalAmountOfClonedLinesInProject = totalAmountOfClonedLinesInProject;
	}

	public void setTotalAmountOfLinesInProject(CloneScore totalAmountOfLinesInProject) {
		this.totalAmountOfLinesInProject = totalAmountOfLinesInProject;
	}

	public void setPercentageOfProjectCloned(CloneScore percentageOfProjectCloned) {
		this.percentageOfProjectCloned = percentageOfProjectCloned;
	}

	public void setTotalNumberOfClones(CloneScore totalNumberOfClones) {
		this.totalNumberOfClones = totalNumberOfClones;
	}

	public void setTotalNumberOfCloneClasses(CloneScore totalNumberOfCloneClasses) {
		this.totalNumberOfCloneClasses = totalNumberOfCloneClasses;
	}

	public void setMostLinesCloneClass(CloneScore mostLinesCloneClass) {
		this.mostLinesCloneClass = mostLinesCloneClass;
	}

	public void setMostOccurrentClone(CloneScore mostOccurrentClone) {
		this.mostOccurrentClone = mostOccurrentClone;
	}

	public void setBiggestCloneClass(CloneScore biggestCloneClass) {
		this.biggestCloneClass = biggestCloneClass;
	}

	public void setTotalCloneVolume(CloneScore totalCloneVolume) {
		this.totalCloneVolume = totalCloneVolume;
	}

	@Override
	public String toString() {
		return "CloneMetrics [totalAmountOfClonedLinesInProject=" + totalAmountOfClonedLinesInProject
				+ ", totalAmountOfLinesInProject=" + totalAmountOfLinesInProject + ", percentageOfProjectCloned="
				+ percentageOfProjectCloned + ", totalNumberOfClones=" + totalNumberOfClones
				+ ", totalNumberOfCloneClasses=" + totalNumberOfCloneClasses + ", mostLinesCloneClass="
				+ mostLinesCloneClass + ", mostOccurrentClone=" + mostOccurrentClone + ", biggestCloneClass="
				+ biggestCloneClass + ", totalCloneVolume=" + totalCloneVolume + "]";
	}

	public void calculateClonePercentage() {
		getPercentageOfProjectCloned().setScorePoints(CloneDetection.get().perc(getTotalAmountOfLinesInProject().getScorePoints(), getTotalAmountOfClonedLinesInProject().getScorePoints()));
	}
}
