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

void calculateCodeDuplication(list[loc] partialScanList){
	calculateCodeDuplication(partialScanList, 1);
}

void calculateCodeDuplication(list[loc] partialScanList, int cloneType){
	calculateCodeDuplication(partialScanList, cloneType, 0.00);
}

void doPartialScan(list[loc] partialScanList, int cloneType, real similarityPercentage){
	if(cloneType<1 || cloneType>3)
		throw "Clone type must be an integer value between 1 and 3.";
	set[Declaration] asts = {};
	for (loc m <- partialScanList)
		asts += createAstFromFile(m, true);
	getDuplication(cloneType, asts, similarityPercentage);
}