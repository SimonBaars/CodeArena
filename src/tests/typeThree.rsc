module tests::typeThree

import IO;
import Set;
import List;
import lang::java::m3::Core;
import lang::java::m3::AST;
import astCreation;
import String;

test bool duplicateTestNoDuplicates(){
	return(size(getDuplication(1, createAstsFromDirectory(|file:///home/sander/.clone/projects/tests/T1/WithoutDup|, false), 0.00)) == 0);
}

test bool duplicateTestDuplicatesWithinFile(){
	list[tuple[int, list[loc]]] dupList = getDuplication(1, createAstsFromDirectory(|file:///home/sander/.clone/projects/tests/T1/DupWithinOneFile|, false), 0.00);
	return(size(dupList) == 1 && size(dupList[0][1]) == 2);
}

test bool duplicateTestDuplicatesWithinFile(){
	list[tuple[int, list[loc]]] dupList = getDuplication(2, createAstsFromDirectory(|file:///home/sander/.clone/projects/tests/T2/TwoDupBetweenFiles|, false), 0.00);
	list[tuple[int, list[loc]]] dupList3 = getDuplication(3, createAstsFromDirectory(|file:///home/sander/.clone/projects/tests/T2/TwoDupBetweenFiles|, false), 0.00);
	return(dupList == dupList3);
}

//test bool duplicateTestThreeDuplicatesBetweenFiles(){
//	list[tuple[int, list[loc]]] dupList = getDuplication(1, createAstsFromDirectory(|file:///home/sander/.clone/projects/tests/T1/ThreeDupBetweenFiles|, false), 0.00);
//	return(size(dupList) == 1 && size(dupList[0][1]) == 3);
//}

//test bool duplicateTestDuplicatesBetweenFiles(){
//	list[tuple[int, list[loc]]] dupList = getDuplication(1, createAstsFromDirectory(|file:///home/sander/.clone/projects/tests/T1/TwoDupBetweenFiles|, false), 0.00);
//	return(size(dupList) == 1 && size(dupList[0][1]) == 2);
//}
//
//test bool duplicateTestDuplicatesBetweenThreeFiles(){
//	list[tuple[int, list[loc]]] dupList = getDuplication(1, createAstsFromDirectory(|file:///home/sander/.clone/projects/tests/T1/DupBetweenThreeFiles|, false), 0.00);
//	return(size(dupList) == 1 && size(dupList[0][1]) == 3);
//}
//
//test bool duplicateTestFourDuplicatesBetweenFiles(){
//	list[tuple[int, list[loc]]] dupList = getDuplication(1, createAstsFromDirectory(|file:///home/sander/.clone/projects/tests/T1/FourDupBetweenFiles|, false), 0.00);
//	return(size(dupList) == 2 && size(dupList[0][1]) == 2 && size(dupList[1][1]) == 2);
//}
//
//test bool duplicateTestSubDuplicatesBetweenFiles(){
//	list[tuple[int, list[loc]]] dupList = getDuplication(1, createAstsFromDirectory(|file:///home/sander/.clone/projects/tests/T1/SubDupBetweenFiles|, false), 0.00);
//	return(size(dupList) == 2 && size(dupList[0][1]) == 2 && size(dupList[1][1]) == 3);
//}
//
//test bool duplicateTestThreeDuplicatesWithinFiles(){
//	list[tuple[int, list[loc]]] dupList = getDuplication(1, createAstsFromDirectory(|file:///home/sander/.clone/projects/tests/T1/ThreeDupWithinOneFile|, false), 0.00);
//	return(size(dupList) == 1 && size(dupList[0][1]) == 3);
//} 
//
//test bool duplicateTestFourDuplicatesWithinFiles(){
//	list[tuple[int, list[loc]]] dupList = getDuplication(1, createAstsFromDirectory(|file:///home/sander/.clone/projects/tests/T1/FourDupWithinOneFile|, false), 0.00);
//	return(size(dupList) == 1 && size(dupList[0][1]) == 4);
//} 
//
//test bool duplicateTestHardDuplicatesBetweenThreeFiles(){
//	list[tuple[int, list[loc]]] dupList = getDuplication(1, createAstsFromDirectory(|file:///home/sander/.clone/projects/tests/T1/DiffDiffAstDups|, false), 0.00);
//	return(size(dupList) == 3);
//}

