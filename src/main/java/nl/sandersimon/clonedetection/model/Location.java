package nl.sandersimon.clonedetection.model;

import java.io.File;
import java.io.IOException;
import java.util.function.BiConsumer;

import nl.sandersimon.clonedetection.common.Commons;

public class Location {
	String type;
	String file;
	
	int offset;
	int length;
	
	int beginLine;
	int beginCol;
	
	int endLine;
	int endCol;
	
	public Location() {
	}

	public Location(String type, String file, int beginLine, int beginCol, int endLine, int endCol) {
		super();
		this.type = type;
		this.file = file;
		this.beginLine = beginLine;
		this.beginCol = beginCol;
		this.endLine = endLine;
		this.endCol = endCol;
	}

	public Location(String type, String file, int offset, int length, int beginLine, int beginCol, int endLine,
			int endCol) {
		super();
		this.type = type;
		this.file = file;
		this.offset = offset;
		this.length = length;
		this.beginLine = beginLine;
		this.beginCol = beginCol;
		this.endLine = endLine;
		this.endCol = endCol;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public int getBeginLine() {
		return beginLine;
	}

	public void setBeginLine(int beginLine) {
		this.beginLine = beginLine;
	}

	public int getBeginCol() {
		return beginCol;
	}

	public void setBeginCol(int beginCol) {
		this.beginCol = beginCol;
	}

	public int getEndLine() {
		return endLine;
	}

	public void setEndLine(int endLine) {
		this.endLine = endLine;
	}

	public int getEndCol() {
		return endCol;
	}

	public void setEndCol(int endCol) {
		this.endCol = endCol;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + beginCol;
		result = prime * result + beginLine;
		result = prime * result + endCol;
		result = prime * result + endLine;
		result = prime * result + ((file == null) ? 0 : file.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Location other = (Location) obj;
		if (beginCol != other.beginCol)
			return false;
		if (beginLine != other.beginLine)
			return false;
		if (endCol != other.endCol)
			return false;
		if (endLine != other.endLine)
			return false;
		if (file == null) {
			if (other.file != null)
				return false;
		} else if (!file.equals(other.file))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	public static Location construct(String stringRepr) {
		Location loc = new Location();
		int pathSeparator = stringRepr.indexOf(':');
		loc.setType(stringRepr.substring(0, pathSeparator));
		int numLoc = stringRepr.indexOf('|');
		loc.setFile(stringRepr.substring(pathSeparator+3, numLoc));
		numLoc = parseNumber(stringRepr, Location::setOffset, loc, numLoc+2);
		numLoc = parseNumber(stringRepr, Location::setLength, loc, numLoc);
		numLoc = parseNumber(stringRepr, Location::setBeginLine, loc, numLoc+1);
		numLoc = parseNumber(stringRepr, Location::setBeginCol, loc, numLoc);
		numLoc = parseNumber(stringRepr, Location::setEndLine, loc, numLoc+2);
		parseNumber(stringRepr, Location::setEndCol, loc, numLoc);
		return loc;
	}

	public static<T> int parseNumber(String stringRepr, BiConsumer<T, Integer> function, T loc, int startIndex) {
		String offsetStr = collectInt(stringRepr, startIndex);
		function.accept(loc, Integer.parseInt(offsetStr));
		startIndex+=offsetStr.length()+1;
		return startIndex;
	}
	
	public static String collectInt(String str, int offset) {
		StringBuilder number = new StringBuilder();
		for(int i = offset; i<str.length(); i++) {
			char charAt = str.charAt(i);
			if(charAt>='0' && charAt<='9')
				number.append(charAt);
			else break;
		}
		return number.toString();
	}
	
	

	@Override
	public String toString() {
		return System.lineSeparator()+"Location"+System.lineSeparator()+"type=" + type + System.lineSeparator() + "file=" + file + "beginLine=" + beginLine + System.lineSeparator() + " endLine=" + endLine
				+ System.lineSeparator() + "code:"+System.lineSeparator() +"===================================="+ System.lineSeparator() + getSnippet() +"====================================";
	}

	public String getSnippet() {
		try {
			StringBuilder snippet = new StringBuilder();
			String content = Commons.getFileAsString(new File(file));
			String[] lines = content.split(System.lineSeparator());
			for(int i = beginLine - 1; i<=endLine-1; i++)
				snippet.append(lines[i] + System.lineSeparator());
			return snippet.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public File file() {
		return new File(getFile());
	}

	public String getName() {
		return file().getName();
	}

	public String rascalFile() {
		return "|file://"+getFile()+"|";
	}
}
