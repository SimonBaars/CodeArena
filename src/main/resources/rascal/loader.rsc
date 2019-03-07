module loader

import IO;
import Set;
import List;
import lang::java::m3::Core;
import lang::java::m3::AST;
import String;

set[Declaration] asts = {};

void calcMetric(void (set[Declaration]) metricFunction, list[loc] partialScanList, bool reload){
	if(reload){
		asts = {};
		for (loc m <- partialScanList)
			asts += createAstFromFile(m, true);
	}
	metricFunction(asts);
	println(0);
}