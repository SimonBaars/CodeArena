package com.simonbaars.clonerefactor.model.simple;

import java.util.List;
import java.util.stream.Collectors;

import com.github.javaparser.ast.Node;
import com.simonbaars.clonerefactor.ast.interfaces.RequiresNodeOperations;

public class AbstractClonedStatement implements RequiresNodeOperations {
	private final Class<? extends Node> statementClass;
	private final Class<? extends Node> parentClass;
	private final List<Class<? extends Node>> childClasses;
	
	public AbstractClonedStatement(Node n) {
		statementClass = n.getClass();
		parentClass = n.getParentNode().isPresent() ? n.getParentNode().get().getClass() : null;
		childClasses = childrenToParse(n).stream().filter(e -> !isExcluded(e)).map(e -> e.getClass()).collect(Collectors.toList());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((childClasses == null) ? 0 : childClasses.hashCode());
		result = prime * result + ((parentClass == null) ? 0 : parentClass.hashCode());
		result = prime * result + ((statementClass == null) ? 0 : statementClass.hashCode());
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
		AbstractClonedStatement other = (AbstractClonedStatement) obj;
		if (childClasses == null) {
			if (other.childClasses != null)
				return false;
		} else if (!childClasses.equals(other.childClasses))
			return false;
		if (parentClass == null) {
			if (other.parentClass != null)
				return false;
		} else if (!parentClass.equals(other.parentClass))
			return false;
		if (statementClass == null) {
			if (other.statementClass != null)
				return false;
		} else if (!statementClass.equals(other.statementClass))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "AbstractClonedStatement [statementClass=" + statementClass.getName() + ", parentClass=" + parentClass.getName()
				+ ", childClasses=" + childClasses.stream().map(e -> e.getName()).collect(Collectors.joining(", ", "{", "}")) + "]";
	}
	
	
}
