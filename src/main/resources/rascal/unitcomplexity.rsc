module unitcomplexity

import IO;
import Set;
import List;
import lang::java::m3::Core;
import lang::java::m3::AST;
import lang::java::jdt::m3::Core;
import lang::java::jdt::m3::AST;
import sigscore;
import util::Math;

public SIGScore getUnitComplexityScore(list[Declaration] model){
	list[int] cc = calculateCC(model);
	return getSIGForPercentages(cc, moderateComplexity, highComplexity, veryHighComplexity);
}

public SIGScore getSIGForPercentages(list[int] cc, bool (int) comparingMethodModerate, bool (int) comparingMethodHigh, bool (int) comparingMethodVeryHigh){
	real pModerate = getPercentage(cc, comparingMethodModerate, "Moderate");
	real pHigh = getPercentage(cc, comparingMethodHigh, "High");
	real pVeryHigh = getPercentage(cc, comparingMethodVeryHigh, "Very high");
	if(pModerate<=25 && pHigh==0 && pVeryHigh==0)
		return plusplus();
	else if (pModerate<=30 && pHigh<=5 && pVeryHigh==0)
		return plus();
	else if (pModerate<=40 && pHigh<=10 && pVeryHigh==0)
		return neutral();
	else if (pModerate<=50 && pHigh<=15 && pVeryHigh<=5)
		return minus();
	return minusminus();
}

public real getPercentage(list[int] cc, bool (int) comparingMethod, str info){
	int total = size(cc);
	int amount = 0;
	for(score <- cc, comparingMethod(score))
		amount += 1;
	
	println("<info> : <amount> / <total> ( <toReal(amount)/toReal(total)*100>%)");
	return toReal(amount)/toReal(total)*100;
}

public bool moderateComplexity(int cc){
	return cc>=11 && cc<=20;
}

public bool highComplexity(int cc){
	return cc>=21 && cc<=50;
}

public bool veryHighComplexity(int cc){
	return cc>50;
}

public list[int] calculateCC(list[Declaration] asts) {
    list[int] locs = [];
	for (m <- asts)
		locs += calcCCForAST(m);
	return locs;
}

list[int] calcCCForAST(Declaration dec){
	list[int] size = [];
	visit(dec){
		case \method(_,_,_,_, Statement impl): size += calcCC(impl);
		case \constructor(_, _, _, Statement impl): size += calcCC(impl);
	}
	return size;
}

// Based on: https://stackoverflow.com/questions/40064886/obtaining-cyclomatic-complexity
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