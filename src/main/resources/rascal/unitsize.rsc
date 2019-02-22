module unitsize

import IO;
import Set;
import List;
import lang::java::m3::Core;
import lang::java::m3::AST;
import lang::java::jdt::m3::Core;
import lang::java::jdt::m3::AST;
import sigscore;
import metrics::volume;
import metrics::unitcomplexity;

public SIGScore getUnitSize(list[Declaration] model){
	list[int] unitSizes = calculateLinesOfCodePerMethod(model);
	return getSIGForPercentages(unitSizes, moderateUnitSize, highUnitSize, veryHighUnitSize);
}

public bool moderateUnitSize(int unitSize){
	return unitSize>=16 && unitSize<=30;
}

public bool highUnitSize(int unitSize){
	return unitSize>=31 && unitSize<=60;
}

public bool veryHighUnitSize(int unitSize){
	return unitSize>60;
}

public list[int] calculateLinesOfCodePerMethod(list[Declaration] asts) {
    list[int] locs = [];
	for (ast <- asts){
		visit(ast){
			case Declaration d: {
				if(isMethod(d)) locs+=getLinesOfCode(d);
			}
		}
	}
	return locs;
}

public bool isMethod(Declaration d){
	switch(d){
		case \method(_,_,_,_,_): return true;
		case \method(_,_,_,_): return true;
	}
	return false;
}
