module loader

import IO;
import Set;
import List;
import lang::java::m3::Core;
import lang::java::m3::AST;
import String;

list[Declaration] asts = [];

void getAsts(list[loc] partialScanList){
	asts = [];
	for (loc m <- partialScanList)
		asts += createAstFromFile(m, true);
}

void scanMetric(void (set[Declaration]) metricFunction, list[int] astIndexes){
	metricFunction({asts[astIndexes[i]] | int i <- [0 .. size(astIndexes)]});
	println(0);
}

void calcMetric(void (set[Declaration]) metricFunction){
	scanMetric(metricFunction, [0..size(asts)]);
}