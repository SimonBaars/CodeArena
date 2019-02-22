module componentsize

import IO;
import Set;
import List;
import String;
import util::Math;
import lang::java::m3::Core;
import lang::java::m3::AST;
import lang::java::jdt::m3::Core;
import lang::java::jdt::m3::AST;
import sigscore;
import metrics::volume;

public str getComponentSize(M3 model, map[loc, Declaration] asts){
	list[int] sizePerComponent = calculateSizePerComponent(model, asts);
	int amountOfComponents = size(sizePerComponent);
	real gini = giniCoefficient(sizePerComponent);

	SIGScore amountOfComponentsScore = minusminus();
	if(amountOfComponents==9)
		amountOfComponentsScore = plusplus();
	else if(amountOfComponents == 8 || amountOfComponents == 10)
		amountOfComponentsScore = plus();
	else if(amountOfComponents == 7 || amountOfComponents == 11)
		amountOfComponentsScore = neutral();
	else if(amountOfComponents == 6 || amountOfComponents == 12)
		amountOfComponentsScore = minus();
	println("Amount of components score = <getSigScoreAsString(amountOfComponentsScore)>");
		
	SIGScore giniScore = minusminus();
	if(gini<=0.61)
		giniScore = plusplus();
	else if(gini<=0.71)
		giniScore = plus();
	else if(gini<=0.81)
		giniScore = neutral();
	else if(gini<=0.91)
		giniScore = minus();
	println("Components size deviation score = <getSigScoreAsString(giniScore)>");
	return getAverageOfSigScores([getSigScoreAsString(giniScore), getSigScoreAsString(amountOfComponentsScore)]);
}

public real giniCoefficient(list[int] sizePerComponent){
	int s = 0;
	for(i <- sizePerComponent){
		for(j <- sizePerComponent){
			s+=abs(i-j);
		}
	}
	return s/toReal(2*size(sizePerComponent)*sum(sizePerComponent));
}

public list[int] calculateSizePerComponent(M3 model, map[loc, Declaration] asts){
	set[loc] packages = packages(model);
	list[int] componentSizes = [];
	for(package <- packages){
		list[loc] classFiles = [x | x <- resolveLocation(package).ls, endsWith(x.file, ".java")];
		if(size(classFiles)>0){
			int sourceSize = 0;
			for(classFile <- classFiles){
				classFile.scheme = "java+compilationUnit";
				classFile.authority = "";
				if(classFile in asts)	
					sourceSize+=getLinesOfCode(asts[classFile]);
			}
			componentSizes += sourceSize;
		}
	}
	return componentSizes;
}