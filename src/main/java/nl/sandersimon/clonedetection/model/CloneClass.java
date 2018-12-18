package nl.sandersimon.clonedetection.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.scoreboard.Score;
import nl.sandersimon.clonedetection.CloneDetection;
import nl.sandersimon.clonedetection.editor.CodeEditor;
import nl.sandersimon.clonedetection.editor.CodeEditorMaker;

public class CloneClass implements Comparable<CloneClass>{

	int lines;
	List<Location> locations = new ArrayList<>();
	
	public CloneClass() {
		super();
		CloneDetection.get().getTotalNumberOfCloneClasses().incrementScore();
	}

	public CloneClass(int lines, List<Location> locations) {
		this();
		this.lines = lines;
		this.locations = locations;
	}

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

	public void add(Location construct) {
		CloneDetection c = CloneDetection.get();
		locations.add(construct);
		c.getTotalNumberOfClones().incrementScore();
		c.getTotalCloneVolume().increaseScore(lines);
		Score mostLines = c.getMostLinesCloneClass();
		if(lines > mostLines.getScorePoints())
			mostLines.setScorePoints(lines);
		Score mostOccurrent = c.getMostOccurrentClone();
		if(size() > mostOccurrent.getScorePoints())
			mostOccurrent.setScorePoints(size());
		Score highestVolume = c.getBiggestCloneClass();
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

	public List<CodeEditor> open() {
		return CodeEditorMaker.create(this);
	}

	public String getName() {
		if(size() == 0)
			return "error";
		return get(0).getName();
	}

	public String rascalLocList() {
		return "["+locations.stream().map(e -> e.rascalFile()).collect(Collectors.joining(","))+"]";
	}
}
