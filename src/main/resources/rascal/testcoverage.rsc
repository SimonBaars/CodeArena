module metrics::testcoverage

import IO;
import Set;
import Map;
import List;
import String;
import util::Math;
import lang::java::m3::Core;
import lang::java::m3::AST;
import lang::java::jdt::m3::Core;
import lang::java::jdt::m3::AST;
import sigscore;

public tuple[SIGScore score, real perc, list[int] tests] getTestCoveragePerc(M3 model, map[loc, Declaration] asts){
	list[list[str]] methods = getTestCoverage(model, asts);
	int testedMethods = size(toSet(methods[0]) & toSet(methods[1]));
	
	real testCovPerc = testedMethods / toReal(size(toSet(methods[1]))) * 100;

	SIGScore score = neutral();
	
	if(testCovPerc>=95) score = plusplus();
	else if(testCovPerc>=80 && testCovPerc<95) score = plus();
	else if(testCovPerc>=60 && testCovPerc<80) score = neutral();
	else if(testCovPerc>=20 && testCovPerc<60) score = minus();
	else score = minusminus();
	
	return <score, testCovPerc, [size(toSet(methods[1])), testedMethods]>;
}


list[list[str]] getTestCoverage(M3 model, map[loc, Declaration] asts) {
	list[str] testMethods = [];
	list[str] codeMethods = [];
	
	set[loc] packages = packages(model);
	for(package <- packages){
		list[loc] classFiles = [x | x <- resolveLocation(package).ls, endsWith(x.file, ".java")];
		if(size(classFiles)>0){
			for(classFile <- classFiles){
				classFile.scheme = "java+compilationUnit";
				classFile.authority = "";
				if(classFile in asts){
					if(package.file=="test" || package.file=="junit"){ //If the folder is either test or junit we'll assume it contains tests.
						testMethods += getTestMethods(asts[classFile]);
					} else {
						codeMethods += getCodeMethods(asts[classFile]);			
					}
				}
			}
		}
	}
	
	return([testMethods, codeMethods]);	
}

list[str] getCodeMethods(Declaration dec){
	list[str] methodNames = [];
	try{
		visit(dec){
			case \method(_,str name,_,_,_): methodNames += name;
			case \method(_,str name,_,_): methodNames += name;
		}
		return methodNames;
	}
	catch IO(message):
	return methodNames;
}

list[str] getTestMethods(Declaration dec){
	list[str] methodNames = [];
	visit(dec){
		case \methodCall(_,str name,_): methodNames += name;
		case \methodCall(_,_,str name,_): methodNames += name;
	}
	return methodNames;
}