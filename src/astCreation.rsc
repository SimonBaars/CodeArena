module astCreation

import IO;
import Set;
import Map;
import List;
import Node;
import String;
import lang::java::m3::Core;
import lang::java::m3::AST;
import util::Math;

int minAmountOfLines = 6;

alias LineRegistry = map[loc, map[int, list[value]]];
// = map[fileloc, map[regelnummer, wat er aan ast op de regel staat]]

public list[tuple[int, list[loc]]] getDuplication(int t, list[Declaration] asts) {
    LineRegistry fileLineAsts = fileLineMapGeneration(t, asts);
    tuple[map[int, list[loc]] locRegistries, map[str, list[int]] sortedDomains] nodeRegs = calculateLocationsOfNodeTypes(fileLineAsts);
    map[int, list[loc]] locsAtHash = nodeRegs.locRegistries;
    // alle locs die bij een hash horen 
    map[str, list[int]] sortedDomains = nodeRegs.sortedDomains;
    // loc met alle regels op volgorde
    return getDupList(fileLineAsts, locsAtHash, sortedDomains);
}

public tuple[map[int, list[loc]] locRegistries,map[str, list[int]] sortedDomains] calculateLocationsOfNodeTypes(LineRegistry fileLineAsts){
	map[int, list[loc]] registry = ();
	map[str, list[int]] sortedDomains = ();
	for(location <- fileLineAsts){
		map[int, list[value]] fileLines = fileLineAsts[location];
		for(lineNumber <- fileLines){
			list[value] stuffOnLine = fileLines[lineNumber];
			loc l = |unknown:///|(0,0,<0,0>,<0,0>);
			l.uri = location.uri;
			l.end.line = lineNumber;
			l.begin.line = lineNumber;
			//println("Line <lineNumber> of file <indexOf(sort(domain(fileLineAsts)), location)> has hash <makeHashOfLine(stuffOnLine)>");
			//println(stuffOnLine);
			registry = addTo(registry, makeHashOfLine(stuffOnLine), l);
		}
		sortedDomains[location.uri] = sort(domain(fileLines));
	}
	return <registry, sortedDomains>;
}

public int makeHashOfLine(list[value] lines){
	int hash = 7;
	for(lineVal <- lines) {
		switch(lineVal){
			case int n: hash += hash*31 + n;
			case str n: hash = doHash(hash, n);
			case node n: hash = doHash(hash, toString(n));
		}
	}
	return hash;
}

public int doHash(int hash, str string){
	for(int j <- [0 .. size(string)])
		hash += hash*31 + charAt(string, j);
	return hash;
}

public map[int, list[loc]] addTo(map[int, list[loc]] numberMap, int codeNumber, loc l){
	if(codeNumber in numberMap)
		numberMap[codeNumber] += l;
	else numberMap[codeNumber] = [l];
	return numberMap;
}

public LineRegistry fileLineMapGeneration(int t, list[Declaration] asts) {
	LineRegistry fileLineAsts = ();
	for (m <- asts)
		fileLineAsts[m.src] = getLocLineAst(t, m);
	return fileLineAsts;
}

public map[int, list[value]] getLocLineAst(int t, Declaration location) {
	map[int, list[value]] astMap = (); 
	top-down visit (location) {
        case Declaration d: astMap = addToASTMap(t, astMap, d);
		case Statement s: astMap = addToASTMap(t, astMap, s);
	 	case Expression e: astMap = addToASTMap(t, astMap, e);
    }
	return astMap;
}

public map[int, list[value]] addToASTMap(int t, map[int, list[value]] astMap, node n){
	loc location = getSrc(n);
	//print("VISIT ");
	//println(location);
	if(location!=|unknown:///|){
		list[value] values = getComparables(n, t);
		astMap = addToMap(astMap, location.begin.line, values);
		if(location.begin.line!=location.end.line)
			astMap = addToMap(astMap, location.end.line, [0]);
	}
	return astMap;
}

public map[int, list[value]] addToMap(map[int, list[value]] astMap, int line, list[value] n){
	if(line in astMap)
		astMap[line] += n;
	else astMap[line] = n;
	return astMap;
}

public list[tuple[int, list[loc]]] getDupList(LineRegistry fileLineAsts, map[int, list[loc]] locsAtHash, map[str, list[int]] sortedDomains){
	list[tuple[int, list[loc]]] dupList = [];
	list[str] parsedURIs = [];
	int totalLines = 0;
	for(loc location <- fileLineAsts){
		println("Currently working on: <location>");
		list[tuple[int lines, loc duplicate]] potentialDuplicates = []; // list[<aantal regels, regel die duplicate hash heeft>]
		map[int, list[value]] fileLines = fileLineAsts[location];
		//fileLineAsts = map[fileloc, map[regelnummer, wat er aan ast op de regel staat]]
		//fileLines = map[regelnummer, wat er aan ast op de regel staat]]
		list[int] sortedDomain = sortedDomains[location.uri];
		int sortedDomainSize = size(sortedDomain);
		totalLines += sortedDomainSize;
		int i = 0;
		while(i < sortedDomainSize){
			if(i+5<sortedDomainSize && size(potentialDuplicates) == 0){
				int futureRes = inspectFutureDups(i, locsAtHash, sortedDomain, fileLines);
				if(futureRes != -1){
					potentialDuplicates = [];
					i+=futureRes+1;
					continue;
				}
			}
			int lineNumber = sortedDomain[i];
			list[value] stuffOnLine = fileLines[lineNumber];
			list[loc] dupLines = locsAtHash[makeHashOfLine(stuffOnLine)];
			//iprintln("line <lineNumber>, file <location>, stuffFound = <dupLines>");
			list[tuple[int lines, loc duplicate]] newPotentialDuplicates = [];
			map[str, tuple[int lines, loc duplicate]] reg1 = ();
			
			for(tuple[int lines, loc duplicate] potDupOld <- potentialDuplicates){
				reg1["<potDupOld.duplicate.uri><potDupOld.duplicate.end.line>"] = potDupOld;
			}
			for(loc potDupNew <- dupLines){
				if(potDupNew.uri notin parsedURIs && (potDupNew.uri != location.uri || potDupNew.begin.line > lineNumber)){
					bool partOfChain = false;
					str searchKey = "<potDupNew.uri><sortedDomains[potDupNew.uri][indexOf(sortedDomains[potDupNew.uri], potDupNew.begin.line)-1]>";
					if(searchKey in reg1){
						tuple[int lines, loc duplicate] potDupOld = reg1[searchKey];
						potDupNew.begin.line = potDupOld.duplicate.begin.line;
						newPotentialDuplicates += <potDupOld.lines+1, potDupNew>;
						//println("We upped thing to <potDupOld.lines+1>");
						partOfChain = true;
					}
					if(!partOfChain)
						newPotentialDuplicates+=<1, potDupNew>;
				}
			}
			dupList = populateBeforeRemoval(dupList, potentialDuplicates, newPotentialDuplicates, sortedDomain, location, lineNumber, lineNumber == last(sortedDomain));
			potentialDuplicates = newPotentialDuplicates;
			//iprintln("line = <lineNumber>, newPotDup = <newPotentialDuplicates>");
			i+=1;
		}
		parsedURIs += location.uri;
	}
	//- totaal aantal lines printen?
	//- % duplicated lines dan ook printen?
	println(totalLines);
	return dupList;
}

public int inspectFutureDups(int i, map[int, list[loc]] locsAtHash, list[int] sortedDomain, map[int, list[value]] fileLines){
	for(int j <- [0..minAmountOfLines]){
		if(size(locsAtHash[makeHashOfLine(fileLines[sortedDomain[i+j]])])<=1){
			return j;
		}
	}
	return -1;
}

public list[tuple[int, list[loc]]] populateBeforeRemoval(list[tuple[int, list[loc]]] dupList, list[tuple[int lines, loc duplicate]] potentialDuplicates, list[tuple[int lines, loc duplicate]] newPotentialDuplicates, list[int] sortedDomain, loc location, int lineNumber, bool isLast){
	map[int, list[loc]] finalizedDups = ();
	for(tuple[int lines, loc duplicate] potDup <- potentialDuplicates, potDup.lines>=minAmountOfLines){
		if(potDup.lines notin finalizedDups){
			loc l = |unknown:///|(0,0,<0,0>,<0,0>);
			l.uri = location.uri;
			l.end.line = sortedDomain[indexOf(sortedDomain, lineNumber)-1];
			l.begin.line = sortedDomain[indexOf(sortedDomain, lineNumber)-potDup.lines];
			finalizedDups[potDup.lines] = [l];
			newPotentialDuplicates+=<minAmountOfLines, l>;
		}
		finalizedDups[potDup.lines] += potDup.duplicate;
	}
	//iprintln(finalizedDups);
	list[tuple[int, list[loc]]] temp = [*[<finDup, finalizedDups[finDup]>] | finDup <- finalizedDups, isLast || any(loc aDup <- finalizedDups[finDup], willBeRemoved(aDup, newPotentialDuplicates)), !(any(tuple[int, list[loc]] aDup <- dupList, finalizedDups[finDup] <= aDup[1]))];
	if(size(temp) > 0){
		str buffer = toString(temp);
		println(size(buffer));
		println(buffer);
		dupList += temp;
	}
	//println("size = <size(dupList)>, potDups = <potentialDuplicates>");
	return dupList;
}

public bool willBeRemoved(loc dup, list[tuple[int lines, loc duplicate]] newPotentialDuplicates){
	for(pot <- newPotentialDuplicates)
		if(pot.duplicate.uri == dup.uri && pot.duplicate.begin.line == dup.begin.line)
			return false;
	//println("Will be removed <dup> in <newPotentialDuplicates>");
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

list[value] getComparables(node n, int t){
	if(t == 3){
		switch(n){
			case Statement d: return [0];
		}
	}
    switch(n){
    //Decls
        case \compilationUnit(list[Declaration] imports, list[Declaration] types) : return [1];
	    case \compilationUnit(Declaration package, list[Declaration] imports, list[Declaration] types) : return [2];
	    case \enum(str name, list[Type] implements, list[Declaration] constants, list[Declaration] body) : return [3] + (t==1?[name]:[]);
	    case \enumConstant(str name, list[Expression] arguments, Declaration class) : return [4] + (t == 1 ? [name] : []);
	    case \enumConstant(str name, list[Expression] arguments) : return [5] + (t == 1 ? [name] : []);
	    case \class(str name, list[Type] extends, list[Type] implements, list[Declaration] body) : return [6] + (t == 1 ? [name] : []);
	    case \class(list[Declaration] body) : return [7];
	    case \interface(str name, list[Type] extends, list[Type] implements, list[Declaration] body) : return [8] + (t == 1 ? [name] : []);
	    case \field(Type \type, list[Expression] fragments) : return [9] + extractType(\type, t);
	    case \initializer(Statement initializerBody) : return [10];
	    case \method(Type \return, str name, list[Declaration] parameters, list[Expression] exceptions, Statement impl) : return [11] + extractType(\return, t) + (t == 1 ? [name] : []); 
	    case \method(Type \return, str name, list[Declaration] parameters, list[Expression] exceptions) : return [12] + extractType(\return, t) + (t == 1 ? [name] : []);
	    case \constructor(str name, list[Declaration] parameters, list[Expression] exceptions, Statement impl) : return [13] + (t == 1 ? [name] : []);
	    case \import(str name) : return [14] + (t == 1 ? [name] : []);
	    case \package(str name) : return [15] + (t == 1 ? [name] : []);
	    case \package(Declaration parentPackage, str name) : return [16] + (t == 1 ? [name] : []);
	    case \variables(Type \type, list[Expression] \fragments) : return [17] + extractType(\type, t);
	    case \typeParameter(str name, list[Type] extendsList) : return [18] + (t == 1 ? [name] : []);
	    case \annotationType(str name, list[Declaration] body) : return [19] + (t == 1 ? [name] : []);
	    case \annotationTypeMember(Type \type, str name) : return [20] + extractType(\type, t) + (t == 1 ? [name] : []);
	    case \annotationTypeMember(Type \type, str name, Expression defaultBlock) : return [21] + extractType(\type, t) + (t == 1 ? [name] : []);
	    case \parameter(Type \type, str name, int extraDimensions) : return [22, extraDimensions] + extractType(\type, t) + (t == 1 ? [name] : []);
	    case \vararg(Type \type, str name) : return [23] + extractType(\type, t) + (t == 1 ? [name] : []);
	   
    ////Exprs
	   case \arrayAccess(Expression array, Expression index) : return [24];
	   case \newArray(Type \type, list[Expression] dimensions, Expression init) : return [25] + extractType(\type, t);
	   case \newArray(Type \type, list[Expression] dimensions) : return [26] + extractType(\type, t);
	   case \arrayInitializer(list[Expression] elements) : return [27];
	   case \assignment(Expression lhs, str operator, Expression rhs) : return [28, operator];
	   case \cast(Type \type, Expression expression) : return [29] + extractType(\type, t);
	   case \characterLiteral(str charValue) : return [30, charValue];
	   case \newObject(Expression expr, Type \type, list[Expression] args, Declaration class) : return [31] + extractType(\type, t);
 	   case \newObject(Expression expr, Type \type, list[Expression] args) : return [32] + extractType(\type, t);
	   case \newObject(Type \type, list[Expression] args, Declaration class) : return [33] + extractType(\type, t);
	   case \newObject(Type \type, list[Expression] args) : return [34] + extractType(\type, t);
	   case \qualifiedName(Expression qualifier, Expression expression) : return [35];
	   case \conditional(Expression expression, Expression thenBranch, Expression elseBranch) : return [36];
	   case \fieldAccess(bool isSuper, Expression expression, str name) : return [37, isSuper] + (t == 1 ? [name] : []);
	   case \fieldAccess(bool isSuper, str name) : return [38, isSuper] + (t == 1 ? [name] : []);
	   case \instanceof(Expression leftSide, Type rightSide) : return [39] + extractType(rightSide, t);
	   case \methodCall(bool isSuper, str name, list[Expression] arguments) : return [40, isSuper] + (t == 1 ? [name] : []);
	   case \methodCall(bool isSuper, Expression receiver, str name, list[Expression] arguments) : return [41, isSuper] + (t == 1 ? [name] : []);
	   //case \null() : return [42];
	   case \number(str numberValue) : return [43, numberValue];
	   case \booleanLiteral(bool boolValue) : return  [44, boolValue];
	   case \stringLiteral(str stringValue) : return [45, stringValue];
	   case \type(Type \type) : return [46] + extractType(\type, t);
	   case \variable(str name, int extraDimensions) : return [47, extraDimensions] + (t == 1 ? [name] : []);
	   case \variable(str name, int extraDimensions, Expression \initializer) : return [48, extraDimensions] + (t == 1 ? [name] : []);
	   case \bracket(Expression expression) : return [49];
	   case \this() : return [50];
	   case \this(Expression thisExpression) : return [51];
	   case \super() : return [52];
	   case \declarationExpression(Declaration declaration) : return [53];
	   case \infix(Expression lhs, str operator, Expression rhs) : return  [54, operator];
	   case \postfix(Expression operand, str operator) : return [55, operator];
	   case \prefix(str operator, Expression operand) : return [56, operator];
	   case \simpleName(str name) : return [57] + (t == 1 ? [name] : []);
	   case \markerAnnotation(str typeName) : return [58, typeName];
	   case \normalAnnotation(str typeName, list[Expression] memberValuePairs) : return [59, typeName];
	   case \memberValuePair(str name, Expression \value) : return [60] + (t == 1 ? [name] : []);
	   case \singleMemberAnnotation(str typeName, Expression \value) : return [61, typeName];
    //
    ////Statements
	   case \assert(Expression expression) : return [62];
	   case \assert(Expression expression, Expression message) : return [63];
	   case \block(list[Statement] statements) : return [64];
	   case \break() : return [65];
	   case \break(str label) : return [66, label];
	   case \continue() : return [67];
	   case \continue(str label) : return [68, label];
	   case \do(Statement body, Expression condition) : return [69];
	   case \empty() : return [70];
	   case \foreach(Declaration parameter, Expression collection, Statement body) : return [71];
	   case \for(list[Expression] initializers, Expression condition, list[Expression] updaters, Statement body) : return [72];
	   case \for(list[Expression] initializers, list[Expression] updaters, Statement body) : return [73];
	   case \if(Expression condition, Statement thenBranch) : return [74];
	   case \if(Expression condition, Statement thenBranch, Statement elseBranch) : return [75];
	   case \label(str name, Statement body) : [76] + (t == 1 ? [name] : []);
	   case \return(Expression expression) : return [77];
	   case \return() : return [78];
	   case \switch(Expression expression, list[Statement] statements) : return [79];
	   case \case(Expression expression) : return [80];
	   case \defaultCase() : return [81];
	   case \synchronizedStatement(Expression lock, Statement body) : return [82];
	   case \throw(Expression expression) : return [83];
	   case \try(Statement body, list[Statement] catchClauses) : return [84];
	   case \try(Statement body, list[Statement] catchClauses, Statement \finally) : return [85];                                  
	   case \catch(Declaration exception, Statement body) : return [86];
	   case \declarationStatement(Declaration declaration) : return [87];
	   case \while(Expression condition, Statement body) : return [88];
	   case \expressionStatement(Expression stmt) : return [89];
	   case \constructorCall(bool isSuper, Expression expr, list[Expression] arguments) : return [90, isSuper];
	   case \constructorCall(bool isSuper, list[Expression] arguments) : return [91, isSuper];
    }
    return [127];
}

public list[value] extractType(Type t, int ty){
	switch(t){
		case arrayType(Type \type): return [109] + extractType(\type, ty);
		case parameterizedType(Type \type): return [108] + extractType(\type, ty);
    	case qualifiedType(Type qualifier, Expression simpleName): return [107] + extractType(qualifier, ty);
    	case simpleType(Expression name): return [106] + getComparables(name, ty);
    	case unionType(list[Type] types): return [105] + [extractType(teip, ty) | teip <- types];
    	case wildcard(): return [104];
    	case upperbound(Type \type): return [103] + extractType(\type, ty);
    	case lowerbound(Type \type): return [102] + extractType(\type, ty);
    	case \int(): return [101];
   		case short(): return [100];
    	case long(): return [99];
    	case float(): return [98];
    	case double(): return [97];
    	case char(): return [96];
    	case string(): return [95];
    	case byte(): return [94];
    	case \void(): return [93];
    	case \boolean(): return [92];
	}
	return [42]; // The answer to life, the universe and everything
}