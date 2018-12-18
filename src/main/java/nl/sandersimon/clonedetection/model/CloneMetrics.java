package nl.sandersimon.clonedetection.model;

public class CloneMetrics {
	private int totalAmountOfClonedLinesInProject = 0;
	private int totalAmountOfLinesInProject = 0;
	private int percentageOfProjectCloned = 0;
	private int totalNumberOfClones = 0;
	private int totalNumberOfCloneClasses = 0;
	private int mostLinesCloneClass = 0;
	private int mostOccurrentClone = 0;
	private int biggestCloneClass = 0;
	private int totalCloneVolume = 0;

	public CloneMetrics() {
	}

	public CloneMetrics(int totalAmountOfClonedLinesInProject, int totalAmountOfLinesInProject,
			int percentageOfProjectCloned, int totalNumberOfClones, int totalNumberOfCloneClasses,
			int mostLinesCloneClass, int mostOccurrentClone, int biggestCloneClass, int totalCloneVolume) {
		super();
		this.totalAmountOfClonedLinesInProject = totalAmountOfClonedLinesInProject;
		this.totalAmountOfLinesInProject = totalAmountOfLinesInProject;
		this.percentageOfProjectCloned = percentageOfProjectCloned;
		this.totalNumberOfClones = totalNumberOfClones;
		this.totalNumberOfCloneClasses = totalNumberOfCloneClasses;
		this.mostLinesCloneClass = mostLinesCloneClass;
		this.mostOccurrentClone = mostOccurrentClone;
		this.biggestCloneClass = biggestCloneClass;
		this.totalCloneVolume = totalCloneVolume;
	}

	public int getTotalAmountOfClonedLinesInProject() {
		return totalAmountOfClonedLinesInProject;
	}

	public void setTotalAmountOfClonedLinesInProject(int totalAmountOfClonedLinesInProject) {
		this.totalAmountOfClonedLinesInProject = totalAmountOfClonedLinesInProject;
	}

	public int getTotalAmountOfLinesInProject() {
		return totalAmountOfLinesInProject;
	}

	public void setTotalAmountOfLinesInProject(int totalAmountOfLinesInProject) {
		this.totalAmountOfLinesInProject = totalAmountOfLinesInProject;
	}

	public int getPercentageOfProjectCloned() {
		return percentageOfProjectCloned;
	}

	public void setPercentageOfProjectCloned(int percentageOfProjectCloned) {
		this.percentageOfProjectCloned = percentageOfProjectCloned;
	}

	public int getTotalNumberOfClones() {
		return totalNumberOfClones;
	}

	public void setTotalNumberOfClones(int totalNumberOfClones) {
		this.totalNumberOfClones = totalNumberOfClones;
	}

	public int getTotalNumberOfCloneClasses() {
		return totalNumberOfCloneClasses;
	}

	public void setTotalNumberOfCloneClasses(int totalNumberOfCloneClasses) {
		this.totalNumberOfCloneClasses = totalNumberOfCloneClasses;
	}

	public int getMostLinesCloneClass() {
		return mostLinesCloneClass;
	}

	public void setMostLinesCloneClass(int mostLinesCloneClass) {
		this.mostLinesCloneClass = mostLinesCloneClass;
	}

	public int getMostOccurrentClone() {
		return mostOccurrentClone;
	}

	public void setMostOccurrentClone(int mostOccurrentClone) {
		this.mostOccurrentClone = mostOccurrentClone;
	}

	public int getBiggestCloneClass() {
		return biggestCloneClass;
	}

	public void setBiggestCloneClass(int biggestCloneClass) {
		this.biggestCloneClass = biggestCloneClass;
	}

	public int getTotalCloneVolume() {
		return totalCloneVolume;
	}

	public void setTotalCloneVolume(int totalCloneVolume) {
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

}
