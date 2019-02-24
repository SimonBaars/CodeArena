module metricscommons

import IO;
import Set;
import List;
import lang::java::m3::Core;
import lang::java::m3::AST;
import String;

// problemSize = totalProblemSize (total of all problems in the list
void signalProblems(list[tuple[int, list[loc]]] problemLocs, int problemSize){
	str buffer = toString(problemLocs);
	println(size(buffer));
	println(problemSize);
	println(buffer);
}

void signalProblem(tuple[int, list[loc]] problemLocs, int problemSize){
	signalProblem([problemLocs], problemSize);
}

void signalProblemLoc(loc location, int problemSize){
	signalProblem([problemSize, location], problemSize);
}

void signalProblemDec(Declaration d, int problemSize){
	signalProblem(d.src, problemSize);
}

public int getLinesOfCode(Declaration location) {
	set[int] sourceLocations = {};
	visit (location) {
		case \expressionStatement(_):;
        case Declaration d: {
	        try{
		        sourceLocations+=d.src.begin.line;
		        sourceLocations+=d.src.end.line;
	        } catch RuntimeException:;
		}
		case Statement d: {
	        try{
	        	sourceLocations+=d.src.begin.line;
	        	sourceLocations+=d.src.end.line;
	        } catch RuntimeException:;
		}
	 	case Expression d: {
	        try{
	    	    sourceLocations+=d.src.begin.line;
	        	sourceLocations+=d.src.end.line;
	        } catch RuntimeException:;
		}
    }
	return size(sourceLocations);
}