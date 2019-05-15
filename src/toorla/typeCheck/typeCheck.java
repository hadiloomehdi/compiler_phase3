package toorla.typeCheck;

import toorla.ast.Program;
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
import toorla.nameAnalyzer.compileErrorException.*;
import toorla.symbolTable.SymbolTable;
import toorla.symbolTable.exceptions.ItemAlreadyExistsException;
import toorla.symbolTable.symbolTableItem.ClassSymbolTableItem;
import toorla.symbolTable.symbolTableItem.MethodSymbolTableItem;
import toorla.symbolTable.symbolTableItem.varItems.FieldSymbolTableItem;
import toorla.symbolTable.symbolTableItem.varItems.LocalVariableSymbolTableItem;
import toorla.typeCheck.compileErrorException.ConditionNotBoolean;
import toorla.typeCheck.compileErrorException.PrintArg;
import toorla.typeCheck.compileErrorException.UnsupportOperand;
import toorla.types.Type;
import toorla.types.arrayType.ArrayType;
import toorla.types.singleType.SingleType;
import toorla.visitor.Visitor;
import toorla.types.singleType.*;

import java.util.ArrayList;

public class typeCheck implements Visitor<Void> {
    private boolean inFunc;

    public typeCheck() {
        inFunc = false;
    }

    @Override
    public Void visit(PrintLine printLine) {
        Type type =  printLine.getArg().accept(this);

        if(!(type instanceof ArrayType) && !(type instanceof IntType) && !(type instanceof StringType)){
            PrintArg ee = new PrintArg(printLine.line,printLine.col);
            printLine.relatedErrors.add(ee);
        }
        return null;
    }

    @Override
    public Void visit(Assign assign) {
        Type lValue = assign.getLvalue().accept(this);
        Type rValue = assign.getRvalue().accept(this);

        return null;
    }

    @Override
    public Void visit(Block block) {
        for (Statement s : block.body)
            s.accept(this);
        return null;
    }

    @Override
    public Void visit(Conditional conditional) {
        Type cond = conditional.getCondition().accept(this);
        if (!(cond instanceof BoolType)){
            ConditionNotBoolean ee = new ConditionNotBoolean("Conditional",conditional.line,conditional.col);
            conditional.relatedErrors.add(ee);
        }
        conditional.getThenStatement().accept(this);
        conditional.getElseStatement().accept(this);
        return null;
    }

    @Override
    public Void visit(While whileStat) {
        Type cond = whileStat.expr.accept(this);
        if (!(cond instanceof BoolType)){
            ConditionNotBoolean ee = new ConditionNotBoolean("Loop",whileStat.line,whileStat.col);
            whileStat.relatedErrors.add(ee);
        }
        whileStat.body.accept(this);
        return null;
    }

    @Override
    public Void visit(Return returnStat) {
        returnStat.getReturnedExpr().accept(this);
        return null;
    }

    @Override
    public Void visit(Plus plusExpr) {
        Type lValue = plusExpr.getLhs().accept(this);
        Type rValue = plusExpr.getRhs().accept(this);
        if(rValue.toString() != lValue.toString()){
            UnsupportOperand ee = new UnsupportOperand("+",plusExpr.line,plusExpr.col);
            plusExpr.relatedErrors.add(ee);
        }
        return null;
    }

    @Override
    public Void visit(Minus minusExpr) {
        Type lValue = minusExpr.getLhs().accept(this);
        Type rValue = minusExpr.getRhs().accept(this);
        if(rValue.toString() != lValue.toString()){
            UnsupportOperand ee = new UnsupportOperand("-",minusExpr.line,minusExpr.col);
            minusExpr.relatedErrors.add(ee);
        }
        return null;
    }

    @Override
    public Void visit(Times timesExpr) {
        Type lValue = timesExpr.getLhs().accept(this);
        Type rValue = timesExpr.getRhs().accept(this);
        if(rValue.toString() != lValue.toString()){
            UnsupportOperand ee = new UnsupportOperand("*",timesExpr.line,timesExpr.col);
            timesExpr.relatedErrors.add(ee);
        }
        return null;
    }

    @Override
    public Void visit(Division divisionExpr) {
        Type lValue = divisionExpr.getLhs().accept(this);
        Type rValue = divisionExpr.getRhs().accept(this);
        if(rValue.toString() != lValue.toString()){
            UnsupportOperand ee = new UnsupportOperand("/",divisionExpr.line,divisionExpr.col);
            divisionExpr.relatedErrors.add(ee);
        }
        return null;
    }

    @Override
    public Void visit(Modulo moduloExpr) {
        Type lValue = moduloExpr.getLhs().accept(this);
        Type rValue = moduloExpr.getRhs().accept(this);
        if(rValue.toString() != lValue.toString()){
            UnsupportOperand ee = new UnsupportOperand("%",moduloExpr.line,moduloExpr.col);
            moduloExpr.relatedErrors.add(ee);
        }
        return null;
    }

    @Override
    public Void visit(Equals equalsExpr) {
        Type lValue = equalsExpr.getLhs().accept(this);
        Type rValue = equalsExpr.getRhs().accept(this);
        if(rValue.toString() != lValue.toString()){
            UnsupportOperand ee = new UnsupportOperand("=",equalsExpr.line,equalsExpr.col);
            equalsExpr.relatedErrors.add(ee);
        }
        return null;
    }

    @Override
    public Void visit(GreaterThan gtExpr) {
        Type lValue = gtExpr.getLhs().accept(this);
        Type rValue = gtExpr.getRhs().accept(this);
        if(rValue.toString() != lValue.toString()){
            UnsupportOperand ee = new UnsupportOperand("=",equalsExpr.line,equalsExpr.col);
            equalsExpr.relatedErrors.add(ee);
        }
        return null;
    }

    @Override
    public Void visit(LessThan ltExpr) {
        Type lValue = ltExpr.getLhs().accept(this);
        Type rValue = ltExpr.getRhs().accept(this);
        if(rValue.toString() != lValue.toString()){
            UnsupportOperand ee = new UnsupportOperand("=",equalsExpr.line,equalsExpr.col);
            equalsExpr.relatedErrors.add(ee);
        }
        return null;
    }

    @Override
    public Void visit(And andExpr) {
        Type lValue = andExpr.getLhs().accept(this);
        Type rValue = andExpr.getRhs().accept(this);
        if(rValue.toString() != lValue.toString()){
            UnsupportOperand ee = new UnsupportOperand("=",equalsExpr.line,equalsExpr.col);
            equalsExpr.relatedErrors.add(ee);
        }
        return null;
    }

    @Override
    public Void visit(Or orExpr) {
        Type lValue = orExpr.getLhs().accept(this);
        Type rValue = orExpr.getRhs().accept(this);
        if(rValue.toString() != lValue.toString()){
            UnsupportOperand ee = new UnsupportOperand("=",equalsExpr.line,equalsExpr.col);
            equalsExpr.relatedErrors.add(ee);
        }
        return null;
    }

    @Override
    public Void visit(Neg negExpr) {
        negExpr.getExpr().accept(this);
        return null;
    }

    @Override
    public Void visit(Not notExpr) {
        notExpr.getExpr().accept(this);
        return null;
    }

    @Override
    public Void visit(MethodCall methodCall) {
        methodCall.getInstance().accept(this);
        methodCall.getMethodName().accept(this);
        for (Expression arg : methodCall.getArgs()) {
            arg.accept(this);
        }
        return null;
    }

    @Override
    public Void visit(Identifier identifier) {

        return null;
    }

    @Override
    public Void visit(Self self) {
        return null;
    }

    @Override
    public Void visit(Break breakStat) {
        return null;
    }

    @Override
    public Void visit(Continue continueStat) {
        return null;
    }

    @Override
    public Void visit(Skip skip) {
        return null;
    }

    @Override
    public Void visit(IntValue intValue) {
        return null;
    }

    @Override
    public Void visit(NewArray newArray) {
        newArray.getLength().accept(this);
        return null;
    }

    @Override
    public Void visit(BoolValue booleanValue) {
        return null;
    }

    @Override
    public Void visit(StringValue stringValue) {
        return null;
    }

    @Override
    public Void visit(NewClassInstance newClassInstance) {
        newClassInstance.getClassName().accept(this);
        return null;
    }

    @Override
    public Void visit(FieldCall fieldCall) {
        fieldCall.getInstance().accept(this);
        fieldCall.getField().accept(this);
        return null;
    }

    @Override
    public Void visit(ArrayCall arrayCall) {
        arrayCall.getInstance().accept(this);
        arrayCall.getIndex().accept(this);
        return null;
    }

    @Override
    public Void visit(NotEquals notEquals) {
        notEquals.getLhs().accept(this);
        notEquals.getRhs().accept(this);
        return null;
    }

    @Override
    public Void visit(LocalVarDef localVarDef) {
        localVarDef.getLocalVarName().accept(this);
        localVarDef.getInitialValue().accept(this);
        return null;
    }

    @Override
    public Void visit(IncStatement incStatement) {
        incStatement.getOperand().accept(this);
        return null;
    }

    @Override
    public Void visit(DecStatement decStatement) {
        decStatement.getOperand().accept(this);
        return null;
    }

    @Override
    public Void visit(ClassDeclaration classDeclaration) {
        visitClassBody(classDeclaration);
        return null;
    }

    private void visitClassBody(ClassDeclaration classDeclaration) {
        classDeclaration.getName().accept(this);
        if (classDeclaration.getParentName().getName() != null) {
            classDeclaration.getParentName().accept(this);
        }
        for (ClassMemberDeclaration md : classDeclaration.getClassMembers())
            md.accept(this);
    }

    @Override
    public Void visit(EntryClassDeclaration entryClassDeclaration) {
        visitClassBody(entryClassDeclaration);
        return null;
    }

    @Override
    public Void visit(FieldDeclaration fieldDeclaration) {
        fieldDeclaration.getIdentifier().accept(this);
        return null;
    }

    @Override
    public Void visit(ParameterDeclaration parameterDeclaration) {
        parameterDeclaration.getIdentifier().accept(this);
        return null;
    }

    @Override
    public Void visit(MethodDeclaration methodDeclaration) {
        methodDeclaration.getName().accept(this);
        for (ParameterDeclaration pd : methodDeclaration.getArgs()) {
            pd.accept(this);
        }
        inFunc = true;
        for (Statement stmt : methodDeclaration.getBody())
            stmt.accept(this);
        return null;
    }

    @Override
    public Void visit(LocalVarsDefinitions localVarsDefinitions) {
        for (LocalVarDef lvd : localVarsDefinitions.getVarDefinitions())
            lvd.accept(this);
        return null;
    }

    @Override
    public Void visit(Program program) {
        for (ClassDeclaration cd : program.getClasses())
            cd.accept(this);
        return null;
    }
}
