package com.simonbaars.clonerefactor.compare;

import java.util.Collections;
import java.util.List;

import com.github.javaparser.JavaToken;
import com.github.javaparser.Range;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.LiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.type.ReferenceType;
import com.simonbaars.clonerefactor.ast.interfaces.HasCompareList;
import com.simonbaars.clonerefactor.settings.CloneType;

public abstract class Compare implements HasRange {
	protected CloneType cloneType;
	private final Range range;
	
	protected Compare(CloneType cloneType, Range range) {
		this.cloneType=cloneType;
		this.range = range;
	}
	
	public abstract int getHashCode();
	
	public static Compare create(Object tokenOrNode, JavaToken e, CloneType cloneType) {
		if(tokenOrNode instanceof ReferenceType)
			return new CompareType(cloneType, (ReferenceType)tokenOrNode);
		else if(tokenOrNode instanceof NameExpr)
			return new CompareVariable(cloneType, (NameExpr)tokenOrNode);
		else if(tokenOrNode instanceof LiteralExpr)
			return new CompareLiteral(cloneType, e);
		else if(tokenOrNode instanceof SimpleName)
			return new CompareName(cloneType, e);
		else if(tokenOrNode instanceof MethodCallExpr)
			return new CompareMethodCall(cloneType, (MethodCallExpr)tokenOrNode);
		return new CompareToken(cloneType, e);
	}
	
	/**
	 * These nodes are compared by node rather than token.
	 * @return
	 */
	public static boolean comparingNode(Node node) {
		return node instanceof ReferenceType || node instanceof NameExpr || node instanceof LiteralExpr || node instanceof SimpleName || node instanceof MethodCallExpr;
	}

	public boolean equals(Object o) {
		if(this.getClass() != o.getClass())
			return false;
		return true;
	}
	
	public void setCloneType(CloneType type) {
		this.cloneType = type;
	}
	
	public List<Compare> relevantChildren(HasCompareList locationContents){
		return Collections.emptyList();
	}

	public Range getRange() {
		return range;
	}
}
