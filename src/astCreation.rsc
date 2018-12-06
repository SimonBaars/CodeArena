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
			if(headSrc != tailSrc && compareAsts(getComparables(bucket[i]), getComparables(bucket[j])))
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

/*list[value] listComparablesType1(list[node] n){
	list[value] noodList = [];
	for(nood <- n)
		noodList += getComparablesType1(nood);
	return noodList;
}*/

//(type == 1 ? [name] : [])
tuple[int,list[value]] getComparables(node n, int t){
    switch(n){
    //Decls
        case \compilationUnit(list[Declaration] imports, list[Declaration] types) : return <1,[]>;
	    case \compilationUnit(Declaration package, list[Declaration] imports, list[Declaration] types) : return <2,[]>;
	    case \enum(str name, list[Type] implements, list[Declaration] constants, list[Declaration] body) : return <3,t==1?[name]:[]>;
	    case \enumConstant(str name, list[Expression] arguments, Declaration class) : return <4, (t == 1 ? [name] : [])>;
	    case \enumConstant(str name, list[Expression] arguments) : return <5, (t == 1 ? [name] : [])>;
	    case \class(str name, list[Type] extends, list[Type] implements, list[Declaration] body) : return <6, (t == 1 ? [name] : [])>;
	    case \class(list[Declaration] body) : return <7, []>;
	    case \interface(str name, list[Type] extends, list[Type] implements, list[Declaration] body) : return <8, (t == 1 ? [name] : [])>;
	    case \field(Type \type, list[Expression] fragments) : return <9, [\type]>;
	    case \initializer(Statement initializerBody) : return <10, []>;
	    case \method(Type \return, str name, list[Declaration] parameters, list[Expression] exceptions, Statement impl) : return <11, [\return] + (t == 1 ? [name] : [])>; 
	    case \method(Type \return, str name, list[Declaration] parameters, list[Expression] exceptions) : return <12, [\return] + (t == 1 ? [name] : [])>;
	    case \constructor(str name, list[Declaration] parameters, list[Expression] exceptions, Statement impl) : return <13, (t == 1 ? [name] : [])>;
	    case \import(str name) : return <14, (t == 1 ? [name] : [])>;
	    case \package(str name) : return <15, (t == 1 ? [name] : [])>;
	    case \package(Declaration parentPackage, str name) : return <16, (t == 1 ? [name] : [])>;
	    case \variables(Type \type, list[Expression] \fragments) : return <17, [\type]>;
	    case \typeParameter(str name, list[Type] extendsList) : return <18, (t == 1 ? [name] : [])>;
	    case \annotationType(str name, list[Declaration] body) : return <19, (t == 1 ? [name] : [])>;
	    case \annotationTypeMember(Type \type, str name) : return <20, [\type]+ (t == 1 ? [name] : [])>;
	    case \annotationTypeMember(Type \type, str name, Expression defaultBlock) : return <21, [\type] + (t == 1 ? [name] : [])>;
	    case \parameter(Type \type, str name, int extraDimensions) : return <22, [\type] + (t == 1 ? [name] : []) + [extraDimensions] >;
	    case \vararg(Type \type, str name) : return <23, [\type]+ (t == 1 ? [name] : [])>;
	   
    ////Exprs
	   case \arrayAccess(Expression array, Expression index) : return <24, []>;
	   case \newArray(Type \type, list[Expression] dimensions, Expression init) : return <25, [\type]>;
	   case \newArray(Type \type, list[Expression] dimensions) : return <26, [\type]>;
	   case \arrayInitializer(list[Expression] elements) : return <27, []>;
	   case \assignment(Expression lhs, str operator, Expression rhs) : return <28, [operator]>;
	   case \cast(Type \type, Expression expression) : return <29, [\type]>;
	   case \characterLiteral(str charValue) : return <30, [charValue]>;
	   case \newObject(Expression expr, Type \type, list[Expression] args, Declaration class) : return <31, [\type]>;
 	   case \newObject(Expression expr, Type \type, list[Expression] args) : return <32, [\type]>;
	   case \newObject(Type \type, list[Expression] args, Declaration class) : return <33, [\type]>;
	   case \newObject(Type \type, list[Expression] args) : return <34, [\type]>;
	   case \qualifiedName(Expression qualifier, Expression expression) : return <35, []>;
	   case \conditional(Expression expression, Expression thenBranch, Expression elseBranch) : return <36, []>;
	   case \fieldAccess(bool isSuper, Expression expression, str name) : return <37, [isSuper] + (t == 1 ? [name] : [])>;
	   case \fieldAccess(bool isSuper, str name) : return <38, [isSuper] + (t == 1 ? [name] : [])>;
	   case \instanceof(Expression leftSide, Type rightSide) : return <39, [rightSide]>;
	   case \methodCall(bool isSuper, str name, list[Expression] arguments) : return <40, [isSuper] + (t == 1 ? [name] : [])>;
	   case \methodCall(bool isSuper, Expression receiver, str name, list[Expression] arguments) : return <41, [isSuper] + (t == 1 ? [name] : [])>;
	   //case \null() : return <42, []>;
	   case \number(str numberValue) : return <43, [numberValue]>;
	   case \booleanLiteral(bool boolValue) : return  <44, [boolValue]>;
	   case \stringLiteral(str stringValue) : return <45, [stringValue]>;
	   case \type(Type \type) : return <46, [\type]>;
	   case \variable(str name, int extraDimensions) : return <47, (t == 1 ? [name] : []) + [extraDimensions]>;
	   case \variable(str name, int extraDimensions, Expression \initializer) : return <48, (t == 1 ? [name] : []) + [extraDimensions]>;
	   case \bracket(Expression expression) : return <49, []>;
	   case \this() : return <50, []>;
	   case \this(Expression thisExpression) : return <51, []>;
	   case \super() : return <52, []>;
	   case \declarationExpression(Declaration declaration) : return <53, []>;
	   case \infix(Expression lhs, str operator, Expression rhs) : return  <54, [operator]>;
	   case \postfix(Expression operand, str operator) : return <55, [operator]>;
	   case \prefix(str operator, Expression operand) : return <56, [operator]>;
	   case \simpleName(str name) : return <57, (t == 1 ? [name] : [])>;
	   case \markerAnnotation(str typeName) : return <58, [typeName]>;
	   case \normalAnnotation(str typeName, list[Expression] memberValuePairs) : return <59, [typeName]>;
	   case \memberValuePair(str name, Expression \value) : return <60, (t == 1 ? [name] : [])>;
	   case \singleMemberAnnotation(str typeName, Expression \value) : return <61, [typeName]>;
    //
    ////Statements
	   case \assert(Expression expression) : return <62, []>;
	   case \assert(Expression expression, Expression message) : return <63, []>;
	   case \block(list[Statement] statements) : return <64, []>;
	   case \break() : return <65, []>;
	   case \break(str label) : return <66, [label]>;
	   case \continue() : return <67, []>;
	   case \continue(str label) : return <68, [label]>;
	   case \do(Statement body, Expression condition) : return <69, []>;
	   case \empty() : return <70, []>;
	   case \foreach(Declaration parameter, Expression collection, Statement body) : return <71, []>;
	   case \for(list[Expression] initializers, Expression condition, list[Expression] updaters, Statement body) : return <72, []>;
	   case \for(list[Expression] initializers, list[Expression] updaters, Statement body) : return <73, []>;
	   case \if(Expression condition, Statement thenBranch) : return <74, []>;
	   case \if(Expression condition, Statement thenBranch, Statement elseBranch) : return <75, []>;
	   case \label(str name, Statement body) : <76, (t == 1 ? [name] : [])>;
	   case \return(Expression expression) : return <77, []>;
	   case \return() : return <78, []>;
	   case \switch(Expression expression, list[Statement] statements) : return <79, []>;
	   case \case(Expression expression) : return <80, []>;
	   case \defaultCase() : return <81, []>;
	   case \synchronizedStatement(Expression lock, Statement body) : return <82, []>;
	   case \throw(Expression expression) : return <83, []>;
	   case \try(Statement body, list[Statement] catchClauses) : return <84, []>;
	   case \try(Statement body, list[Statement] catchClauses, Statement \finally) : return <85, []>;                                  
	   case \catch(Declaration exception, Statement body) : return <86, []>;
	   case \declarationStatement(Declaration declaration) : return <87, []>;
	   case \while(Expression condition, Statement body) : return <88, []>;
	   case \expressionStatement(Expression stmt) : return <89, []>;
	   case \constructorCall(bool isSuper, Expression expr, list[Expression] arguments) : return <90, [isSuper]>;
	   case \constructorCall(bool isSuper, list[Expression] arguments) : return <91, [isSuper]>;
	   
    //////Type
    //   case arrayType(Type \type) : return [sss];
    //   case simpleType(Expression name) : return getComparablesType1(name);
    }
    return <99, []>;
}