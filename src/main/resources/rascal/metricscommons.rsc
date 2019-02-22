module metricsCommons

import IO;
import Set;
import List;

void signalProblems(list[tuple[int, list[loc]]] problemLocs, int problemSize){
	str buffer = toString(problemLocs);
	println(size(buffer));
	println(problemSize);
	println(buffer);
}

void signalProblem(tuple[int, list[loc]] problemLocs, int problemSize){
	signalProblem([problemLocs], problemSize);
}