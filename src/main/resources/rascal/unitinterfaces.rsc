module unitinterfaces

import IO;
import Set;
import List;
import lang::java::m3::Core;
import lang::java::m3::AST;
import lang::java::jdt::m3::Core;
import lang::java::jdt::m3::AST;
import sigscore;
import util::Math;
import metrics::unitcomplexity;

public SIGScore getParamSizeScore(list[Declaration] asts){
	return getSIGForPercentages(calculateParams(asts), checkThreeParm, checkFiveParm, checkSevenParm);
}

public list[int] calculateParams(list[Declaration] asts) {
    list[int] params = [];
	for (m <- asts)
		params += calcParametersForAST(m);
	return params;
}

bool checkThreeParm(int param){
	return param >= 3 && param<=4;
}

bool checkFiveParm(int param){
	return param >= 5 && param<=6;
}

bool checkSevenParm(int param){
	return param >= 7;
}

list[int] calcParametersForAST(Declaration dec){
	list[int] paramsSizes = [];
	visit(dec){
		case \method(_,_,list[Declaration] parameters,_,_,_): paramsSizes += size(parameters);
		case \method(_,_,list[Declaration] parameters,_,_): paramsSizes += size(parameters);
		case \constructor(_, list[Declaration] parameters, _, _): paramsSizes += size(parameters);
	}
	return paramsSizes;
}
