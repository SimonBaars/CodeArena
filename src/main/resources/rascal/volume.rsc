module volume

import IO;
import Set;
import List;
import lang::java::m3::Core;
import lang::java::m3::AST;
import lang::java::jdt::m3::Core;
import lang::java::jdt::m3::AST;
import sigscore;
import Exception;

public tuple[SIGScore score, int LOC] getVolume(list[Declaration] model){
	int linesOfCode = calculateLinesOfCode(model);
	SIGScore score = neutral();
	if(linesOfCode<=66000) score = plusplus();
	else if(linesOfCode>66000 && linesOfCode<=246000) score = plus();
	else if(linesOfCode>246000 && linesOfCode<=665000) score = neutral();
	else if(linesOfCode>665000 && linesOfCode<=1310000) score = minus();
	else score = minusminus();
	return <score, linesOfCode>;
}

public int calculateLinesOfCode(list[Declaration] asts) {
    list[int] locSizes = [];
	for (m <- asts)
		locSizes += getLinesOfCode(m);
	return sum(locSizes);
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
