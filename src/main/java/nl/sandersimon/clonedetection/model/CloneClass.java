package nl.sandersimon.clonedetection.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.sandersimon.clonedetection.CloneDetection;
import nl.sandersimon.clonedetection.editor.CodeEditorMaker;

public class CloneClass implements Comparable<CloneClass>{

	int lines;
	List<Location> locations = new ArrayList<>();
	
	public CloneClass() {}

	public CloneClass(int lines, List<Location> locations) {
		super();
		this.lines = lines;
		this.locations = locations;
		CloneDetection.get().getTotalNumberOfCloneClasses().incrementScore();
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
	}

	public int size() {
		return locations.size();
	}

	public Location get(int j) {
		return locations.get(j);
	}

	@Override
	public String toString() {
		return "CloneClass [lines=" + lines + ", locations=" + Arrays.toString(locations.toArray()) + "]";
	}
	
	public int volume() {
		return lines * size();
	}

	public void open() {
		CodeEditorMaker.create(this);
	}
}
