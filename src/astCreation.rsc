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
alias Monster = map[loc, map[int, list[tuple[int, list[value]]]]];

public list[list[loc]] getDuplication(int t, list[Declaration] asts) {
    Monster fileLineAsts = fileLineMapGeneration(t, asts);
    iprintln(fileLineAsts);
    map[int, list[loc]] locsAtInt = calculateLocationsOfNodeTypes(fileLineAsts);
    list[list[loc]] duplicateList = getDupList(fileLineAsts, locsAtInt);
    iprint(duplicateList);
    return duplicateList;
}

public map[int, list[loc]] calculateLocationsOfNodeTypes(Monster fileLineAsts){
	map[int, list[loc]] registry = ();
	for(location <- fileLineAsts){
		map[int, list[tuple[int code, list[value] valueList]]] fileLines = fileLineAsts[location];
		for(lineNumber <- fileLines){
			list[tuple[int code, list[value] valueList]] stuffOnLine = fileLines[lineNumber];
			int stuffSize = size(stuffOnLine);
			int firstElementCode = head(stuffOnLine).code;
			loc l = |unknown:///|(0,0,<0,0>,<0,0>);
			l.uri = location.uri;
			l.end.line = lineNumber;
			l.begin.line = lineNumber;
			registry = addTo(registry, (stuffSize * 100) + firstElementCode, l);
		}
	}
	return registry;
}

public map[int, list[loc]] addTo(map[int, list[loc]] numberMap, int codeNumber, loc l){
	if(codeNumber in numberMap)
		numberMap[codeNumber] += l;
	else numberMap[codeNumber] = [l];
	return numberMap;
}

public Monster fileLineMapGeneration(int t, list[Declaration] asts) {
	Monster fileLineAsts = ();
	for (m <- asts)
		fileLineAsts[m.src] = getLocLineAst(t, m);
	return fileLineAsts;
}

public map[int, list[tuple[int, list[value]]]] getLocLineAst(int t, Declaration location) {
	map[int, list[tuple[int, list[value]]]] astMap = (); 
	top-down visit (location) {
        case Declaration d: astMap = addToASTMap(t, astMap, d);
		case Statement s: astMap = addToASTMap(t, astMap, s);
	 	case Expression e: astMap = addToASTMap(t, astMap, e);
    }
	return astMap;
}

public map[int, list[tuple[int, list[value]]]] addToASTMap(int t, map[int, list[tuple[int, list[value]]]] astMap, node n){
	loc location = getSrc(n);
	print("VISIT ");
	println(location);
	if(location!=|unknown:///|){
		tuple[int, list[value]] values = getComparables(n, t);
		astMap = addToMap(astMap, location.begin.line, values);
		if(location.begin.line!=location.end.line)
			astMap = addToMap(astMap, location.end.line, values);
	}
	return astMap;
}

public map[int, list[tuple[int, list[value]]]] addToMap(map[int, list[tuple[int, list[value]]]] astMap, int line, tuple[int, list[value]] n){
	if(line in astMap)
		astMap[line] += n;
	else astMap[line] = [n];
	return astMap;
}

public list[list[loc]] getDupList(Monster fileLineAsts, map[int, list[loc]] locsAtInt){
	list[list[loc]] dupList = []; 
	for(location <- fileLineAsts){
		list[loc] potentialDuplicates = [];
		map[int, list[tuple[int code, list[value] valueList]]] fileLines = fileLineAsts[location];
		for(lineNumber <- sort(domain(fileLines))){
			list[tuple[int code, list[value] valueList]] stuffOnLine = fileLines[lineNumber];
			int stuffSize = size(stuffOnLine);
			int firstElementCode = head(stuffOnLine).code;
			list[loc] dupLines = locsAtInt[(stuffSize * 100) + firstElementCode];
			list[loc] newPotentialDuplicates = [];
			for(potDupNew <- dupLines){ //abc
				bool partOfChain = false;
				for(potDupOld <- potentialDuplicates){//xyz
					if(potDupNew.uri == potDupOld.uri && potDupOld.end.line+1 == potDupNew.begin.line){
						potDupNew.begin.line = potDupOld.begin.line;
						newPotentialDuplicates += potDupNew;
						partOfChain = true;
					}
				}
				if(!partOfChain)
					newPotentialDuplicates+=potDupNew;
			}
			dupList = populateBeforeRemoval(dupList, potentialDuplicates, newPotentialDuplicates, location, lineNumber);
			potentialDuplicates = newPotentialDuplicates;
		}
	}
	return dupList;
}

public list[list[loc]] populateBeforeRemoval(list[list[loc]] dupList, list[loc] potentialDuplicates, list[loc] newPotentialDuplicates, loc location, int lineNumber){
	map[int, list[loc]] finalizedDups = ();
	for(dup <- potentialDuplicates, getSourceLength(dup)>=6, willBeRemoved(dup, newPotentialDuplicates)){
		int srcLength = getSourceLength(dup);
		if(srcLength in finalizedDups){
			finalizedDups[srcLength]+=[dup];
		} else {
			location.end.line = lineNumber-1;
			location.begin.line = lineNumber-srcLength-1;
			finalizedDups[srcLength]=[location, dup];
		}
	}
	for(dup <- finalizedDups){
		dupList+=finalizedDups[dup];
	}
	return dupList;
}

public bool willBeRemoved(loc dup, list[loc] newPotentialDuplicates){
	for(pot <- newPotentialDuplicates)
		if(pot.uri == dup.uri && pot.begin.line == dup.begin.line)
			return false;
	return true;
}

public int getSourceLength(loc l){
	return l.end.line-l.begin.line+1;
}

public loc getSrc(value ast) {
	switch (ast) {
        case Declaration d: return d.src;
		case Statement d: return d.src;
	 	case Expression d: return d.src;
	}
}

tuple[int,list[value]] getComparables(node n, int t){
	if(t == 3){
		switch(n){
			case Statement d: return <98, []>;
		}
	}
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
    }
    return <99, []>;
}