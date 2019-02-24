module loader

import IO;
import Set;
import List;
import lang::java::m3::Core;
import lang::java::m3::AST;
import String;

set[Declaration] getAsts(list[loc] partialScanList){
	set[Declaration] asts = {};
	for (loc m <- partialScanList)
		asts += createAstFromFile(m, true);
	return asts;
}

void calculateCodeDuplication(void (set[Declaration]) metricFunction, set[Declaration] asts){
	metricFunction(asts);
}