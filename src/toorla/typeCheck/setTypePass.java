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
import toorla.types.Type;
import toorla.types.singleType.BoolType;
import toorla.types.singleType.IntType;
import toorla.types.singleType.StringType;
import toorla.visitor.Visitor;

public class setTypePass implements Visitor<Type> {

    @Override
    public Type visit(PrintLine printLine) {
        printLine.getArg().accept(this);
        return null;
    }

    @Override
    public Type visit(Assign assign) {
        assign.getLvalue().accept(this);
        assign.getRvalue().accept(this);
        return null;
    }

    @Override
    public Type visit(Block block) {
        for (Statement s : block.body)
            s.accept(this);
        return null;
    }

    @Override
    public Type visit(Conditional conditional) {
        conditional.getCondition().accept(this);
        conditional.getThenStatement().accept(this);
        conditional.getElseStatement().accept(this);
        return null;
    }

    @Override
    public Type visit(While whileStat) {
        whileStat.expr.accept(this);
        whileStat.body.accept(this);
        return null;
    }

    @Override
    public Type visit(Return returnStat) {
        returnStat.getReturnedExpr().accept(this);
        return null;
    }

    @Override
    public Type visit(Plus plusExpr) {
        Type lType = plusExpr.getLhs().accept(this);
        Type rType = plusExpr.getRhs().accept(this);
        return new IntType();
    }

    @Override
    public Type visit(Minus minusExpr) {
        minusExpr.getLhs().accept(this);
        minusExpr.getRhs().accept(this);
        return null;
    }

    @Override
    public Type visit(Times timesExpr) {
        timesExpr.getLhs().accept(this);
        timesExpr.getRhs().accept(this);
        return null;
    }

    @Override
    public Type visit(Division divisionExpr) {
        divisionExpr.getLhs().accept(this);
        divisionExpr.getRhs().accept(this);
        return null;
    }

    @Override
    public Type visit(Modulo moduloExpr) {
        moduloExpr.getLhs().accept(this);
        moduloExpr.getRhs().accept(this);
        return null;
    }

    @Override
    public Type visit(Equals equalsExpr) {
        equalsExpr.getLhs().accept(this);
        equalsExpr.getRhs().accept(this);
        return null;
    }

    @Override
    public Type visit(GreaterThan gtExpr) {
        gtExpr.getLhs().accept(this);
        gtExpr.getRhs().accept(this);
        return null;
    }

    @Override
    public Type visit(LessThan ltExpr) {
        ltExpr.getLhs().accept(this);
        ltExpr.getRhs().accept(this);
        return null;
    }

    @Override
    public Type visit(And andExpr) {
        andExpr.getLhs().accept(this);
        andExpr.getRhs().accept(this);
        return null;
    }

    @Override
    public Type visit(Or orExpr) {
        orExpr.getLhs().accept(this);
        orExpr.getRhs().accept(this);
        return null;
    }

    @Override
    public Type visit(Neg negExpr) {
        negExpr.getExpr().accept(this);
        return null;
    }

    @Override
    public Type visit(Not notExpr) {
        notExpr.getExpr().accept(this);
        return null;
    }

    @Override
    public Type visit(MethodCall methodCall) {
        methodCall.getInstance().accept(this);
        methodCall.getMethodName().accept(this);
        for (Expression arg : methodCall.getArgs()) {
            arg.accept(this);
        }
        return null;
    }

    @Override
    public Type visit(Identifier identifier) {

        return null;
    }

    @Override
    public Type visit(Self self) {
        return null;
    }

    @Override
    public Type visit(Break breakStat) {
        return null;
    }

    @Override
    public Type visit(Continue continueStat) {
        return null;
    }

    @Override
    public Type visit(Skip skip) {
        return null;
    }

    @Override
    public Type visit(IntValue intValue) {
        return new IntType();
    }

    @Override
    public Type visit(NewArray newArray) {
        newArray.getLength().accept(this);
        return null;
    }

    @Override
    public Type visit(BoolValue booleanValue) {
        return new BoolType();
    }

    @Override
    public Type visit(StringValue stringValue) {
        return new StringType();
    }

    @Override
    public Type visit(NewClassInstance newClassInstance) {
        newClassInstance.getClassName().accept(this);
        return null;
    }

    @Override
    public Type visit(FieldCall fieldCall) {
        fieldCall.getInstance().accept(this);
        fieldCall.getField().accept(this);
        return null;
    }

    @Override
    public Type visit(ArrayCall arrayCall) {
        arrayCall.getInstance().accept(this);
        arrayCall.getIndex().accept(this);
        return null;
    }

    @Override
    public Type visit(NotEquals notEquals) {
        notEquals.getLhs().accept(this);
        notEquals.getRhs().accept(this);
        return null;
    }

    @Override
    public Type visit(LocalVarDef localVarDef) {
        localVarDef.getLocalVarName().accept(this);
        localVarDef.getInitialValue().accept(this);
        return null;
    }

    @Override
    public Type visit(IncStatement incStatement) {
        incStatement.getOperand().accept(this);
        return null;
    }

    @Override
    public Type visit(DecStatement decStatement) {
        decStatement.getOperand().accept(this);
        return null;
    }

    @Override
    public Type visit(ClassDeclaration classDeclaration) {
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
    public Type visit(EntryClassDeclaration entryClassDeclaration) {
        visitClassBody(entryClassDeclaration);
        return null;
    }

    @Override
    public Type visit(FieldDeclaration fieldDeclaration) {
        fieldDeclaration.getIdentifier().accept(this);
        return null;
    }

    @Override
    public Type visit(ParameterDeclaration parameterDeclaration) {
        parameterDeclaration.getIdentifier().accept(this);
        return null;
    }

    @Override
    public Type visit(MethodDeclaration methodDeclaration) {
        methodDeclaration.getName().accept(this);
        for (ParameterDeclaration pd : methodDeclaration.getArgs()) {
            pd.accept(this);
        }
        for (Statement stmt : methodDeclaration.getBody())
            stmt.accept(this);
        return null;
    }

    @Override
    public Type visit(LocalVarsDefinitions localVarsDefinitions) {
        for (LocalVarDef lvd : localVarsDefinitions.getVarDefinitions())
            lvd.accept(this);
        return null;
    }

    @Override
    public Type visit(Program program) {
        for (ClassDeclaration cd : program.getClasses())
            cd.accept(this);
        return null;
    }
}
