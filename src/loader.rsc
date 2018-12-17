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
	getDuplication(cloneType, createAstsFromDirectory(location, false));
}

void calculateCodeDuplication(loc location){
	getDuplication(1, createAstsFromDirectory(location, false));
}