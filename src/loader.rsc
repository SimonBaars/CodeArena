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

void calculateCodeDuplication(loc location, int cloneType){
	if(cloneType<1 || cloneType>3)
		throw "Clone type must be an integer value between 1 and 3.";
	getDuplication(cloneType, createAstsFromDirectory(location, false));
}

void calculateCodeDuplication(loc location){
	calculateCodeDuplication(location, 1);
}