module unitsize

import IO;
import Set;
import List;
import lang::java::m3::Core;
import lang::java::m3::AST;
import lang::java::jdt::m3::Core;
import lang::java::jdt::m3::AST;
import sigscore;
import unitcomplexity;
import metricsCommons;

public void getUnitSize(set[Declaration] model){
	list[int] locs = [];
	for (ast <- asts){
		visit(ast){
			case Declaration d: {
				if(isMethod(d)){ 
					int lines = getLinesOfCode(d);
					if(lines>=25)
						signalProblemLoc(d.src, lines);
				}
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
