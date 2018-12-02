package nl.sandersimon.clonedetection.model;

public class Location {
	String type;
	String file;
	
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
		this.type = stringRepr.substring(0, stringRepr.indexOf(':'));
		return null;
	}

}
