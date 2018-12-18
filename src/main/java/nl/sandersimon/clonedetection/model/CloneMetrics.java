package nl.sandersimon.clonedetection.model;

import java.util.Arrays;

import net.minecraft.scoreboard.Score;
import nl.sandersimon.clonedetection.CloneDetection;

public class CloneMetrics {
	private final CloneScore[] scores;
	
	private final CloneScore totalAmountOfClonedLinesInProject;
	private final CloneScore totalAmountOfLinesInProject;
	private final CloneScore percentageOfProjectCloned;
	private final CloneScore totalNumberOfClones;
	private final CloneScore totalNumberOfCloneClasses;
	private final CloneScore mostLinesCloneClass;
	private final CloneScore mostOccurrentClone;
	private final CloneScore biggestCloneClass;
	private final CloneScore totalCloneVolume;

	public CloneMetrics() {
		this.totalAmountOfClonedLinesInProject = new CloneScore("Amount of cloned lines");
		this.totalAmountOfLinesInProject = new CloneScore("Amount of lines in project");
		this.percentageOfProjectCloned = new CloneScore("Percentage of project cloned");
		this.totalNumberOfClones = new CloneScore("Amount of clones");
		this.totalNumberOfCloneClasses = new CloneScore("Number of clone classes");
		this.mostLinesCloneClass = new CloneScore("Biggest clone class (in lines)");
		this.mostOccurrentClone = new CloneScore("Most occurring clone class");
		this.biggestCloneClass = new CloneScore("Biggest clone class (in volume)");
		this.totalCloneVolume = new CloneScore("Total clone volume");
		scores = new CloneScore[]{this.totalAmountOfClonedLinesInProject, this.totalAmountOfLinesInProject, this.percentageOfProjectCloned, this.totalNumberOfClones,
				this.totalNumberOfCloneClasses, this.mostLinesCloneClass, this.mostOccurrentClone, this.biggestCloneClass, this.totalCloneVolume};
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

	public void setTotalAmountOfClonedLinesInProject(Score totalAmountOfClonedLinesInProject) {
		this.totalAmountOfClonedLinesInProject.setScore(totalAmountOfClonedLinesInProject);
	}

	public void setTotalAmountOfLinesInProject(Score totalAmountOfLinesInProject) {
		this.totalAmountOfClonedLinesInProject.setScore(totalAmountOfLinesInProject);
	}

	public void setPercentageOfProjectCloned(Score percentageOfProjectCloned) {
		this.totalAmountOfClonedLinesInProject.setScore(percentageOfProjectCloned);
	}

	public void setTotalNumberOfClones(Score totalNumberOfClones) {
		this.totalAmountOfClonedLinesInProject.setScore(totalNumberOfClones);
	}

	public void setTotalNumberOfCloneClasses(Score totalNumberOfCloneClasses) {
		this.totalAmountOfClonedLinesInProject.setScore(totalNumberOfCloneClasses);
	}

	public void setMostLinesCloneClass(Score mostLinesCloneClass) {
		this.totalAmountOfClonedLinesInProject.setScore(mostLinesCloneClass);
	}

	public void setMostOccurrentClone(Score mostOccurrentClone) {
		this.totalAmountOfClonedLinesInProject.setScore(mostOccurrentClone);
	}

	public void setBiggestCloneClass(Score biggestCloneClass) {
		this.totalAmountOfClonedLinesInProject.setScore(biggestCloneClass);
	}

	public void setTotalCloneVolume(Score totalCloneVolume) {
		this.totalAmountOfClonedLinesInProject.setScore(totalCloneVolume);
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

	public CloneScore[] getScores() {
		return scores;
	}
	
	public CloneScore getScoreByName(String name) {
		return Arrays.stream(scores).filter(s -> s.getName().equals(name)).findAny().get();
	}
}
