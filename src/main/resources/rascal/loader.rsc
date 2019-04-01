module loader

import IO;
import Set;
import List;
import lang::java::m3::Core;
import lang::java::m3::AST;
import String;

Declaration asts = {};

void calcMetric(void (set[Declaration]) metricFunction, loc partialScanList, bool reload){
	if(reload)
		asts = createAstFromFile(partialScanList, true);
	metricFunction(asts);
	println(0);
}