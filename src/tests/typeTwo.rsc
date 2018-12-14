module tests::typeTwo

import IO;
import Set;
import List;
import lang::java::m3::Core;
import lang::java::m3::AST;
import astCreation;
import String;

test bool duplicateTestNoDuplicates(){
	M3 model = createM3FromDirectory(|file:///home/sander/.clone/projects/tests/ProjectWithoutDuplicate|);
	map[loc, Declaration] astsMap = ();
	list[Declaration] asts = [];
	for (m <- model.containment, m[0].scheme == "java+compilationUnit"){
		Declaration ast = createAstFromFile(m[0], true);
		astsMap[m[0]] = ast;
		asts += ast;
	}
	return(size(getDuplication(2, asts)) == 0);
}

test bool duplicateTestDuplicatesWithinFile(){
	M3 model = createM3FromDirectory(|file:///home/sander/.clone/projects/tests/ProjectWithT2DuplicateWithinOneFile|);
	map[loc, Declaration] astsMap = ();
	list[Declaration] asts = [];
	for (m <- model.containment, m[0].scheme == "java+compilationUnit"){
		Declaration ast = createAstFromFile(m[0], true);
		astsMap[m[0]] = ast;
		asts += ast;
	}
	return(size(getDuplication(2, asts)[0][1]) == 2);
}

test bool duplicateTestThreeDuplicatesBetweenFiles(){
	M3 model = createM3FromDirectory(|file:///home/sander/.clone/projects/tests/ProjectWithThreeDuplicatesBetweenFiles|);
	map[loc, Declaration] astsMap = ();
	list[Declaration] asts = [];
	for (m <- model.containment, m[0].scheme == "java+compilationUnit"){
		Declaration ast = createAstFromFile(m[0], true);
		astsMap[m[0]] = ast;
		asts += ast;
	}
	return(size(getDuplication(2, asts)[0][1]) == 3);
}

test bool duplicateTestDuplicatesBetweenFiles(){
	M3 model = createM3FromDirectory(|file:///home/sander/.clone/projects/tests/ProjectWithT2DuplicateBetweenFiles|);
	map[loc, Declaration] astsMap = ();
	list[Declaration] asts = [];
	for (m <- model.containment, m[0].scheme == "java+compilationUnit"){
		Declaration ast = createAstFromFile(m[0], true);
		astsMap[m[0]] = ast;
		asts += ast;
	}
	return(size(getDuplication(2, asts)) > 0);
}
