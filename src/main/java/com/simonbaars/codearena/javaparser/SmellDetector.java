package com.simonbaars.codearena.javaparser;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.Range;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.simonbaars.clonerefactor.SequenceObservable;
import com.simonbaars.clonerefactor.metrics.ProblemType;
import com.simonbaars.clonerefactor.model.Sequence;
import com.simonbaars.clonerefactor.model.location.Location;

/**
 * Standalone JavaParser-based smell detector.
 * Detects code duplication and complexity issues without requiring CloneRefactor library.
 */
public class SmellDetector {
    
    private final JavaParser parser = new JavaParser();
    private final SequenceObservable observable = SequenceObservable.get();
    
    public void scanFiles(List<File> javaFiles) {
        List<CompilationUnit> units = new ArrayList<>();
        Map<CompilationUnit, Path> unitToPath = new HashMap<>();
        
        for (File file : javaFiles) {
            try {
                ParseResult<CompilationUnit> result = parser.parse(file);
                if (result.isSuccessful() && result.getResult().isPresent()) {
                    CompilationUnit cu = result.getResult().get();
                    units.add(cu);
                    unitToPath.put(cu, file.toPath());
                }
            } catch (Exception e) {
                System.err.println("Failed to parse " + file + ": " + e.getMessage());
            }
        }
        
        detectDuplication(units, unitToPath);
        detectComplexity(units, unitToPath);
        detectLargeUnits(units, unitToPath);
        detectLargeInterfaces(units, unitToPath);
    }
    
    public void scanDirectory(String path) {
        scanFiles(collectJavaFiles(path));
    }
    
    public void scanDemoSources() {
        try {
            String resourcePath = getClass().getClassLoader().getResource("demo-sources").getPath();
            scanDirectory(resourcePath);
        } catch (Exception e) {
            System.err.println("Failed to load demo sources: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private List<File> collectJavaFiles(String basePath) {
        List<File> javaFiles = new ArrayList<>();
        try {
            Files.walkFileTree(Paths.get(basePath), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path filePath, BasicFileAttributes attrs) throws IOException {
                    File file = filePath.toFile();
                    if (file.getName().endsWith(".java")) {
                        javaFiles.add(file);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            System.err.println("Failed to scan directory " + basePath + ": " + e.getMessage());
        }
        return javaFiles;
    }
    
    private void detectDuplication(List<CompilationUnit> units, Map<CompilationUnit, Path> unitToPath) {
        Map<String, List<MethodInfo>> methodsByContent = new HashMap<>();
        
        for (CompilationUnit cu : units) {
            Path path = unitToPath.get(cu);
            cu.findAll(MethodDeclaration.class).forEach(method -> {
                Optional<BlockStmt> body = method.getBody();
                if (body.isPresent()) {
                    String normalized = normalizeCode(body.get().toString());
                    if (normalized.length() > 50) {
                        MethodInfo info = new MethodInfo(method, path, normalized);
                        methodsByContent.computeIfAbsent(normalized, k -> new ArrayList<>()).add(info);
                    }
                }
            });
        }
        
        for (Map.Entry<String, List<MethodInfo>> entry : methodsByContent.entrySet()) {
            List<MethodInfo> methods = entry.getValue();
            if (methods.size() > 1) {
                Sequence seq = new Sequence();
                int totalLines = 0;
                
                for (MethodInfo info : methods) {
                    Optional<Range> range = info.method.getRange();
                    if (range.isPresent()) {
                        Location loc = new Location(info.path, range.get());
                        seq.add(loc);
                        totalLines += range.get().end.line - range.get().begin.line + 1;
                    }
                }
                
                if (seq.size() > 1) {
                    int problemSize = totalLines / seq.size();
                    observable.notify(ProblemType.DUPLICATION, seq, problemSize);
                }
            }
        }
    }
    
    private void detectComplexity(List<CompilationUnit> units, Map<CompilationUnit, Path> unitToPath) {
        for (CompilationUnit cu : units) {
            Path path = unitToPath.get(cu);
            cu.findAll(MethodDeclaration.class).forEach(method -> {
                int complexity = calculateCyclomaticComplexity(method);
                if (complexity > 10) {
                    Optional<Range> range = method.getRange();
                    if (range.isPresent()) {
                        Sequence seq = new Sequence();
                        seq.add(new Location(path, range.get()));
                        observable.notify(ProblemType.UNITCOMPLEXITY, seq, complexity);
                    }
                }
            });
        }
    }
    
    private void detectLargeUnits(List<CompilationUnit> units, Map<CompilationUnit, Path> unitToPath) {
        for (CompilationUnit cu : units) {
            Path path = unitToPath.get(cu);
            cu.findAll(MethodDeclaration.class).forEach(method -> {
                Optional<Range> range = method.getRange();
                if (range.isPresent()) {
                    int lines = range.get().end.line - range.get().begin.line + 1;
                    if (lines > 50) {
                        Sequence seq = new Sequence();
                        seq.add(new Location(path, range.get()));
                        observable.notify(ProblemType.UNITVOLUME, seq, lines);
                    }
                }
            });
        }
    }
    
    private void detectLargeInterfaces(List<CompilationUnit> units, Map<CompilationUnit, Path> unitToPath) {
        for (CompilationUnit cu : units) {
            Path path = unitToPath.get(cu);
            cu.findAll(MethodDeclaration.class).forEach(method -> {
                int paramCount = method.getParameters().size();
                if (paramCount > 5) {
                    Optional<Range> range = method.getRange();
                    if (range.isPresent()) {
                        Sequence seq = new Sequence();
                        seq.add(new Location(path, range.get()));
                        observable.notify(ProblemType.UNITINTERFACESIZE, seq, paramCount);
                    }
                }
            });
        }
    }
    
    private String normalizeCode(String code) {
        return code.replaceAll("\\s+", " ")
                   .replaceAll("/\\*.*?\\*/", "")
                   .replaceAll("//.*", "")
                   .trim();
    }
    
    private int calculateCyclomaticComplexity(MethodDeclaration method) {
        int complexity = 1;
        
        complexity += method.findAll(com.github.javaparser.ast.stmt.IfStmt.class).size();
        complexity += method.findAll(com.github.javaparser.ast.stmt.ForStmt.class).size();
        complexity += method.findAll(com.github.javaparser.ast.stmt.ForEachStmt.class).size();
        complexity += method.findAll(com.github.javaparser.ast.stmt.WhileStmt.class).size();
        complexity += method.findAll(com.github.javaparser.ast.stmt.DoStmt.class).size();
        complexity += method.findAll(com.github.javaparser.ast.stmt.SwitchEntry.class).size();
        complexity += method.findAll(com.github.javaparser.ast.stmt.CatchClause.class).size();
        complexity += method.findAll(com.github.javaparser.ast.expr.ConditionalExpr.class).size();
        
        return complexity;
    }
    
    private static class MethodInfo {
        final MethodDeclaration method;
        final Path path;
        final String normalizedContent;
        
        MethodInfo(MethodDeclaration method, Path path, String normalizedContent) {
            this.method = method;
            this.path = path;
            this.normalizedContent = normalizedContent;
        }
    }
}
