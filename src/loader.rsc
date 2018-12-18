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

void calculateCodeDuplication(loc location, int cloneType, real allowedDiffPercentage){
	if(cloneType<1 || cloneType>3)
		throw "Clone type must be an integer value between 1 and 3.";
	getDuplication(cloneType, createAstsFromDirectory(location, false), allowedDiffPercentage);
}

void calculateCodeDuplication(loc location){
	calculateCodeDuplication(location, 1);
}

void calculateCodeDuplication(loc location, int cloneType){
	calculateCodeDuplication(location, cloneType, 0.00);
}

void doPartialScan(list[loc] partialScanList){
	set[Declaration] asts = {};
	for (loc m <- partialScanList)
		asts += createAstFromFile(m, true);
	getDuplication(1, asts, 0.00);
}