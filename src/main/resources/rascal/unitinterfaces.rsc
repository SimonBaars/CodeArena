module unitinterfaces

import IO;
import Set;
import List;
import lang::java::m3::Core;
import lang::java::m3::AST;
import sigscore;
import util::Math;
import metricscommons;

int TOO_MANY_UNIT_PARAMETERS = 4;

public void unitinterfaces(Declaration dec){
	visit(dec){
		case Declaration d: checkType(d);
	}
}

public void checkType(Declaration dec){
	switch(dec){
		case \method(_,_,list[Declaration] parameters,_,_,_): checkTooBig(dec, parameters);
		case \method(_,_,list[Declaration] parameters,_,_): checkTooBig(dec, parameters);
		case \constructor(_, list[Declaration] parameters, _, _): checkTooBig(dec, parameters);
	}
}

public void checkTooBig(Declaration dec, list[Declaration] params){
	int nParam = size(params);
	if(nParam>=TOO_MANY_UNIT_PARAMETERS){
		signalProblemDec(dec, nParam);
	}
}
