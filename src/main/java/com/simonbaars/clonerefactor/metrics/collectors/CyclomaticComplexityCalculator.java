package com.simonbaars.clonerefactor.metrics.collectors;
import java.util.List;
import java.util.stream.Collectors;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.stmt.DoStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.SwitchEntry;
import com.github.javaparser.ast.stmt.WhileStmt;

import static com.github.javaparser.ast.expr.BinaryExpr.Operator.AND;
import static com.github.javaparser.ast.expr.BinaryExpr.Operator.OR;

public class CyclomaticComplexityCalculator implements Calculator<MethodDeclaration> {
    @Override
    public int calculate(MethodDeclaration method) {
        List<IfStmt> ifStmts = method.findAll(IfStmt.class);
        List<ForStmt> forStmts = method.findAll(ForStmt.class);
        List<WhileStmt> whileStmts = method.findAll(WhileStmt.class);
        List<DoStmt> doStmts = method.findAll(DoStmt.class);
        List<SwitchEntry> catchStmts = method.findAll(SwitchEntry.class).stream().
                filter(s -> !s.getLabels().isEmpty()) //Don't include "default" statements, only labeled case statements
                .collect(Collectors.toList());
        List<ConditionalExpr> ternaryExprs = method.findAll(ConditionalExpr.class);
        List<BinaryExpr> andExprs = method.findAll(BinaryExpr.class).stream().
                filter(f -> f.getOperator() == AND).collect(Collectors.toList());
        List<BinaryExpr> orExprs = method.findAll(BinaryExpr.class).stream().
                filter(f -> f.getOperator() == OR).collect(Collectors.toList());

       return ifStmts.size() +
                forStmts.size() +
                whileStmts.size() +
                doStmts.size() +
                catchStmts.size() +
                ternaryExprs.size() +
                andExprs.size() +
                orExprs.size() +
                1;
    }
}