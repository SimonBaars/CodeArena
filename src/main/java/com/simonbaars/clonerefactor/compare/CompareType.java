package com.simonbaars.clonerefactor.compare;

import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.simonbaars.clonerefactor.settings.CloneType;

public class CompareType extends Compare {
	private final ReferenceType referenceType;
	private final ResolvedReferenceType type;
	
	public CompareType(CloneType cloneType, ReferenceType t) {
		super(cloneType, t.getRange().get());
		this.referenceType = t;
		ResolvedReferenceType refType = null;
		try {
			refType = (ResolvedReferenceType)t.resolve();
		} catch (Exception e) {}
		type = refType;
	}
	
	public boolean equals(Object o) {
		if(!super.equals(o))
			return false;
		CompareType other = ((CompareType)o);
		if(type!=null)
			return type.equals(other.type);
		return referenceType.equals(other.referenceType);
	}
	
	@Override
	public int getHashCode() {
		if(type==null)
			return referenceType.hashCode();
		return type.hashCode();
	}

	@Override
	public String toString() {
		return "CompareType [type=" + type + "]";
	}
}
