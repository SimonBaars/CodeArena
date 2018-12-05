module packagename::ClassName

import lang::java::m3::Core;
import lang::java::m3::AST;
import IO;
import List;
import String;

void calculateCodeDuplication(loc location){
	M3 model = createM3FromDirectory(location);
	list[Declaration] asts = [];
	list[loc] decls = [];
	list[loc] stats = [];
	for (m <- model.containment, m[0].scheme == "java+compilationUnit"){
		Declaration ast = createAstFromFile(m[0], true);
		visit(ast){
			case Declaration d: if(d.src != |unknown:///|) decls += d.src;
		}
	}
	str buffer = toString([decls, stats]);
	println(size(buffer));
	println(buffer);
}