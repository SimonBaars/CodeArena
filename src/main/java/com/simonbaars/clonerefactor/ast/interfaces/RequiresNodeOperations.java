package com.simonbaars.clonerefactor.ast.interfaces;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.AnnotationMemberDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.ReceiverParameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.nodeTypes.NodeWithBlockStmt;
import com.github.javaparser.ast.nodeTypes.NodeWithBody;
import com.github.javaparser.ast.nodeTypes.NodeWithIdentifier;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.Type;

public interface RequiresNodeOperations {
	@SuppressWarnings("rawtypes")
	public default List<Node> childrenToParse(Node parent){
		if(parent instanceof MethodDeclaration) {
			Optional<BlockStmt> body = ((MethodDeclaration)parent).getBody();
			return body.isPresent() ? body.get().getChildNodes() : new ArrayList<>(0);
		}else if(parent instanceof NodeWithBody)
			return ((NodeWithBody)parent).getBody().getChildNodes();
		else if (parent instanceof NodeWithBlockStmt)
			return ((NodeWithBlockStmt)parent).getBody().getChildNodes();
		return parent.getChildNodes();
	}
	
	public default boolean isExcluded(Node n) {
		return n instanceof Expression || n instanceof Modifier || n instanceof NodeWithIdentifier || n instanceof Comment || n instanceof Type || n instanceof AnnotationMemberDeclaration || n instanceof Parameter || n instanceof ReceiverParameter || (n instanceof VariableDeclarator && n.getParentNode().get() instanceof FieldDeclaration);
	}
}
