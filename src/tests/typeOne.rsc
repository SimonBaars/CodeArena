module tests::typeOne

import IO;
import Set;
import List;
import lang::java::m3::Core;
import lang::java::m3::AST;
import astCreation;
import String;

test bool duplicateTestNoDuplicates(){
	return(size(getDuplication(1, createAstsFromDirectory(|file:///home/sander/.clone/projects/tests/ProjectWithoutDuplicate|, false), 0.00)) == 0);
}

test bool duplicateTestDuplicatesWithinFile(){
	list[tuple[int, list[loc]]] dupList = getDuplication(1, createAstsFromDirectory(|file:///home/sander/.clone/projects/tests/ProjectWithDuplicateWithinOneFile|, false), 0.00);
	return(size(dupList) == 1 && size(dupList[0][1]) == 2);
}

test bool duplicateTestThreeDuplicatesBetweenFiles(){
	list[tuple[int, list[loc]]] dupList = getDuplication(1, createAstsFromDirectory(|file:///home/sander/.clone/projects/tests/ProjectWithThreeDuplicatesBetweenFiles|, false), 0.00);
	return(size(dupList) == 1 && size(dupList[0][1]) == 3);
}

test bool duplicateTestDuplicatesBetweenFiles(){
	list[tuple[int, list[loc]]] dupList = getDuplication(1, createAstsFromDirectory(|file:///home/sander/.clone/projects/tests/ProjectWithDuplicateBetweenFiles|, false), 0.00);
	return(size(dupList) == 1 && size(dupList[0][1]) == 2);
}

test bool duplicateTestDuplicatesBetweenThreeFiles(){
	list[tuple[int, list[loc]]] dupList = getDuplication(1, createAstsFromDirectory(|file:///home/sander/.clone/projects/tests/ProjectWithDuplicateBetweenThreeFiles|, false), 0.00);
	return(size(dupList) == 1 && size(dupList[0][1]) == 3);
}

test bool duplicateTestFourDuplicatesBetweenFiles(){
	list[tuple[int, list[loc]]] dupList = getDuplication(1, createAstsFromDirectory(|file:///home/sander/.clone/projects/tests/ProjectWithFourDuplicatesBetweenFiles|, false), 0.00);
	return(size(dupList) == 1 && size(dupList[0][1]) == 4);
}

test bool duplicateTestSubDuplicatesBetweenFiles(){
	list[tuple[int, list[loc]]] dupList = getDuplication(1, createAstsFromDirectory(|file:///home/sander/.clone/projects/tests/ProjectWithSubDuplicatesBetweenFiles|, false), 0.00);
	return(size(dupList) == 2 && size(dupList[0][1]) == 2 && size(dupList[1][1]) == 3);
}
 
