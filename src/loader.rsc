module loader

import IO;
import Set;
import List;
import lang::java::m3::Core;
import lang::java::m3::AST;
import lang::java::jdt::m3::Core;
import lang::java::jdt::m3::AST;
import astCreation;

void main() {
	M3 model = createM3FromEclipseProject(|project://ProjectWithDuplicateBetweenFiles|);
	map[loc, Declaration] astsMap = ();
	list[Declaration] asts = [];
	for (m <- model.containment, m[0].scheme == "java+compilationUnit"){
		Declaration ast = createAstFromFile(m[0], true);
		astsMap[m[0]] = ast;
		asts += ast;
	}
	
	getDuplication(asts);
}