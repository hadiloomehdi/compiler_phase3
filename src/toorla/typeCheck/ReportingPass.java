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
    }

    @Override
    public Void visit(PrintLine printStat) {
    }

    @Override
    public Void visit(Assign assignStat) {
        return null;
    }

    @Override
    public Void visit(Block block) {
        printRelatedErrors(block);
        for (Statement stmt : block.body)
            stmt.accept(this);
        return;
    }

    @Override
    public Void visit(Conditional conditional) {
        return conditional.getThenStatement().accept(this)
                + conditional.getElseStatement().accept(this);
    }

    @Override
    public Void visit(While whileStat) {
        whileStat.body.accept(this);
    }

    @Override
    public Void visit(Return returnStat) {
        return;
    }

    @Override
    public Void visit(Break breakStat) {

        return 0;
    }

    @Override
    public Void visit(Continue continueStat) {
        return 0;
    }

    @Override
    public Void visit(Skip skip) {
        return 0;
    }

    @Override
    public Void visit(LocalVarDef localVarDef) {
        for (CompileErrorException e : localVarDef.relatedErrors) {
            System.out.println(e);
        }
        return localVarDef.relatedErrors.size();
    }

    @Override
    public Void visit(IncStatement incStatement) {
        return 0;
    }

    @Override
    public Void visit(DecStatement decStatement) {
        return 0;
    }

    @Override
    public Void visit(Plus plusExpr) {
        return 0;
    }

    @Override
    public Void visit(Minus minusExpr) {
        return 0;
    }

    @Override
    public Void visit(Times timesExpr) {
        return 0;
    }

    @Override
    public Void visit(Division divExpr) {
        return 0;
    }

    @Override
    public Void visit(Modulo moduloExpr) {
        return 0;
    }

    @Override
    public Void visit(Equals equalsExpr) {
        return 0;
    }

    @Override
    public Void visit(GreaterThan gtExpr) {
        return 0;
    }

    @Override
    public Void visit(LessThan lessThanExpr) {
        return 0;
    }

    @Override
    public Void visit(And andExpr) {
        return 0;
    }

    @Override
    public Void visit(Or orExpr) {
        return 0;
    }

    @Override
    public Void visit(Neg negExpr) {
        return 0;
    }

    @Override
    public Void visit(Not notExpr) {
        return 0;
    }

    @Override
    public Void visit(MethodCall methodCall) {
        return 0;
    }

    @Override
    public Void visit(Identifier identifier) {
        return 0;
    }

    @Override
    public Void visit(Self self) {
        return 0;
    }

    @Override
    public Void visit(IntValue intValue) {
        return 0;
    }

    @Override
    public Void visit(NewArray newArray) {
        return 0;
    }

    @Override
    public Void visit(BoolValue booleanValue) {
        return 0;
    }

    @Override
    public Void visit(StringValue stringValue) {
        return 0;
    }

    @Override
    public Void visit(NewClassInstance newClassInstance) {
        return 0;
    }

    @Override
    public Void visit(FieldCall fieldCall) {
        return 0;
    }

    @Override
    public Void visit(ArrayCall arrayCall) {
        return 0;
    }

    @Override
    public Void visit(NotEquals notEquals) {
        return 0;
    }

    @Override
    public Void visit(ClassDeclaration classDeclaration) {
        int numOfErrors = classDeclaration.relatedErrors.size();
        for (CompileErrorException e : classDeclaration.relatedErrors) {
            System.out.println(e);
        }
        for (ClassMemberDeclaration cmd : classDeclaration.getClassMembers())
            numOfErrors += cmd.accept(this);
        return numOfErrors;
    }

    @Override
    public Void visit(EntryClassDeclaration entryClassDeclaration) {
        return this.visit((ClassDeclaration) entryClassDeclaration);
    }

    @Override
    public Void visit(FieldDeclaration fieldDeclaration) {
        for (CompileErrorException e : fieldDeclaration.relatedErrors) {
            System.out.println(e);
        }
        return fieldDeclaration.relatedErrors.size();
    }

    @Override
    public Void visit(ParameterDeclaration parameterDeclaration) {
        for (CompileErrorException e : parameterDeclaration.relatedErrors) {
            System.out.println(e);
        }
        return parameterDeclaration.relatedErrors.size();
    }

    @Override
    public Void visit(MethodDeclaration methodDeclaration) {
        int numOfErrors = methodDeclaration.relatedErrors.size();
        for (CompileErrorException e : methodDeclaration.relatedErrors) {
            System.out.println(e);
        }
        for (ParameterDeclaration pd : methodDeclaration.getArgs())
            numOfErrors += pd.accept(this);
        for (Statement stmt : methodDeclaration.getBody())
            numOfErrors += stmt.accept(this);
        return numOfErrors;
    }

    @Override
    public Void visit(LocalVarsDefinitions localVarsDefinitions) {
        int numOfErrors = 0;
        for (LocalVarDef lvd : localVarsDefinitions.getVarDefinitions()) {
            numOfErrors += lvd.accept(this);
        }
        return numOfErrors;
    }

    @Override
    public Void visit(Program program) {
        int numOfErrors = program.relatedErrors.size();
        for (CompileErrorException e : program.relatedErrors) {
            System.out.println(e);
        }
        for (ClassDeclaration cd : program.getClasses()) {
            numOfErrors += cd.accept(this);
        }
        return numOfErrors;
    }

    @Override
    public Void analyze(Program program) {
        int numOfErrors = this.visit(program);
        if( numOfErrors != 0 )
            System.exit( 1 );
    }

    @Override
    public Void getResult() {
        return null;
    }

}

