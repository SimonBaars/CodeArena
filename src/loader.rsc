module loader

import IO;
import Set;
import List;
import lang::java::m3::Core;
import lang::java::m3::AST;
import astCreation;
import String;

void calculateCodeDuplication(loc location){
	M3 model = createM3FromDirectory(location);
	map[loc, Declaration] astsMap = ();
	list[Declaration] asts = [];
	for (m <- model.containment, m[0].scheme == "java+compilationUnit"){
		Declaration ast = createAstFromFile(m[0], true);
		astsMap[m[0]] = ast;
		asts += ast;
	}
	
	list[tuple[int, list[loc]]] result = getDuplication(1, asts);
	str buffer = toString(result);
	println(size(buffer));
	iprintln(result);
}