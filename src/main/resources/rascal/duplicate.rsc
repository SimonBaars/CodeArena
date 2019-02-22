module metrics::duplicate

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
import metrics::volume;

int AMOUNT_OF_LINES = 6;

public tuple[SIGScore score, real perc] getDuplicatePercentage(M3 model){
	tuple[int dups,int totalLinesChecked] dupsTotal = getDuplicatesFromModel(model);
	println("Duplicated lines, Total Lines = <dupsTotal>");
	real duplicatePercentage = calculateDuplicates(dupsTotal);
	SIGScore score = neutral();
	
	if(duplicatePercentage<=3) score = plusplus();
	else if(duplicatePercentage>3 && duplicatePercentage<=5) score = plus();
	else if(duplicatePercentage>5 && duplicatePercentage<=10) score = neutral();
	else if(duplicatePercentage>10 && duplicatePercentage<=20) score = minus();
	else score = minusminus();
	
	return <score, duplicatePercentage>;
}

public real calculateDuplicates(tuple[int dups,int totalLinesChecked] duplicates) {
	return duplicates.dups / toReal(duplicates.totalLinesChecked) * 100;
}

public int hash(list[str] lines, int l) {
	int hash = 7;
	for(int i <- [0 .. 6]) {
		bool leadingSpaces = true;
		for(int j <- [0 .. size(lines[i+l])]) {
			int character = charAt(lines[i+l], j);
			if(leadingSpaces){
				if(character == 32)
					continue;
				leadingSpaces = false;
			}
			hash += hash*31 + character;
		}
		hash += hash*(i+32);
	}
	return hash;
}

public tuple[int dups, int amountOfLines] getDuplicatesFromModel(M3 model) {
	map[int lineHash, int amount] hashMap = ();
	int dups = 0;
	int amountOfLines = 0;
	set[loc] classes = classes(model);
	for (cu <- classes) {
		tuple[int dups, int amountOfLines, map[int lineHash, int amount] hashMap] dupData = getLines(cu, hashMap);
		hashMap = dupData.hashMap;
		dups += dupData.dups;
		amountOfLines += dupData.amountOfLines;
	}
	return <dups, amountOfLines>;
}


public tuple[int dups, int amountOfLines, map[int lineHash, int amount] hashMap] getLines(loc location, map[int lineHash, int amount] hashMap) {
	list[str] lines = readFileLines(location);
	int dups = 0;
	int amountOfLines = 0;
	int chain = 0;
	if(size(lines)>=AMOUNT_OF_LINES){
		for (l <- [0..size(lines)-(AMOUNT_OF_LINES-1)]) {
			int hashLines = hash(lines, l);
			if(hashLines in hashMap){
				if(hashLines == hash(lines, l-1))
					dups +=1;
				else
					dups += AMOUNT_OF_LINES-chain;
				chain=AMOUNT_OF_LINES;
			} else {
				hashMap[hashLines] = 1;
			}
			chain -= 1;
			if(chain<0)
				chain=0;
		}
	}
	amountOfLines += size(lines);
	return <dups, amountOfLines, hashMap>;
}