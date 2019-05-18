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
        printStat.getArg().accept(this);
        return null;
    }

    @Override
    public Void visit(Assign assignStat) {
        printRelatedErrors(assignStat);
        assignStat.getLvalue().accept(this);
        assignStat.getRvalue().accept(this);
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
        conditional.getCondition().accept(this);
        conditional.getThenStatement().accept(this);
        conditional.getElseStatement().accept(this);
        return null;
    }

    @Override
    public Void visit(While whileStat) {
        printRelatedErrors(whileStat);
        whileStat.expr.accept(this);
        whileStat.body.accept(this);
        return null;
    }

    @Override
    public Void visit(Return returnStat) {
        printRelatedErrors(returnStat);
        returnStat.getReturnedExpr().accept(this);
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
        localVarDef.getLocalVarName().accept(this);
        localVarDef.getInitialValue().accept(this);
        return null;
    }

    @Override
    public Void visit(IncStatement incStatement) {
        printRelatedErrors(incStatement);
        incStatement.getOperand().accept(this);
        return null;
    }

    @Override
    public Void visit(DecStatement decStatement) {
        printRelatedErrors(decStatement);
        decStatement.getOperand().accept(this);
        return null;
    }

    @Override
    public Void visit(Plus plusExpr) {
        printRelatedErrors(plusExpr);
        plusExpr.getLhs().accept(this);
        plusExpr.getRhs().accept(this);
        return null;
    }

    @Override
    public Void visit(Minus minusExpr) {
        printRelatedErrors(minusExpr);
        minusExpr.getLhs().accept(this);
        minusExpr.getRhs().accept(this);
        return null;
    }

    @Override
    public Void visit(Times timesExpr) {
        printRelatedErrors(timesExpr);
        timesExpr.getLhs().accept(this);
        timesExpr.getRhs().accept(this);
        return null;
    }

    @Override
    public Void visit(Division divExpr) {
        printRelatedErrors(divExpr);
        divExpr.getLhs().accept(this);
        divExpr.getRhs().accept(this);
        return null;
    }

    @Override
    public Void visit(Modulo moduloExpr) {
        printRelatedErrors(moduloExpr);
        moduloExpr.getLhs().accept(this);
        moduloExpr.getRhs().accept(this);
        return null;
    }

    @Override
    public Void visit(Equals equalsExpr) {
        printRelatedErrors(equalsExpr);
        equalsExpr.getLhs().accept(this);
        equalsExpr.getRhs().accept(this);
        return null;
    }

    @Override
    public Void visit(GreaterThan gtExpr) {
        printRelatedErrors(gtExpr);
        gtExpr.getLhs().accept(this);
        gtExpr.getRhs().accept(this);
        return null;
    }

    @Override
    public Void visit(LessThan lessThanExpr) {
        printRelatedErrors(lessThanExpr);
        lessThanExpr.getLhs().accept(this);
        lessThanExpr.getRhs().accept(this);
        return null;
    }

    @Override
    public Void visit(And andExpr) {
        printRelatedErrors(andExpr);
        andExpr.getLhs().accept(this);
        andExpr.getRhs().accept(this);
        return null;
    }

    @Override
    public Void visit(Or orExpr) {
        printRelatedErrors(orExpr);
        orExpr.getLhs().accept(this);
        orExpr.getRhs().accept(this);
        return null;
    }

    @Override
    public Void visit(Neg negExpr) {
        printRelatedErrors(negExpr);
        negExpr.getExpr().accept(this);
        return null;
    }

    @Override
    public Void visit(Not notExpr) {
        printRelatedErrors(notExpr);
        notExpr.getExpr().accept(this);
        return null;
    }

    @Override
    public Void visit(MethodCall methodCall) {
        printRelatedErrors(methodCall);
        methodCall.getInstance().accept(this);
        methodCall.getMethodName().accept(this);
        for (Expression arg : methodCall.getArgs()) {
            arg.accept(this);
        }
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
        fieldCall.getInstance().accept(this);
        fieldCall.getField().accept(this);
        return null;
    }

    @Override
    public Void visit(ArrayCall arrayCall) {
        printRelatedErrors(arrayCall);
        arrayCall.getInstance().accept(this);
        arrayCall.getIndex().accept(this);
        return null;
    }

    @Override
    public Void visit(NotEquals notEquals) {
        printRelatedErrors(notEquals);
        notEquals.getLhs().accept(this);
        notEquals.getRhs().accept(this);
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
        parameterDeclaration.getIdentifier().accept(this);
        return null;
    }

    @Override
    public Void visit(MethodDeclaration methodDeclaration) {
        printRelatedErrors(methodDeclaration);
        methodDeclaration.getName().accept(this);
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
        else {
            System.exit( 1 );
        }
        return null;
    }

}

