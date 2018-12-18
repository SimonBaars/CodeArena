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
import DateTime;

int minAmountOfLines = 6;

alias LineRegistry = map[str, map[int, list[value]]];
map[str, list[int]] countedLines = ();
// = map[fileloc, map[regelnummer, wat er aan ast op de regel staat]]

public list[tuple[int, list[loc]]] getDuplication(int t, set[Declaration] asts, real similarityPercentage) {
	countedLines = ();
    LineRegistry fileLineAsts = fileLineMapGeneration(t, asts);
    set[str] filesOrder = domain(fileLineAsts);
    tuple[map[int, list[loc]] locRegistries, map[str, list[int]] sortedDomains, map[int, int] hashStartIndex, map[str, map[int, int]] hashMap] nodeRegs = calculateLocationsOfNodeTypes(fileLineAsts, filesOrder, t, similarityPercentage);
    map[int, list[loc]] locsAtHash = nodeRegs.locRegistries;
    // alle locs die bij een hash horen 
    map[str, list[int]] sortedDomains = nodeRegs.sortedDomains;
    map[int, int] hashStartIndex = nodeRegs.hashStartIndex;
    map[str, map[int, int]] hashMap = nodeRegs.hashMap;
    // loc met alle regels op volgorde
    //list[tuple[int, list[loc]]] testDupList = getDupList(hashMap, locsAtHash, sortedDomains, hashStartIndex, filesOrder);
    //println("TESTDUPLIST:::");
    //iprintln(testDupList);
    //println(size(testDupList));
    //println(size(testDupList[0][1]));
    //println(size(testDupList[1][1]));
    return getDupList(hashMap, locsAtHash, sortedDomains, hashStartIndex, filesOrder);
}

public tuple[map[int, list[loc]] locRegistries, map[str, list[int]] sortedDomains, map[int, int] hashStartIndex, map[str, map[int, int]] hashMap] calculateLocationsOfNodeTypes(LineRegistry fileLineAsts, set[str] filesOrder, int t, real similarityPercentage){
	map[int, list[loc]] registry = ();
	map[str, list[int]] sortedDomains = ();
	map[int, int] hashStartIndex = ();
	map[str, map[int, int]] hashMap = ();
	for(str location <- filesOrder){
		map[int, list[value]] fileLines = fileLineAsts[location];
		sortedDomains[location] = sort(domain(fileLines));
		hashMap[location] = ();
		for(int i <- [0..size(fileLines)]){
			list[value] stuffOnLine = fileLines[sortedDomains[location][i]];
			loc l = |unknown:///|(0,0,<0,0>,<0,0>);
			l.uri = location;
			l.end.line = i;
			l.begin.line = i;
			//println("Line <lineNumber> of file <indexOf(sort(domain(fileLineAsts)), location)> has hash <makeHashOfLine(stuffOnLine)>");
			//println(stuffOnLine);
			int hash = makeHashOfLine(stuffOnLine);
			if(t == 3)
				registry = calculateType3Hash(l, location, i, fileLineAsts, hashMap, filesOrder, sortedDomains, similarityPercentage, stuffOnLine, registry);
			registry = addTo(registry, hash, l);
			hashStartIndex[hash] = 0;
			hashMap[location][i] = hash;
		}
	}
	return <registry, sortedDomains, hashStartIndex, hashMap>;
}

public map[int, list[loc]] calculateType3Hash(loc thisLoc, str location, int i, LineRegistry fileLineAsts, map[str, map[int, int]] hashMap, set[str] filesOrder, map[str, list[int]] sortedDomains, real similarityPercentage, list[value] curLineContent, map[int, list[loc]] registry){
	for(str l <- filesOrder){
		map[int, list[value]] fileLines = fileLineAsts[l];
		for(int j <- [0..size(fileLines)]){
			if(l == location && i == j)
				return registry;
			//println("l <l> j <j>");
			list[value] stuffOnLine = fileLines[sortedDomains[l][j]];
			real similarity = calculateSimilarity(curLineContent, stuffOnLine);
			//println(similarity);
			if(similarity<=similarityPercentage && similarityPercentage != 0.00){
				int hash = hashMap[l][j];
				registry = addTo(registry, hash, thisLoc);
			}
		}
	}
	return registry;
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

public LineRegistry fileLineMapGeneration(int t, set[Declaration] asts) {
	LineRegistry fileLineAsts = ();
	for (m <- asts)
		fileLineAsts[m.src.uri] = getLocLineAst(t, m);
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

public list[tuple[int, list[loc]]] getDupList(map[str, map[int, int]] hashMap, map[int, list[loc]] locsAtHash, map[str, list[int]] sortedDomains, map[int, int] hashStartIndex, set[str] filesOrder){
	list[tuple[int, list[loc]]] dupList = [];
	for(str location <- filesOrder){
		map[int,int] curFilesHashes = hashMap[location];
		list[loc] potentialDuplicates = [];
		list[int] sortedDomain = sortedDomains[location];
		int sortedDomainSize = size(sortedDomain);
		if(sortedDomainSize!=0)
			println(sortedDomainSize);
		int i = 0;
		while(i < sortedDomainSize){			
			if(i+5<sortedDomainSize && size(potentialDuplicates) == 0){
				tuple[int skipAmount, map[int, int] hashStartIndex] futureRes = inspectFutureDups(i, locsAtHash, curFilesHashes, hashStartIndex);
				if(futureRes.skipAmount != -1){
					potentialDuplicates = [];
					i+=futureRes.skipAmount+1;
					hashStartIndex = futureRes.hashStartIndex;
					continue;
				}
			}
			int hash = curFilesHashes[i];
			hashStartIndex[hash] = hashStartIndex[hash] + 1;
			list[loc] dupLines = locsAtHash[hash];
			list[loc] newPotentialDuplicates = [];
			map[str, list[loc]] potentialDuplicateRegistry = ();
			for(loc potDupOld <- potentialDuplicates){
				str key = "<potDupOld.uri><potDupOld.end.line>";
				if(key in potentialDuplicateRegistry){
					potentialDuplicateRegistry[key]+=potDupOld;
				} else {
					potentialDuplicateRegistry[key] = [potDupOld];
				}
				
			}

			for(int j <- [hashStartIndex[hash]..size(dupLines)]){
				loc potDupNew = dupLines[j];
				list[int] thisDomain = sortedDomains[potDupNew.uri];
				str searchKey = "<potDupNew.uri><potDupNew.begin.line-1>";
				if(searchKey in potentialDuplicateRegistry){
					list[loc] potDupOldList = potentialDuplicateRegistry[searchKey];
					for(loc potDupOld <- potDupOldList) {
						loc l = |unknown:///|(0,0,<0,0>,<0,0>);
						l.uri = potDupNew.uri;
						l.end.line = potDupNew.end.line;
						l.begin.line = potDupOld.begin.line;
						if(l notin newPotentialDuplicates)
							newPotentialDuplicates += l;
					}
				}
				newPotentialDuplicates+=potDupNew;
			}
			dupList = populateBeforeRemoval(dupList, potentialDuplicates, newPotentialDuplicates, sortedDomains, sortedDomain, location, i, i == size(sortedDomain)-1);
			potentialDuplicates = newPotentialDuplicates;
			i+=1;
		}
		println(0); //End of loc
	}
	println(0); //EOF
	return dupList;
}

public tuple[int skipAmount, map[int, int] hashStartIndex] inspectFutureDups(int i, map[int, list[loc]] locsAtHash, map[int, int] curFilesHashes, map[int, int] hashStartIndex){
	for(int j <- [0..minAmountOfLines]){
		int hash = curFilesHashes[i+j];
		hashStartIndex[hash] = hashStartIndex[hash] + 1;
		if(size(locsAtHash[hash])<=hashStartIndex[hash]){
			return <j, hashStartIndex>;
		}
	}
	return <-1, ()>;
}

int amountOfLines(loc location){
	return location.end.line-location.begin.line+1;
}

public list[tuple[int, list[loc]]] populateBeforeRemoval(list[tuple[int, list[loc]]] dupList, list[loc] potentialDuplicates, list[loc] newPotentialDuplicates, map[str, list[int]] sortedDomains, list[int] sortedDomain, str location, int i, bool isLast){
	map[int, list[loc]] finalizedDups = ();
	for(loc potDup <- potentialDuplicates, amountOfLines(potDup)>=minAmountOfLines){
		int potDupLines = amountOfLines(potDup);
		if(potDupLines notin finalizedDups){
			loc l = |unknown:///|(0,0,<0,0>,<0,0>);
			l.uri = location;
			l.end.line = i-1;
			l.begin.line = i-potDupLines;
			finalizedDups[potDupLines] = [l];
			newPotentialDuplicates+=l;
		}
		finalizedDups[potDupLines] += potDup;
	}

	list[tuple[int, list[loc]]] temp = [];
	for(int amount <- sort(domain(finalizedDups), bool(int a, int b){ return a > b; })){
		list[loc] dupGroup = finalizedDups[amount];
		if((isLast || any(loc aDup <- dupGroup, willBeRemoved(aDup, newPotentialDuplicates))) && !any(tuple[int amount, list[loc] locList] aDup <- dupList, dupGroup <= aDup.locList) && !isSubElement(dupGroup, temp+dupList) && isOutsideOfRange(dupList, dupGroup)){
			dupList+=<amount, dupGroup>;
			for(int j <- [0..size(finalizedDups[amount])]){
				loc thisLoc = dupGroup[j];
				loc l = |unknown:///|(0,0,<0,0>,<0,0>);
				l.uri = thisLoc.uri;
				l.end.line = sortedDomains[thisLoc.uri][thisLoc.end.line];
				l.begin.line = sortedDomains[thisLoc.uri][thisLoc.begin.line];
				dupGroup[j] = l;
			}
			temp+=<amount, dupGroup>;
		}
	}

	if(size(temp) > 0){
		int duplicateLines = 0;
		for(tuple[int line, list[loc] locs] t <- temp){
			for(loc l <- t.locs){
				if(l.uri notin countedLines) countedLines[l.uri] = [];
				for(i <- [l.begin.line .. l.end.line+1]){
					if(i notin countedLines[l.uri]){
						countedLines[l.uri] += i;
						duplicateLines+=1;
					}
				}
			}
		}
		
		str buffer = toString(temp);
		println(size(buffer));
		println(duplicateLines);
		println(buffer);
	}
	//println("size = <size(dupList)>, potDups = <potentialDuplicates>");
	return dupList;
}

public bool isOutsideOfRange(list[tuple[int, list[loc]]] currDups, list[loc] dupGroup){
	//iprintln(currDups);
	//iprintln("WWW");
	//iprintln(dupGroup);
	return any(loc l <- dupGroup, isOutsideOfRangeCheck(l, currDups));
}

public bool isOutsideOfRangeCheck(loc l, list[tuple[int, list[loc]]] currDups){
	for(tuple[int amount, list[loc] locList] aDupList <- currDups){
		//println(l);
		//println("WWWW");
		//println(aDupList);
		if(any(loc aDup <- aDupList.locList, l.uri == aDup.uri && l.begin.line>=aDup.begin.line && l.end.line<=aDup.end.line)){
			return false;
		}
		else return true;
	}
	return true;
}

public bool isSubElement(list[loc] locList, list[tuple[int, list[loc]]] collectedDups){
	for(tuple[int lines, list[loc] locs] dup <- collectedDups){
		if(size (locList) == size(dup.locs)){
			return true;
		}
	}
	return false;
}

public bool willBeRemoved(loc dup, list[loc] newPotentialDuplicates){
	for(loc pot <- newPotentialDuplicates)
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

public real calculateSimilarity(list[value] line1, list[value] line2){
	int differentElements = size(line1 - line2) + size(line2 - line1);
	int combinedSize = size(line1) + size(line2);
	//println("differentElements <differentElements> combinedSize <combinedSize> line1 <line1> line2 <line2>");
	return toReal(differentElements)/toReal(combinedSize) * 100;
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