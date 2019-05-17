package toorla.typeCheck;

import toorla.CompileErrorException;
import toorla.ast.Program;
import toorla.ast.Tree;
import toorla.ast.declaration.classDecs.ClassDeclaration;
import toorla.ast.declaration.classDecs.EntryClassDeclaration;
import toorla.ast.declaration.classDecs.classMembersDecs.ClassMemberDeclaration;
import toorla.ast.declaration.classDecs.classMembersDecs.FieldDeclaration;
import toorla.ast.declaration.classDecs.classMembersDecs.MethodDeclaration;
import toorla.ast.declaration.localVarDecs.ParameterDeclaration;
import toorla.ast.expression.*;
import toorla.ast.expression.binaryExpression.*;
import toorla.ast.expression.unaryExpression.Neg;
import toorla.ast.expression.unaryExpression.Not;
import toorla.ast.expression.value.BoolValue;
import toorla.ast.expression.value.IntValue;
import toorla.ast.expression.value.StringValue;
import toorla.ast.statement.*;
import toorla.ast.statement.localVarStats.LocalVarDef;
import toorla.ast.statement.localVarStats.LocalVarsDefinitions;
import toorla.ast.statement.returnStatement.Return;
import toorla.nameAnalyzer.INameAnalyzingPass;
import toorla.visitor.Visitor;

public class ReportingPass implements Visitor<Void> {

    private Boolean hasError = false;

    public Void printRelatedErrors(Tree node) {
        for (CompileErrorException error : node.relatedErrors) {
            System.out.println(error);
        }
        if (node.relatedErrors.size() > 0)
            hasError = true;
        return null;

    }

    @Override
    public Void visit(PrintLine printStat) {
        printRelatedErrors(printStat);
        return null;
    }

    @Override
    public Void visit(Assign assignStat) {
        printRelatedErrors(assignStat);
        return null;
    }

    @Override
    public Void visit(Block block) {
        printRelatedErrors(block);
        for (Statement stmt : block.body)
            stmt.accept(this);
        return null;
    }

    @Override
    public Void visit(Conditional conditional) {
        printRelatedErrors(conditional);
        conditional.getThenStatement().accept(this);
        conditional.getElseStatement().accept(this);
        return null;
    }

    @Override
    public Void visit(While whileStat) {
        printRelatedErrors(whileStat);
        whileStat.body.accept(this);
        return null;
    }

    @Override
    public Void visit(Return returnStat) {
        printRelatedErrors(returnStat);
        return null;
    }

    @Override
    public Void visit(Break breakStat) {
        printRelatedErrors(breakStat);
        return null;
    }

    @Override
    public Void visit(Continue continueStat) {
        printRelatedErrors(continueStat);
        return null;
    }

    @Override
    public Void visit(Skip skip) {
        printRelatedErrors(skip);
        return null;
    }

    @Override
    public Void visit(LocalVarDef localVarDef) {
        printRelatedErrors(localVarDef);
        return null;
    }

    @Override
    public Void visit(IncStatement incStatement) {
        printRelatedErrors(incStatement);
        return null;
    }

    @Override
    public Void visit(DecStatement decStatement) {
        printRelatedErrors(decStatement);
        return null;
    }

    @Override
    public Void visit(Plus plusExpr) {
        printRelatedErrors(plusExpr);
        return null;
    }

    @Override
    public Void visit(Minus minusExpr) {
        printRelatedErrors(minusExpr);
        return null;
    }

    @Override
    public Void visit(Times timesExpr) {
        printRelatedErrors(timesExpr);
        return null;
    }

    @Override
    public Void visit(Division divExpr) {
        printRelatedErrors(divExpr);
        return null;
    }

    @Override
    public Void visit(Modulo moduloExpr) {
        printRelatedErrors(moduloExpr);
        return null;
    }

    @Override
    public Void visit(Equals equalsExpr) {
        printRelatedErrors(equalsExpr);
        return null;
    }

    @Override
    public Void visit(GreaterThan gtExpr) {
        printRelatedErrors(gtExpr);
        return null;
    }

    @Override
    public Void visit(LessThan lessThanExpr) {
        printRelatedErrors(lessThanExpr);
        return null;
    }

    @Override
    public Void visit(And andExpr) {
        printRelatedErrors(andExpr);
        return null;
    }

    @Override
    public Void visit(Or orExpr) {
        printRelatedErrors(orExpr);
        return null;
    }

    @Override
    public Void visit(Neg negExpr) {
        printRelatedErrors(negExpr);
        return null;
    }

    @Override
    public Void visit(Not notExpr) {
        printRelatedErrors(notExpr);
        return null;
    }

    @Override
    public Void visit(MethodCall methodCall) {
        printRelatedErrors(methodCall);
        return null;
    }

    @Override
    public Void visit(Identifier identifier) {
        printRelatedErrors(identifier);
        return null;
    }

    @Override
    public Void visit(Self self) {
        printRelatedErrors(self);
        return null;
    }

    @Override
    public Void visit(IntValue intValue) {
        printRelatedErrors(intValue);
        return null;
    }

    @Override
    public Void visit(NewArray newArray) {
        printRelatedErrors(newArray);
        return null;
    }

    @Override
    public Void visit(BoolValue booleanValue) {
        printRelatedErrors(booleanValue);
        return null;
    }

    @Override
    public Void visit(StringValue stringValue) {
        printRelatedErrors(stringValue);
        return null;
    }

    @Override
    public Void visit(NewClassInstance newClassInstance) {
        printRelatedErrors(newClassInstance);
        return null;
    }

    @Override
    public Void visit(FieldCall fieldCall) {
        printRelatedErrors(fieldCall);
        return null;
    }

    @Override
    public Void visit(ArrayCall arrayCall) {
        printRelatedErrors(arrayCall);
        return null;
    }

    @Override
    public Void visit(NotEquals notEquals) {
        printRelatedErrors(notEquals);
        return null;
    }

    @Override
    public Void visit(ClassDeclaration classDeclaration) {
        printRelatedErrors(classDeclaration);
        for (ClassMemberDeclaration cmd : classDeclaration.getClassMembers())
            cmd.accept(this);
        return null;
    }

    @Override
    public Void visit(EntryClassDeclaration entryClassDeclaration) {
        printRelatedErrors(entryClassDeclaration);
        this.visit((ClassDeclaration) entryClassDeclaration);
        return null;
    }

    @Override
    public Void visit(FieldDeclaration fieldDeclaration) {
        printRelatedErrors(fieldDeclaration);
        return null;
    }

    @Override
    public Void visit(ParameterDeclaration parameterDeclaration) {
        printRelatedErrors(parameterDeclaration);
        return null;
    }

    @Override
    public Void visit(MethodDeclaration methodDeclaration) {
        printRelatedErrors(methodDeclaration);
        for (ParameterDeclaration pd : methodDeclaration.getArgs())
            pd.accept(this);
        for (Statement stmt : methodDeclaration.getBody())
            stmt.accept(this);
        return null;
    }

    @Override
    public Void visit(LocalVarsDefinitions localVarsDefinitions) {
        printRelatedErrors(localVarsDefinitions);
        for (LocalVarDef lvd : localVarsDefinitions.getVarDefinitions()) {
            lvd.accept(this);
        }
        return null;
    }

    @Override
    public Void visit(Program program) {

        for (ClassDeclaration cd : program.getClasses()) {
        cd.accept(this);
        }
        return null;
    }

    public Void analyze(Program program) {
        this.visit(program);
        if (!hasError){
            System.out.println("No error detected;");
        }
        return null;
    }

}

