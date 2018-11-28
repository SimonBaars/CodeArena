//AST -> map[int, [object]]
// map[loc, map[int, [object]]]

//return duplicates as a mapping of codelines as an ast to a list of location and linenumbers


//new method
// make hash from all subtrees in an ast -> add hash to bucket corresponding to number of nodes
// bottom up

module astCreation

import IO;
import Set;
import Map;
import List;
import String;
import lang::java::m3::Core;
import lang::java::m3::AST;
import lang::java::jdt::m3::Core;
import lang::java::jdt::m3::AST;
import util::Math;

public void getDuplication(list[Declaration] asts) {
    map[int, map[int, list[value]]] bucketMap = bucketMapGeneration(asts);
}
		
public map[int, map[int, list[value]]] bucketMapGeneration(list[Declaration] asts) {
	map[int, map[int, list[value]]] bucketMap = ();
	for (m <- asts)
		getBucketAst(m, bucketMap);
	return bucketMap;
}

public map[int, map[int, list[value]]] getBucketAst(Declaration location, map[int, map[int, list[value]]] bucketMap) {
	map[int, list[value]] astMap = ();
	visit (location) {
        case Declaration d: {
        	int hashD = hash([d]);
        	if(hashD in astMap)
        		astMap[hashD] += d;
        	else astMap[hashD] = [d];	
		}
		case Statement d: {
        	int hashD = hash([d]);
        	if(hashD in astMap)
        		astMap[hashD] += d;
        	else astMap[hashD] = [d];	
		}
	 	case Expression d: {
        	int hashD = hash([d]);
        	if(hashD in astMap)
        		astMap[hashD] += d;
        	else astMap[hashD] = [d];	
		}
    }
    //0 = size of subtree
    //if(sizeSubTree in bucketMap)
    	bucketMap[0] += astMap;
    //else bucketMap[0] = astMap;
	return bucketMap;
}

public int hash(list[value] ast) {
	list[int] charRepresentation = chars(toString(ast));
	int hash = 7;
	for(int i <- charRepresentation) {
		if(i == 32)
			continue;
		hash += hash*31 + i;
	}
	return hash;
}

//public map[loc, map[int, list[value]]] convertSrc(map[loc, map[int, list[value]]] astMap){
//	for (item <- astMap){
//		for(tree <- astMap[item]){
//			astMap[item[tree]] = astMap[item[tree]]{
//				case node n: {
//					n.src = |unknown:///|;
//				}
//			}
//		}
//	}
//}


//public map[list[value], list[loc]] compareAsts(list[value] ast, map[loc, map[int, list[value]]] astMap, map[list[value], list[loc]] duplicates){
//	for (compAst <- astMap){
//		if(ast == astMap[compast]){
//			println("WW");
//		}
//	}
//	return duplicates;
//}
