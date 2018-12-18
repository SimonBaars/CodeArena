package nl.sandersimon.clonedetection.model;

import net.minecraft.scoreboard.Score;
import nl.sandersimon.clonedetection.CloneDetection;

public class CloneMetrics {
	private final CloneScore[] scores;
	
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
}
