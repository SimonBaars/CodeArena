package nl.sandersimon.clonedetection.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import nl.sandersimon.clonedetection.editor.CodeEditor;
import nl.sandersimon.clonedetection.editor.CodeEditorMaker;

public class CloneClass implements Comparable<CloneClass>{

	private int lines;
	private List<Location> locations = new ArrayList<>();
	
	public CloneClass(CloneMetrics metrics) {
		super();
		metrics.getTotalNumberOfCloneClasses().incrementScore();
	}
	
	public CloneClass() {
		super();
	}

	//public CloneClass(int lines, List<Location> locations) {
	//	this();
	//	this.lines = lines;
	//	this.locations = locations;
	//}

	public int getLines() {
		return lines;
	}

	public void setLines(int lines) {
		this.lines = lines;
	}

	public List<Location> getLocations() {
		return locations;
	}

	public void setLocations(List<Location> locations) {
		this.locations = locations;
	}

	@Override
	public int compareTo(CloneClass o) {
		return Integer.compare(volume(), o.volume());
	}

	public void add(CloneMetrics metrics, Location construct) {
		locations.add(construct);
		metrics.getTotalNumberOfClones().incrementScore();
		metrics.getTotalCloneVolume().increaseScore(lines);
		CloneScore mostLines = metrics.getMostLinesCloneClass();
		if(lines > mostLines.getScorePoints())
			mostLines.setScorePoints(lines);
		CloneScore mostOccurrent = metrics.getMostOccurrentClone();
		if(size() > mostOccurrent.getScorePoints())
			mostOccurrent.setScorePoints(size());
		CloneScore highestVolume = metrics.getBiggestCloneClass();
		if(volume() > highestVolume.getScorePoints())
			highestVolume.setScorePoints(volume());
	}
	
	public int size() {
		return locations.size();
	}

	public Location get(int j) {
		return locations.get(j);
	}

	@Override
	public String toString() {
		return "CloneClass"+System.lineSeparator()+"lines=" + lines + System.lineSeparator()+"volume=" + System.lineSeparator()+"volume=" + volume() + System.lineSeparator()+"locations=" + Arrays.toString(locations.toArray());
	}
	
	public int volume() {
		return lines * size();
	}

	public void open() {
		CodeEditorMaker.create(this);
	}

	public String getName() {
		if(size() == 0)
			return "error";
		return get(0).getName();
	}

	public String rascalLocList() {
		return "["+locations.stream().map(Location::rascalFile).collect(Collectors.joining(","))+"]";
	}
}
