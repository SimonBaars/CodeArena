package com.simonbaars.clonerefactor.ast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.utils.SourceRoot;
import com.github.javaparser.utils.SourceRoot.Callback.Result;
import com.simonbaars.clonerefactor.ast.interfaces.Parser;
import com.simonbaars.clonerefactor.detection.CloneDetection;
import com.simonbaars.clonerefactor.detection.interfaces.RemovesDuplicates;
import com.simonbaars.clonerefactor.detection.type2.Type2Variability;
import com.simonbaars.clonerefactor.detection.type3.Type3Opportunities;
import com.simonbaars.clonerefactor.metrics.MetricCollector;
import com.simonbaars.clonerefactor.model.DetectionResults;
import com.simonbaars.clonerefactor.model.Sequence;
import com.simonbaars.clonerefactor.model.location.Location;
import com.simonbaars.clonerefactor.model.location.LocationHolder;
import com.simonbaars.clonerefactor.settings.CloneType;
import com.simonbaars.clonerefactor.settings.Settings;

public class CloneParser implements Parser, RemovesDuplicates {

	private NodeParser astParser;
	public final MetricCollector metricCollector = new MetricCollector();
	
	public DetectionResults parse(SourceRoot sourceRoot, ParserConfiguration config) {
		astParser = new NodeParser(metricCollector);
		Location lastLoc = calculateLineReg(sourceRoot, config);
		if(lastLoc!=null) {
			List<Sequence> findChains = new CloneDetection().findChains(lastLoc);
			doTypeSpecificTransformations(findChains);
			return new DetectionResults(metricCollector.reportClones(findChains), findChains);
		}
		return new DetectionResults();
	}
	
	public DetectionResults parse(Collection<File> files) {
		astParser = new NodeParser(metricCollector);
		Location lastLoc = calculateLineReg(files);
		if(lastLoc!=null) {
			List<Sequence> findChains = new CloneDetection().findChains(lastLoc);
			doTypeSpecificTransformations(findChains);
			return new DetectionResults(metricCollector.reportClones(findChains), findChains);
		}
		return new DetectionResults();
	}

	private void doTypeSpecificTransformations(List<Sequence> findChains) {
		doType2Transformations(findChains); 
		if (Settings.get().getCloneType() == CloneType.TYPE3) {
			new Type3Opportunities().determineType3Opportunities(findChains);
		}
	}

	private void doType2Transformations(List<Sequence> findChains) {
		if(Settings.get().getCloneType().isNotTypeOne()) {
			IntStream.range(0, findChains.size()).forEach(i -> {
				List<Sequence> determineVariability = new Type2Variability().determineVariability(findChains.remove(0));
				for(Sequence s : determineVariability) {
					if(removeDuplicatesOf(findChains, s))
						findChains.add(s);
				}
			});
		}
	}

	private final Location calculateLineReg(SourceRoot sourceRoot, ParserConfiguration config) {
		final LocationHolder lh = new LocationHolder();
		try {
			sourceRoot.parse("", config, (Path localPath, Path absolutePath, ParseResult<CompilationUnit> result) -> {
				if(result.getResult().isPresent()) {
					CompilationUnit cu = result.getResult().get();
					lh.setLocation(astParser.extractLinesFromAST(lh.getLocation(), cu, cu));
				}
				return Result.DONT_SAVE;
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return lh.getLocation();
	}
	
	private final Location calculateLineReg(Collection<File> files) {
		Location l = null;
		for(File file : files) {
			ParseResult<CompilationUnit> compilationUnitNode;
			try {
				compilationUnitNode = new JavaParser().parse(file);
				if(compilationUnitNode.getResult().isPresent()) {
					CompilationUnit cu = compilationUnitNode.getResult().get();
					l = setIfNotNull(l, astParser.extractLinesFromAST(l, cu, cu));
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		return l;
	}
}
