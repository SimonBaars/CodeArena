module tests::typeThree

import IO;
import Set;
import List;
import lang::java::m3::Core;
import lang::java::m3::AST;
import astCreation;
import String;

/* Standard tests. */
test bool duplicateTestNoDuplicates(){
	return(size(getDuplication(1, createAstsFromDirectory(|file:///home/sander/.clone/projects/tests/T1/WithoutDup|, false), 0.00)) == 0);
}

test bool duplicateTestDuplicatesWithinFileZero(){
	list[tuple[int, list[loc]]] dupList = getDuplication(3, createAstsFromDirectory(|file:///home/sander/.clone/projects/tests/T1/DupWithinOneFile|, false), 0.00);
	return(size(dupList) == 1 && size(dupList[0][1]) == 2);
}

test bool duplicateTestEqualToTwo(){
	list[tuple[int, list[loc]]] dupList = getDuplication(2, createAstsFromDirectory(|file:///home/sander/.clone/projects/tests/T2/TwoDupBetweenFiles|, false), 0.00);
	list[tuple[int, list[loc]]] dupList3 = getDuplication(3, createAstsFromDirectory(|file:///home/sander/.clone/projects/tests/T2/TwoDupBetweenFiles|, false), 0.00);
	return(dupList == dupList3);
}

test bool duplicateTestNotEqualToTwo(){
	list[tuple[int, list[loc]]] dupList = getDuplication(2, createAstsFromDirectory(|file:///home/sander/.clone/projects/tests/T2/TwoDupBetweenFiles|, false), 0.00);
	list[tuple[int, list[loc]]] dupList3 = getDuplication(3, createAstsFromDirectory(|file:///home/sander/.clone/projects/tests/T2/TwoDupBetweenFiles|, false), 100.00);
	return(dupList != dupList3);
}

/* Percentage tests one. */
test bool duplicateTestDupsBetweenFilesPercs10(){
	list[tuple[int, list[loc]]] dupList = getDuplication(3, createAstsFromDirectory(|file:///home/sander/.clone/projects/tests/T3/TwoDupBetweenFiles|, false), 10.0);
	return(size(dupList) == 0);
}

test bool duplicateTestDupsBetweenFilesPercs25(){
	list[tuple[int, list[loc]]] dupList = getDuplication(3, createAstsFromDirectory(|file:///home/sander/.clone/projects/tests/T3/TwoDupBetweenFiles|, false), 25.0);
	return(size(dupList) == 0);
}

test bool duplicateTestDupsBetweenFilesPercs50(){
	list[tuple[int, list[loc]]] dupList = getDuplication(3, createAstsFromDirectory(|file:///home/sander/.clone/projects/tests/T3/TwoDupBetweenFiles|, false), 50.0);
	return(size(dupList) == 0);
}

test bool duplicateTestDupsBetweenFilesPercs75(){
	list[tuple[int, list[loc]]] dupList = getDuplication(3, createAstsFromDirectory(|file:///home/sander/.clone/projects/tests/T3/TwoDupBetweenFiles|, false), 75.0);
	return(size(dupList) == 1 && size(dupList[0][1]) == 2);
}

test bool duplicateTestDupsBetweenFilesPercs99(){
	list[tuple[int, list[loc]]] dupList = getDuplication(3, createAstsFromDirectory(|file:///home/sander/.clone/projects/tests/T3/TwoDupBetweenFiles|, false), 99.0);
	return(size(dupList) > 1);
}

test bool duplicateTestDupsBetweenFilesPercs100(){
	list[tuple[int, list[loc]]] dupList = getDuplication(3, createAstsFromDirectory(|file:///home/sander/.clone/projects/tests/T3/TwoDupBetweenFiles|, false), 100.0);
	return(size(dupList) == 1);
}


/* Percentage tests Two. */
test bool duplicateTestWithinFilePercs10(){
	list[tuple[int, list[loc]]] dupList = getDuplication(3, createAstsFromDirectory(|file:///home/sander/.clone/projects/tests/T3/DupWithinOneFile|, false), 10.0);
	return(size(dupList) == 0);
}

test bool duplicateTestWithinFilePercs25(){
	list[tuple[int, list[loc]]] dupList = getDuplication(3, createAstsFromDirectory(|file:///home/sander/.clone/projects/tests/T3/DupWithinOneFile|, false), 25.0);
	return(size(dupList) == 0);
}

test bool duplicateTestWithinFilePercs50(){
	list[tuple[int, list[loc]]] dupList = getDuplication(3, createAstsFromDirectory(|file:///home/sander/.clone/projects/tests/T3/DupWithinOneFile|, false), 50.0);
	return(size(dupList) == 0);
}

test bool duplicateTestWithinFilePercs75(){
	list[tuple[int, list[loc]]] dupList = getDuplication(3, createAstsFromDirectory(|file:///home/sander/.clone/projects/tests/T3/DupWithinOneFile|, false), 75.0);
	return(size(dupList) == 1 && size(dupList[0][1]) == 3);
}

test bool duplicateTestWithinFilePercs99(){
	list[tuple[int, list[loc]]] dupList = getDuplication(3, createAstsFromDirectory(|file:///home/sander/.clone/projects/tests/T3/DupWithinOneFile|, false), 99.0);
	return(size(dupList) > 1);
}

test bool duplicateTestWithinFilePercs100(){
	list[tuple[int, list[loc]]] dupList = getDuplication(3, createAstsFromDirectory(|file:///home/sander/.clone/projects/tests/T3/DupWithinOneFile|, false), 100.0);
	return(size(dupList) == 1 && size(dupList[0][1]) == 2);
}
