package com.simonbaars.clonerefactor.model.location;

import java.nio.file.Path;

import com.github.javaparser.Range;
import com.github.javaparser.ast.Node;
import com.simonbaars.clonerefactor.compare.HasRange;
import com.simonbaars.clonerefactor.metrics.enums.CloneLocation;
import com.simonbaars.clonerefactor.metrics.enums.CloneLocation.LocationType;

public class Location implements Comparable<Location>, HasRange {
	private final Path file;
	private Range range;
	
	private final LocationContents contents;
	
	private Location prevLocation;
	private Location clone;
	private Location nextLocation;

	private LocationType locationType;

	public Location(Path file, Range r, Location prevLocation) {
		this(file, r);
		this.prevLocation = prevLocation;
	}

	public Location(Location clonedLocation) {
		this(clonedLocation, clonedLocation.range);
	}

	public Location(Path file, Range range) {
		this.file = file;
		this.range = range;
		this.contents = new LocationContents();
	}

	public Location(Location clonedLocation, Range r) {
		this.file = clonedLocation.file;
		this.contents = new LocationContents(clonedLocation.contents, r);
		this.range = r;
		this.prevLocation = clonedLocation.prevLocation;
		this.clone = clonedLocation.clone;
		this.nextLocation = clonedLocation.nextLocation;
		if(range!=clonedLocation.range)
			getContents().stripToRange();
	}
	
	public Location(Path path, Location prevLocation, Node n) {
		this.file = path;
		this.prevLocation = prevLocation;
		this.contents = new LocationContents(n);
		this.range = contents.getRange();
	}

	public Path getFile() {
		return file;
	}
	
	public Location getPrevLine() {
		return prevLocation;
	}

	public void setPrevLine(Location nextLine) {
		this.prevLocation = nextLine;
	}

	public Location getClone() {
		return clone;
	}

	public void setClone(Location clone) {
		this.clone = clone;
	}

	@Override
	public String toString() {
		return "Location [file=" + file + ", range=" + range + "]";
	}
	
	public int getAmountOfLines() {
		return range.end.line-range.begin.line+1;
	}
	
	public int getEffectiveLines() {
		return getContents().getEffectiveLines().size();
	}

	public boolean isSame(Location other) {
		if (range != other.range)
			return false;
		if (file == null) {
			if (other.file != null)
				return false;
		}
		return file.equals(other.file);
	}

	public int getAmountOfTokens() {
		return getContents().getAmountOfTokens();
	}

	public Location getNextLine() {
		return nextLocation;
	}

	public void setNextLine(Location nextLine) {
		this.nextLocation = nextLine;
	}

	public LocationContents getContents() {
		return contents;
	}

	public Range getRange() {
		return range;
	}

	public Location getPrevLocation() {
		return prevLocation;
	}

	public void setPrevLocation(Location prevLocation) {
		this.prevLocation = prevLocation;
	}

	public Location getNextLocation() {
		return nextLocation;
	}
	
	public void setRange(Range r) {
		this.range = r;
	}

	public void setNextLocation(Location nextLocation) {
		this.nextLocation = nextLocation;
	}

	public Location mergeWith(Location oldClone) {
		if(file != oldClone.getFile())
			throw new IllegalStateException("Files of merging locations do not match! "+file+" != "+oldClone.getFile());
		Location copy = new Location(this, getRange().withEnd(oldClone.getRange().end));
		copy.contents.merge(oldClone.getContents());
		return copy;
	}

	public int getAmountOfNodes() {
		return getContents().getNodes().size();
	}

	public LocationType getLocationType() {
		return locationType;
	}
	
	public void setMetrics(CloneLocation l) {
		this.locationType = l.get(this);
	}

	@Override
	public int compareTo(Location o) {
		int stringCompare = file.compareTo(o.file);
		if(stringCompare == 0)
			return range.begin.compareTo(o.range.begin);
		return stringCompare;
	}

	public String getName() {
		return file.getName(file.getNameCount()-1).toString();
	}
}
