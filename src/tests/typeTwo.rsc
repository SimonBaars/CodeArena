module tests::typeTwo

import IO;
import Set;
import List;
import lang::java::m3::Core;
import lang::java::m3::AST;
import astCreation;
import String;

test bool duplicateTestNoDuplicates(){
	return(size(getDuplication(2, createAstsFromDirectory(|file:///home/sander/.clone/projects/tests/T2/WithoutDup|, false), 0.00)) == 0);
}

test bool duplicateTestDuplicatesWithinFile(){
	list[tuple[int, list[loc]]] dupList = getDuplication(2, createAstsFromDirectory(|file:///home/sander/.clone/projects/tests/T2/DupWithinOneFile|, false), 0.00);
	return(size(dupList) == 1 && size(dupList[0][1]) == 2);
}

test bool duplicateTestThreeDuplicatesBetweenFiles(){
	list[tuple[int, list[loc]]] dupList = getDuplication(2, createAstsFromDirectory(|file:///home/sander/.clone/projects/tests/T2/ThreeDupBetweenFiles|, false), 0.00);
	return(size(dupList) == 1 && size(dupList[0][1]) == 3);
}

test bool duplicateTestDuplicatesBetweenFiles(){
	list[tuple[int, list[loc]]] dupList = getDuplication(2, createAstsFromDirectory(|file:///home/sander/.clone/projects/tests/T2/TwoDupBetweenFiles|, false), 0.00);
	return(size(dupList) == 1 && size(dupList[0][1]) == 2);
}

test bool duplicateTestDuplicatesBetweenThreeFiles(){
	list[tuple[int, list[loc]]] dupList = getDuplication(2, createAstsFromDirectory(|file:///home/sander/.clone/projects/tests/T2/DupBetweenThreeFiles|, false), 0.00);
	return(size(dupList) == 1 && size(dupList[0][1]) == 3);
}

test bool duplicateTestFourDuplicatesBetweenFiles(){
	list[tuple[int, list[loc]]] dupList = getDuplication(2, createAstsFromDirectory(|file:///home/sander/.clone/projects/tests/T2/FourDupBetweenFiles|, false), 0.00);
	return(size(dupList) == 2 && size(dupList[0][1]) == 2 && size(dupList[1][1]) == 2);
}

test bool duplicateTestSubDuplicatesBetweenFiles(){
	list[tuple[int, list[loc]]] dupList = getDuplication(2, createAstsFromDirectory(|file:///home/sander/.clone/projects/tests/T2/SubDupBetweenFiles|, false), 0.00);
	return(size(dupList) == 2 && size(dupList[0][1]) == 2 && size(dupList[1][1]) == 3);
}

test bool duplicateTestThreeDuplicatesWithinFiles(){
	list[tuple[int, list[loc]]] dupList = getDuplication(2, createAstsFromDirectory(|file:///home/sander/.clone/projects/tests/T2/ThreeDupWithinOneFile|, false), 0.00);
	return(size(dupList) == 1 && size(dupList[0][1]) == 3);
} 

test bool duplicateTestFourDuplicatesWithinFiles(){
	list[tuple[int, list[loc]]] dupList = getDuplication(2, createAstsFromDirectory(|file:///home/sander/.clone/projects/tests/T2/FourDupWithinOneFile|, false), 0.00);
	return(size(dupList) == 1 && size(dupList[0][1]) == 4);
} 

test bool duplicateTestHardDuplicatesBetweenThreeFiles(){
	list[tuple[int, list[loc]]] dupList = getDuplication(2, createAstsFromDirectory(|file:///home/sander/.clone/projects/tests/T2/DiffDiffAstDups|, false), 0.00);
	return(size(dupList) == 3);
}

