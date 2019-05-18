package com.simonbaars.clonerefactor.ast;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.LocalClassDeclarationStmt;
import com.simonbaars.clonerefactor.SequenceObservable;
import com.simonbaars.clonerefactor.ast.interfaces.DeterminesNodeTokens;
import com.simonbaars.clonerefactor.ast.interfaces.Parser;
import com.simonbaars.clonerefactor.metrics.MetricCollector;
import com.simonbaars.clonerefactor.metrics.ProblemType;
import com.simonbaars.clonerefactor.metrics.collectors.CyclomaticComplexityCalculator;
import com.simonbaars.clonerefactor.metrics.collectors.NumberOfParametersCalculator;
import com.simonbaars.clonerefactor.metrics.collectors.UnitSizeCalculator;
import com.simonbaars.clonerefactor.model.Sequence;
import com.simonbaars.clonerefactor.model.location.Location;
import com.simonbaars.clonerefactor.model.location.LocationContents;
import com.simonbaars.clonerefactor.settings.Settings;

public class NodeParser implements Parser, DeterminesNodeTokens {
	final Map<LocationContents, Location> lineReg = new HashMap<>();
	private final MetricCollector metricCollector;
	
	public NodeParser(MetricCollector metricCollector) {
		this.metricCollector = metricCollector;
	}

	public Location extractLinesFromAST(Location prevLocation, CompilationUnit cu, Node n) {
		if(n instanceof ImportDeclaration || n instanceof PackageDeclaration || isExcluded(n))
			return prevLocation;
		if(n instanceof MethodDeclaration && SequenceObservable.get().isActive()) {
			collectAlternateMetrics((MethodDeclaration)n, cu);
		}
		if(!(n instanceof CompilationUnit || n instanceof BlockStmt || n instanceof LocalClassDeclarationStmt))
			prevLocation = setIfNotNull(prevLocation, parseToken(prevLocation, cu,  n));
		for (Node child : childrenToParse(n)) {
			prevLocation = setIfNotNull(prevLocation, extractLinesFromAST(prevLocation, cu, child));
		}
		return prevLocation;
	}
	
	
	private void collectAlternateMetrics(MethodDeclaration n, CompilationUnit cu) {
		final Location l = new Location(cu.getStorage().get().getPath(), n.getRange().get());
		Sequence sequence = new Sequence(Collections.singletonList(l));
		l.getContents().getNodes().add(n);
		l.getContents().setTokens(n.getTokenRange().get());
		
		int cc = new CyclomaticComplexityCalculator().calculate(n);
		int methodSize = new UnitSizeCalculator().calculate(n);
		int parameters = new NumberOfParametersCalculator().calculate(n);
		if(cc >= Settings.get().getCyclomaticComplexity()) 
			SequenceObservable.get().sendUpdate(ProblemType.UNITCOMPLEXITY, sequence);
		if(methodSize >= Settings.get().getUnitSize())
			SequenceObservable.get().sendUpdate(ProblemType.UNITVOLUME, sequence);
		if(parameters >= Settings.get().getUnitInterfaceParameters()) {
			sequence = new Sequence(Collections.singletonList(new Location(l).setRange(getRange(n))));
			SequenceObservable.get().sendUpdate(ProblemType.UNITINTERFACESIZE, sequence);
		}
	}

	public Location parseToken(Location prevLocation, CompilationUnit cu, Node n) {
		Location thisLocation = new Location(cu.getStorage().get().getPath(), prevLocation, n);
		if(prevLocation!=null) prevLocation.setNextLine(thisLocation);
		addLineTokensToReg(thisLocation);
		return thisLocation;
	}

	public Location addLineTokensToReg(Location location) {
		if(lineReg.containsKey(location.getContents())) {
			location.setClone(lineReg.get(location.getContents()));
			lineReg.put(location.getContents(), location);
		} else {
			lineReg.put(location.getContents(), location);
		}
		metricCollector.reportFoundNode(location);
		return location;
	}
}
