module unitsize

import IO;
import Set;
import List;
import lang::java::m3::Core;
import lang::java::m3::AST;
import sigscore;
import unitcomplexity;
import metricscommons;

int TOO_BIG_UNIT_SIZE = 25;

public void unitsize(set[Declaration] asts){
	list[int] locs = [];
	for (ast <- asts){
		visit(ast){
			case Declaration d: {
				if(isMethod(d)){ 
					int lines = getLinesOfCode(d);
					if(lines>=TOO_BIG_UNIT_SIZE)
						signalProblemDec(d, lines);
				}
			}
		}
	}
}

public bool isMethod(Declaration d){
	switch(d){
		case \method(_,_,_,_,_): return true;
		case \method(_,_,_,_): return true;
		case \constructor(_, _, _, _): return true;
	}
	return false;
}
