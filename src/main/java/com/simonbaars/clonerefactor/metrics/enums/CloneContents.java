package com.simonbaars.clonerefactor.metrics.enums;

import static com.simonbaars.clonerefactor.metrics.enums.CloneContents.ContentsType.FULLCLASS;
import static com.simonbaars.clonerefactor.metrics.enums.CloneContents.ContentsType.FULLCONSTRUCTOR;
import static com.simonbaars.clonerefactor.metrics.enums.CloneContents.ContentsType.FULLENUM;
import static com.simonbaars.clonerefactor.metrics.enums.CloneContents.ContentsType.FULLINTERFACE;
import static com.simonbaars.clonerefactor.metrics.enums.CloneContents.ContentsType.FULLMETHOD;
import static com.simonbaars.clonerefactor.metrics.enums.CloneContents.ContentsType.HASCLASSDECLARATION;
import static com.simonbaars.clonerefactor.metrics.enums.CloneContents.ContentsType.HASENUMDECLARATION;
import static com.simonbaars.clonerefactor.metrics.enums.CloneContents.ContentsType.HASENUMFIELDS;
import static com.simonbaars.clonerefactor.metrics.enums.CloneContents.ContentsType.HASINTERFACEDECLARATION;
import static com.simonbaars.clonerefactor.metrics.enums.CloneContents.ContentsType.INCLUDESCONSTRUCTOR;
import static com.simonbaars.clonerefactor.metrics.enums.CloneContents.ContentsType.INCLUDESFIELDS;
import static com.simonbaars.clonerefactor.metrics.enums.CloneContents.ContentsType.ONLYFIELDS;
import static com.simonbaars.clonerefactor.metrics.enums.CloneContents.ContentsType.OTHER;
import static com.simonbaars.clonerefactor.metrics.enums.CloneContents.ContentsType.PARTIALCONSTRUCTOR;
import static com.simonbaars.clonerefactor.metrics.enums.CloneContents.ContentsType.PARTIALMETHOD;
import static com.simonbaars.clonerefactor.metrics.enums.CloneContents.ContentsType.SEVERALMETHODS;

import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.EnumConstantDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.simonbaars.clonerefactor.ast.interfaces.RequiresNodeOperations;
import com.simonbaars.clonerefactor.metrics.enums.CloneContents.ContentsType;
import com.simonbaars.clonerefactor.model.Sequence;
import com.simonbaars.clonerefactor.model.location.LocationContents;

public class CloneContents implements MetricEnum<ContentsType>, RequiresNodeOperations {
	public enum ContentsType{
		FULLMETHOD, 
		PARTIALMETHOD, 
		SEVERALMETHODS, 
		FULLCONSTRUCTOR,
		PARTIALCONSTRUCTOR,
		ONLYFIELDS, 
		FULLCLASS, 
		FULLINTERFACE,
		FULLENUM,
		HASCLASSDECLARATION, 
		HASINTERFACEDECLARATION, 
		HASENUMDECLARATION, 
		HASENUMFIELDS,
		INCLUDESFIELDS,
		INCLUDESCONSTRUCTOR,
		OTHER;
	}

	@Override
	public ContentsType get(Sequence sequence) {
		return get(sequence.getAny().getContents());
	}

	public ContentsType get(LocationContents c) {
		List<Node> nodes = c.getNodes();
		if(nodes.get(0) instanceof MethodDeclaration && nodes.get(nodes.size()-1) == getLastStatement(nodes.get(0))) {
			return FULLMETHOD;
		} else if(getMethod(nodes.get(0))!=null && getMethod(nodes.get(0)) == getMethod(nodes.get(nodes.size()-1))) {
			return PARTIALMETHOD;
		} else if(nodes.get(0) instanceof ConstructorDeclaration && nodes.get(nodes.size()-1) == getLastStatement(nodes.get(0))) {
			return FULLCONSTRUCTOR;
		} else if(getConstructor(nodes.get(0))!=null && getConstructor(nodes.get(0)) == getConstructor(nodes.get(nodes.size()-1))) {
			return PARTIALCONSTRUCTOR;
		} else if(nodes.stream().allMatch(e -> getMethod(e)!=null)) {
			return SEVERALMETHODS;
		} else if(nodes.stream().allMatch(e -> getMethod(e)== null && e instanceof FieldDeclaration)) {
			return ONLYFIELDS;
		} else if(nodes.get(0) instanceof ClassOrInterfaceDeclaration && !((ClassOrInterfaceDeclaration)nodes.get(0)).isInterface() && nodes.get(nodes.size()-1) == getLastStatement(nodes.get(0))) {
			return FULLCLASS;
		} else if(nodes.get(0) instanceof ClassOrInterfaceDeclaration && ((ClassOrInterfaceDeclaration)nodes.get(0)).isInterface() && nodes.get(nodes.size()-1) == getLastStatement(nodes.get(0))) {
			return FULLINTERFACE;
		} else if(nodes.get(0) instanceof EnumDeclaration && nodes.get(nodes.size()-1) == getLastStatement(nodes.get(0))) {
			return FULLENUM;
		} else if(nodes.stream().anyMatch(e -> e instanceof ClassOrInterfaceDeclaration && !((ClassOrInterfaceDeclaration)e).isInterface())) {
			return HASCLASSDECLARATION;
		} else if(nodes.stream().anyMatch(e -> e instanceof ClassOrInterfaceDeclaration && ((ClassOrInterfaceDeclaration)e).isInterface())) {
			return HASINTERFACEDECLARATION;
		} else if(nodes.stream().anyMatch(e -> e instanceof EnumDeclaration)) {
			return HASENUMDECLARATION;
		} else if(nodes.stream().anyMatch(e -> e instanceof EnumConstantDeclaration)) {
			return HASENUMFIELDS;
		} else if(nodes.stream().anyMatch(e -> getMethod(e)== null && e instanceof FieldDeclaration)) {
			return INCLUDESFIELDS;
		} else if(nodes.stream().anyMatch(e -> getConstructor(e)!=null)) {
			return INCLUDESCONSTRUCTOR;
		}
		return OTHER;
	}

	private Node getLastStatement(Node n) {
		List<Node> children = n.getChildNodes();
		Optional<Node> reduce = children.stream().filter(e -> !isExcluded(e)).reduce((first, second) -> second);
		if(reduce.isPresent()) {
			n = reduce.get();
			if(!n.getChildNodes().isEmpty())
				return getLastStatement(n);
		}
		return n;
	}
}
