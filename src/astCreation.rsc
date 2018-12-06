// map(loc, map(line, list[comparableastvalues]);

module astCreation

import IO;
import Set;
import Map;
import List;
import Node;
import String;
import lang::java::m3::Core;
import lang::java::m3::AST;
import lang::java::jdt::m3::Core;
import lang::java::jdt::m3::AST;
import util::Math;

int minAmountOfLines = 6;

public list[list[loc]] getDuplication(list[Declaration] asts) {
    map[loc, map[int, list[value]]] fileLineAsts = ();
    fileLineAsts = fileLineMapGeneration(asts, fileLineAsts);
    map[int, set[loc]] duplicateSet = getDupSet(fileLineAsts);
	list[list[loc]] duplicateList = dupSetToList(duplicateSet);
    iprint(fileLineAsts);
    return duplicateList;
}

public list[list[loc]] dupSetToList(map[int, set[loc]] duplicateSet){
	list[list[loc]] duplicateList = [];
	for(dupSet <- duplicateSet)
		duplicateList += [toList(duplicateSet[dupSet])];
	return duplicateList;
}

public map[loc, map[int, list[value]]] fileLineMapGeneration(list[Declaration] asts, map[loc, map[int, list[value]]] fileLineAsts) {
	for (m <- asts){
		fileLineAsts[m.src] = getLocLineAst(m);
	}
	return fileLineAsts;
}

public map[int, list[value]] getLocLineAst(Declaration location) {
	map[int, list[value]] astMap = (); 
	visit (location) {
        case Declaration d: astMap = addToMap(astMap, d);
		case Statement d: astMap = addToMap(astMap, d);
	 	case Expression d: astMap = addToMap(astMap, d);
    }
	return astMap;
}

public map[int, list[value]] addToMap(map[int, list[value]] astMap, node d){
	int beginLine = getSrc(d).begin.line;
	int endLine = getSrc(d).end.line;
	if(beginLine in astMap)
		astMap[beginLine] += getComparablesType1(d);
	else astMap[beginLine] = getComparablesType1(d);
	if(endLine in astMap)
		astMap[endLine] += getComparablesType1(d);
	else astMap[endLine] = getComparablesType1(d);
	return astMap;
}

public map[int, set[loc]] getDupSet(map[loc, map[int, list[value]]] fileLineAsts){
	for(i <- [0..size(bucket)-1]){
		loc headSrc = getSrc(bucket[i]);
		for(j <- [i..size(bucket)]){
			loc tailSrc = getSrc(bucket[j]);
			if(headSrc != tailSrc && compareAsts(getComparablesType1(bucket[i]), getComparablesType1(bucket[j])))
				duplicateSet = addToDupSet(duplicateSet, headSrc, tailSrc);
		}
	}
	return duplicateSet;
}	

//public map[int, set[loc]] addToDupSet(map[int, set[loc]] duplicateSet, loc sourceOne, loc sourceTwo) {
//	bool found = false;
//	int lastItem = 0;
//	for(locSet <- duplicateSet){
//		if(sourceOne in duplicateSet[locSet]){
//			found = true;
//			duplicateSet[locSet] += sourceTwo;
//		}
//		if(sourceTwo in duplicateSet[locSet]){
//			found = true;
//			duplicateSet[locSet] += sourceOne;
//		}
//		if(locSet > lastItem)
//			lastItem = locSet;
//	}
//	if(!found)
//		duplicateSet[lastItem + 1] = {sourceOne, sourceTwo};
//	return duplicateSet;
//}

public int getSourceLength(node n){
	loc l = getSrc(n);
	if(l == |unknown:///|){
		return -1;
	}
	return l.end.line-l.begin.line+1;
}

public loc getSrc(value ast) {
	switch (ast) {
        case Declaration d: return d.src;
		case Statement d: return d.src;
	 	case Expression d: return d.src;
	}
}

public bool compareAsts(list[value] ast1, list[value] ast2){
	//println("DIT ZIJN DE TWEE ASTS");
	//iprint(ast1);
	//iprint(ast2);
	//println();
	if(ast1 == ast2){
		//iprint(ast1);
		//iprint(ast2);
		//println("DE TWEE ASTS ZIJN GELIJK");
		return true;}
	return false;
}

list[value] listComparablesType1(list[node] n){
	list[value] noodList = [];
	for(nood <- n)
		noodList += getComparablesType1(nood);
	return noodList;
}

//(type == 1 ? [name] : [])
list[value] getComparablesType1(node n){
    switch(n){
    //Decls
        case \compilationUnit(list[Declaration] imports, list[Declaration] types) : return listComparablesType1(imports)+listComparablesType1(types);
	    case \compilationUnit(Declaration package, list[Declaration] imports, list[Declaration] types) : return listComparablesType1(imports)+listComparablesType1(types);
	    case \enum(str name, list[Type] implements, list[Declaration] constants, list[Declaration] body) : return  [name]+listComparablesType1(constants)+listComparablesType1(body);
	    case \enumConstant(str name, list[Expression] arguments, Declaration class) : return [name] + listComparablesType1(arguments) +getComparablesType1(class);
	    case \enumConstant(str name, list[Expression] arguments) : return [name] + listComparablesType1(arguments);
	    case \class(str name, list[Type] extends, list[Type] implements, list[Declaration] body) : return [name] + listComparablesType1(extends) + listComparablesType1(implements) + listComparablesType1(body);
	    case \class(list[Declaration] body) : return listComparablesType1(body);
	    case \interface(str name, list[Type] extends, list[Type] implements, list[Declaration] body) : return [name] + listComparablesType1(extends) + listComparablesType1(implements) + listComparablesType1(body);
	    case \field(Type \type, list[Expression] fragments) : return [\type] + listComparablesType1(fragments);
	    case \initializer(Statement initializerBody) : return getComparablesType1(initializerBody);
	    case \method(Type \return, str name, list[Declaration] parameters, list[Expression] exceptions, Statement impl) : return [\return] + [name] + listComparablesType1(parameters) + listComparablesType1(exceptions) + getComparablesType1(impl); 
	    case \method(Type \return, str name, list[Declaration] parameters, list[Expression] exceptions) : return [\return] + [name] + listComparablesType1(parameters) + listComparablesType1(exceptions); 
	    case \constructor(str name, list[Declaration] parameters, list[Expression] exceptions, Statement impl) : return [name] + listComparablesType1(parameters) + listComparablesType1(exceptions) + getComparablesType1(impl); 
	    case \import(str name) : return [name];
	    case \package(str name) : return [name];
	    case \package(Declaration parentPackage, str name) : return getComparablesType1(parentPackage) + [name];
	    case \variables(Type \type, list[Expression] \fragments) : return [\type] + listComparablesType1(\fragments);
	    case \typeParameter(str name, list[Type] extendsList) : return [name] + listComparablesType1(extendsList);
	    case \annotationType(str name, list[Declaration] body) : return [name] + listComparablesType1(body);
	    case \annotationTypeMember(Type \type, str name) : return [\type] + [name];
	    case \annotationTypeMember(Type \type, str name, Expression defaultBlock) : return [\type] + [name] + getComparablesType1(defaultBlock);
	    case \parameter(Type \type, str name, int extraDimensions) : return [\type.typ] + [name] + [extraDimensions];
	    case \vararg(Type \type, str name) : return [\type] + [name];
	   
    ////Exprs
	   case \arrayAccess(Expression array, Expression index) : return getComparablesType1(array) + getComparablesType1(index);
	   case \newArray(Type \type, list[Expression] dimensions, Expression init) : return [\type] + listComparablesType1(dimensions) + getComparablesType1(init);
	   case \newArray(Type \type, list[Expression] dimensions) : return [\type] + listComparablesType1(dimensions);
	   case \arrayInitializer(list[Expression] elements) : return listComparablesType1(elements);
	   case \assignment(Expression lhs, str operator, Expression rhs) : return getComparablesType1(lhs) + [operator] + getComparablesType1(rhs);
	   case \cast(Type \type, Expression expression) : return [\type] + getComparablesType1(expression);
	   case \characterLiteral(str charValue) : return [charValue];
	   case \newObject(Expression expr, Type \type, list[Expression] args, Declaration class) : return getComparablesType1(expr) + [\type] + listComparablesType1(args) + getComparablesType1(class);
 	   case \newObject(Expression expr, Type \type, list[Expression] args) : return getComparablesType1(expr) + [\type] + listComparablesType1(args);
	   case \newObject(Type \type, list[Expression] args, Declaration class) : return [\type] + listComparablesType1(args) + getComparablesType1(class);
	   case \newObject(Type \type, list[Expression] args) : return [\type] + listComparablesType1(args);
	   case \qualifiedName(Expression qualifier, Expression expression) : return getComparablesType1(qualifier) + getComparablesType1(expression);
	   case \conditional(Expression expression, Expression thenBranch, Expression elseBranch) : return getComparablesType1(expression) + getComparablesType1(elseBranch) + getComparablesType1(elseBranch);
	   case \fieldAccess(bool isSuper, Expression expression, str name) : return [isSuper] + getComparablesType1(expression) + [name];
	   case \fieldAccess(bool isSuper, str name) : return [isSuper] + [name];
	   case \instanceof(Expression leftSide, Type rightSide) : return getComparablesType1(leftSide) + [rightSide];
	   case \methodCall(bool isSuper, str name, list[Expression] arguments) : return [isSuper] + [name] + listComparablesType1(arguments);
	   case \methodCall(bool isSuper, Expression receiver, str name, list[Expression] arguments) : return [isSuper] + getComparablesType1(receiver) + [name] + listComparablesType1(arguments);
	   //case \null() : return [];
	   case \number(str numberValue) : return [numberValue];
	   case \booleanLiteral(bool boolValue) : return [boolValue];
	   case \stringLiteral(str stringValue) : return [stringValue];
	   case \type(Type \type) : return [\type];
	   case \variable(str name, int extraDimensions) : return [name] + [extraDimensions];
	   case \variable(str name, int extraDimensions, Expression \initializer) : return [name] + [extraDimensions] + getComparablesType1(\initializer);
	   case \bracket(Expression expression) : return getComparablesType1(expression);
	   case \this() : return [];
	   case \this(Expression thisExpression) : return getComparablesType1(thisExpression);
	   case \super() : return [];
	   case \declarationExpression(Declaration declaration) : return getComparablesType1(declaration);
	   case \infix(Expression lhs, str operator, Expression rhs) : return getComparablesType1(lhs) + [operator] + getComparablesType1(rhs);
	   case \postfix(Expression operand, str operator) : return getComparablesType1(operand) + [operator];
	   case \prefix(str operator, Expression operand) : return [operator] + getComparablesType1(operand);
	   case \simpleName(str name) : return [name];
	   case \markerAnnotation(str typeName) : return [typeName];
	   case \normalAnnotation(str typeName, list[Expression] memberValuePairs) : return [typeName] + listComparablesType1(memberValuePairs);
	   case \memberValuePair(str name, Expression \value) : return [name] + getComparablesType1(\value);
	   case \singleMemberAnnotation(str typeName, Expression \value) : return [typeName] + getComparablesType1(\value);
    //
    ////Statements
	   case \assert(Expression expression) : return getComparablesType1(expression);
	   case \assert(Expression expression, Expression message) : return getComparablesType1(expression) + getComparablesType1(message);
	   case \block(list[Statement] statements) : return listComparablesType1(statements);
	   case \break() : return [];
	   case \break(str label) : return [label];
	   case \continue() : return [];
	   case \continue(str label) : return [label];
	   case \do(Statement body, Expression condition) : return getComparablesType1(body) + getComparablesType1(condition);
	   case \empty() : return [];
	   case \foreach(Declaration parameter, Expression collection, Statement body) : return getComparablesType1(parameter) + getComparablesType1(collection) + getComparablesType1(body);
	   case \for(list[Expression] initializers, Expression condition, list[Expression] updaters, Statement body) : return listComparablesType1(initializers) + getComparablesType1(condition) + listComparablesType1(updaters) + getComparablesType1(body);
	   case \for(list[Expression] initializers, list[Expression] updaters, Statement body) : return listComparablesType1(initializers) + listComparablesType1(updaters) + getComparablesType1(body);
	   case \if(Expression condition, Statement thenBranch) : return getComparablesType1(condition) + getComparablesType1(thenBranch);
	   case \if(Expression condition, Statement thenBranch, Statement elseBranch) : return getComparablesType1(condition) + getComparablesType1(thenBranch) + getComparablesType1(elseBranch);
	   //case \label(str name, Statement body) : return [name] + getComparablesType1(body);  error: |project://CloneDetection/src/astCreation.rsc|(12714,1,<232,27>,<232,28>): Expected list[value], but got void
	   case \return(Expression expression) : return getComparablesType1(expression);
	   case \return() : return [];
	   case \switch(Expression expression, list[Statement] statements) : return getComparablesType1(expression) + listComparablesType1(statements);
	   case \case(Expression expression) : return getComparablesType1(expression);
	   case \defaultCase() : return [];
	   case \synchronizedStatement(Expression lock, Statement body) : return getComparablesType1(lock) + getComparablesType1(body); 
	   case \throw(Expression expression) : return getComparablesType1(expression);
	   case \try(Statement body, list[Statement] catchClauses) : return getComparablesType1(body) + listComparablesType1(catchClauses);
	   case \try(Statement body, list[Statement] catchClauses, Statement \finally) : return getComparablesType1(body) + listComparablesType1(catchClauses) + getComparablesType1(\finally);                                      
	   case \catch(Declaration exception, Statement body) : return getComparablesType1(exception) + getComparablesType1(body);
	   case \declarationStatement(Declaration declaration) : return getComparablesType1(declaration);
	   case \while(Expression condition, Statement body) : return getComparablesType1(condition) + getComparablesType1(body);
	   case \expressionStatement(Expression stmt) : return getComparablesType1(stmt);
	   case \constructorCall(bool isSuper, Expression expr, list[Expression] arguments) : return [isSuper] + getComparablesType1(expr) + listComparablesType1(arguments);
	   case \constructorCall(bool isSuper, list[Expression] arguments) : return [isSuper] + listComparablesType1(arguments);
	   
    //////Type
    //   case arrayType(Type \type) : return [sss];
    //   case simpleType(Expression name) : return getComparablesType1(name);
    }
    return [];
}