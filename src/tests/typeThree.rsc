module tests::typeThree

import IO;
import Set;
import List;
import lang::java::m3::Core;
import lang::java::m3::AST;
import astCreation;
import String;

test bool duplicateTestNoDuplicates(){
	return(size(getDuplication(3, createAstsFromDirectory(|file:///home/sander/.clone/projects/tests/ProjectWithoutDuplicate|, false))) == 0);
}

test bool duplicateTestDuplicatesWithinFile(){
	return(size(getDuplication(3, createAstsFromDirectory(|file:///home/sander/.clone/projects/tests/ProjectWithDuplicateWithinOneFile|, false))[0][1]) == 2);
}

test bool duplicateTestThreeDuplicatesBetweenFiles(){
	return(size(getDuplication(3, createAstsFromDirectory(|file:///home/sander/.clone/projects/tests/ProjectWithThreeDuplicatesBetweenFiles|, false))[0][1]) == 3);
}

test bool duplicateTestDuplicatesBetweenFiles(){
	return(size(getDuplication(3, createAstsFromDirectory(|file:///home/sander/.clone/projects/tests/ProjectWithDuplicateBetweenFiles|, false))[0][1]) == 2);
}