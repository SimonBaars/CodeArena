package com.simonbaars.clonerefactor.ast;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.AnnotationDeclaration;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumConstantDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.ForEachStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.stmt.WhileStmt;

public enum NodeType {
	METHODHEADER,
	METHODBODY,
	CLASSHEADER,
	INTERFACEHEADER,
	VARIABLEDECLARATION,
	LAMBDA,
	ENUMFIELD,
	ENUMHEADER,
	ANNOTATION,
	FORLOOPHEADER,
	WHILELOOPHEADER,
	TRY,
	CATCH,
	OTHER;
	
	public static NodeType getNodeType(Node n) {
		if(n instanceof ForEachStmt || n instanceof ForStmt)
			return FORLOOPHEADER;
		else if(n instanceof WhileStmt)
			return WHILELOOPHEADER;
		else if(n instanceof CatchClause)
			return CATCH;
		else if(n instanceof TryStmt) 
			return TRY;
		else if (n instanceof AnnotationDeclaration)
			return ANNOTATION;
		else if(n instanceof EnumDeclaration)
			return ENUMHEADER;
		else if(n instanceof EnumConstantDeclaration)
			return ENUMFIELD;
		else if(n instanceof LambdaExpr)
			return LAMBDA;
		else if (n instanceof VariableDeclarationExpr)
			return VARIABLEDECLARATION;
		else if(n instanceof ClassOrInterfaceDeclaration)
			return ((ClassOrInterfaceDeclaration)n).isInterface() ? INTERFACEHEADER : CLASSHEADER;
		else if(n instanceof BodyDeclaration)
			return METHODBODY;
		else if(n instanceof MethodDeclaration)
			return METHODHEADER;
		return OTHER;
	}
}
