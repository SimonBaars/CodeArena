module unitcomplexity

import IO;
import Set;
import List;
import lang::java::m3::Core;
import lang::java::m3::AST;
import util::Math;
import metricscommons;

public void unitcomplexity(set[Declaration] asts){
	for (m <- asts){
		visit(m){
			case Declaration d: calcCCForAST(d);
		}
	}
}

void calcCCForAST(Declaration dec){
	switch(dec){
		case \method(_,_,_,_, Statement impl): checkCC(dec, impl);
		case \constructor(_, _, _, Statement impl): checkCC(dec, impl);
	}
}

void checkCC(Declaration dec, Statement impl){
	int ccNumber = calcCC(impl);
	if(ccNumber>=21){
		signalProblemDec(dec, ccNumber);
	}
}

int calcCC(Statement impl) {
    int result = 1;
    visit (impl) {
        case \if(_,_): result += 1;
        case \if(_,_,_): result += 1;
        case \case(_): result += 1;
        case \do(_,_): result += 1;
        case \while(_,_): result += 1;
        case \for(_,_,_): result += 1;
        case \for(_,_,_,_): result += 1;
        case foreach(_,_,_): result += 1;
        case \catch(_,_): result += 1;
        case \conditional(_,_,_): result += 1;
        case infix(_,"&&",_): result += 1;
        case infix(_,"||",_): result += 1;
    }
    return result;
}