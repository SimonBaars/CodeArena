module loader

import IO;
import Set;
import List;
import lang::java::m3::Core;
import lang::java::m3::AST;
import String;

set[Declaration] asts = {};

void getAsts(list[loc] partialScanList){
	asts = {};
	for (loc m <- partialScanList)
		asts += createAstFromFile(m, true);
}

void calcMetric(void (set[Declaration]) metricFunction){
	metricFunction(asts);
	println(0);
}