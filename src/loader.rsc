module loader

import IO;
import Set;
import List;
import lang::java::m3::Core;
import lang::java::m3::AST;
import astCreation;
import String;
import tests::typeOne;
import tests::typeTwo;
import tests::typeThree;

void calculateCodeDuplication(loc location){
	M3 model = createM3FromDirectory(location);
	map[loc, Declaration] astsMap = ();
	list[Declaration] asts = [];
	for (m <- model.containment, m[0].scheme == "java+compilationUnit"){
		Declaration ast = createAstFromFile(m[0], true);
		astsMap[m[0]] = ast;
		asts += ast;
	}
	getDuplication(2, asts);
}