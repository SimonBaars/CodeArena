package com.simonbaars.clonerefactor.compare;

import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.resolution.declarations.ResolvedValueDeclaration;
import com.github.javaparser.resolution.types.ResolvedType;
import com.simonbaars.clonerefactor.settings.CloneType;

public class CompareVariable extends Compare {
	private final NameExpr variableName;
	private final ResolvedValueDeclaration dec;
	private final ResolvedType type;
	
	public CompareVariable(CloneType cloneType, NameExpr t) {
		super(cloneType, t.getRange().get());
		variableName = t;
		ResolvedValueDeclaration refType = null;
		ResolvedType resolvedType = null;
		try {
			refType = t.resolve();
			resolvedType = refType.getType();
		} catch (Exception e) {}
		dec = refType;
		type = resolvedType;
	}
	
	@Override
	public boolean equals(Object o) {
		if(!super.equals(o))
			return false;
		CompareVariable compareDec = ((CompareVariable)o);
		if(cloneType == CloneType.TYPE1 && !variableName.equals(compareDec.variableName))
			return false;
		return type == null || type.equals(compareDec.type);
	}

	@Override
	public int getHashCode() {
		return (cloneType == CloneType.TYPE1 ? variableName.hashCode() : 0) + (type == null ? -3 : type.hashCode());
	}

	@Override
	public String toString() {
		return "CompareVariable [dec=" + dec.getName() + ", type=" + type + "]";
	}
}
