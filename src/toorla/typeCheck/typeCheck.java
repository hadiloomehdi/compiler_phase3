package toorla.typeCheck;

import com.sun.org.apache.xpath.internal.operations.Bool;
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
import toorla.nameAnalyzer.compileErrorException.*;
import toorla.symbolTable.SymbolTable;
import toorla.symbolTable.exceptions.ItemAlreadyExistsException;
import toorla.symbolTable.exceptions.ItemNotFoundException;
import toorla.symbolTable.symbolTableItem.ClassSymbolTableItem;
import toorla.symbolTable.symbolTableItem.MethodSymbolTableItem;
import toorla.symbolTable.symbolTableItem.SymbolTableItem;
import toorla.symbolTable.symbolTableItem.varItems.FieldSymbolTableItem;
import toorla.symbolTable.symbolTableItem.varItems.LocalVariableSymbolTableItem;
import toorla.symbolTable.symbolTableItem.varItems.VarSymbolTableItem;
import toorla.typeCheck.compileErrorException.*;
import toorla.types.Type;
import toorla.types.UndefinedType;
import toorla.types.arrayType.ArrayType;
import toorla.types.singleType.SingleType;
import toorla.visitor.Visitor;
import toorla.types.singleType.*;

import java.util.ArrayList;

public class typeCheck implements Visitor<Type> {
    private boolean inFunc;
    private Integer loopDep;
    private Integer numVar;
    public typeCheck() {
        inFunc = false;
    }

    @Override
    public Type visit(PrintLine printLine) {
        Type type =  printLine.getArg().accept(this);
        Boolean intArray = false;
        if (type instanceof ArrayType){
            ArrayType arrayType = (ArrayType)type;
            if(arrayType.getSingleType().toString() == "(IntType)"){
                intArray = true;
            }
        }
        if( !intArray && !(type instanceof IntType) && !(type instanceof StringType)){
            PrintArg ee = new PrintArg(printLine.line,printLine.col);
            printLine.relatedErrors.add(ee);
        }
        return null;
    }

    @Override
    public Type visit(Assign assign) {
        Type lValue = assign.getLvalue().accept(this);
        Type rValue = assign.getRvalue().accept(this);

        return null;
    }

    @Override
    public Type visit(Block block) {
        loopDep+=1;
        for (Statement s : block.body)
            s.accept(this);
        loopDep-=1;
        return null;
    }

    @Override
    public Type visit(Conditional conditional) {
        Type cond = conditional.getCondition().accept(this);
        if (!(cond instanceof BoolType)){
            ConditionNotBoolean ee = new ConditionNotBoolean(conditional.toString(),conditional.line,conditional.col);
            conditional.relatedErrors.add(ee);
        }
        conditional.getThenStatement().accept(this);
        conditional.getElseStatement().accept(this);
        return null;
    }

    @Override
    public Type visit(While whileStat) {
        Type cond = whileStat.expr.accept(this);
        if (!(cond instanceof BoolType)){
            ConditionNotBoolean ee = new ConditionNotBoolean(whileStat.toString(),whileStat.line,whileStat.col);
            whileStat.relatedErrors.add(ee);
        }
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
        Type lValue = plusExpr.getLhs().accept(this);
        Type rValue = plusExpr.getRhs().accept(this);
        if(!(rValue instanceof IntType) && !(lValue instanceof IntType)){
            UnsupportOperand ee = new UnsupportOperand(plusExpr.toString(),plusExpr.line,plusExpr.col);
            plusExpr.relatedErrors.add(ee);
        }
        return null;
    }

    @Override
    public Type visit(Minus minusExpr) {
        Type lValue = minusExpr.getLhs().accept(this);
        Type rValue = minusExpr.getRhs().accept(this);
        if(!(rValue instanceof IntType) && !(lValue instanceof IntType)){
            UnsupportOperand ee = new UnsupportOperand(minusExpr.toString(),minusExpr.line,minusExpr.col);
            minusExpr.relatedErrors.add(ee);
        }
        return null;
    }

    @Override
    public Type visit(Times timesExpr) {
        Type lValue = timesExpr.getLhs().accept(this);
        Type rValue = timesExpr.getRhs().accept(this);
        if(!(rValue instanceof IntType ) && !(lValue instanceof IntType)){
            UnsupportOperand ee = new UnsupportOperand(timesExpr.toString(),timesExpr.line,timesExpr.col);
            timesExpr.relatedErrors.add(ee);
        }
        return null;
    }

    @Override
    public Type visit(Division divisionExpr) {
        Type lValue = divisionExpr.getLhs().accept(this);
        Type rValue = divisionExpr.getRhs().accept(this);
        if(!(rValue instanceof IntType ) && !(lValue instanceof IntType)){
            UnsupportOperand ee = new UnsupportOperand(divisionExpr.toString(),divisionExpr.line,divisionExpr.col);
            divisionExpr.relatedErrors.add(ee);
        }
        return null;
    }

    @Override
    public Type visit(Modulo moduloExpr) {
        Type lValue = moduloExpr.getLhs().accept(this);
        Type rValue = moduloExpr.getRhs().accept(this);
        if(!(rValue instanceof IntType) && !(lValue instanceof IntType)){
            UnsupportOperand ee = new UnsupportOperand(moduloExpr.toString(),moduloExpr.line,moduloExpr.col);
            moduloExpr.relatedErrors.add(ee);
        }
        return null;
    }

    @Override
    public Type visit(Equals equalsExpr) {
        Type lValue = equalsExpr.getLhs().accept(this);
        Type rValue = equalsExpr.getRhs().accept(this);
        if(rValue.toString() != lValue.toString()){
            UnsupportOperand ee = new UnsupportOperand(equalsExpr.toString(),equalsExpr.line,equalsExpr.col);////array
            equalsExpr.relatedErrors.add(ee);
        }
        return null;
    }

    @Override
    public Type visit(GreaterThan gtExpr) {
        Type lValue = gtExpr.getLhs().accept(this);
        Type rValue = gtExpr.getRhs().accept(this);
        if(!(rValue instanceof IntType) && !(lValue instanceof IntType)){
            UnsupportOperand ee = new UnsupportOperand(gtExpr.toString(),gtExpr.line,gtExpr.col);
            gtExpr.relatedErrors.add(ee);
        }
        return null;
    }

    @Override
    public Type visit(LessThan ltExpr) {
        Type lValue = ltExpr.getLhs().accept(this);
        Type rValue = ltExpr.getRhs().accept(this);
        if(!(rValue instanceof IntType) && !(lValue instanceof IntType)){
            UnsupportOperand ee = new UnsupportOperand(ltExpr.toString(),ltExpr.line,ltExpr.col);
            ltExpr.relatedErrors.add(ee);
        }
        return null;
    }

    @Override
    public Type visit(And andExpr) {
        Type lValue = andExpr.getLhs().accept(this);
        Type rValue = andExpr.getRhs().accept(this);
        if(!(rValue instanceof BoolType) && !(lValue instanceof BoolType)){
            UnsupportOperand ee = new UnsupportOperand(andExpr.toString(),andExpr.line,andExpr.col);
            andExpr.relatedErrors.add(ee);
        }
        return null;
    }

    @Override
    public Type visit(Or orExpr) {
        Type lValue = orExpr.getLhs().accept(this);
        Type rValue = orExpr.getRhs().accept(this);
        if(!(rValue instanceof BoolType) && !(lValue instanceof BoolType)){
            UnsupportOperand ee = new UnsupportOperand(orExpr.toString(),orExpr.line,orExpr.col);
            orExpr.relatedErrors.add(ee);
        }
        return null;
    }

    @Override
    public Type visit(Neg negExpr) {
        Type type = negExpr.getExpr().accept(this);
        if (!(type instanceof IntType)) {
            UnsupportOperand ee = new UnsupportOperand(negExpr.toString(),negExpr.line, negExpr.col);
            negExpr.relatedErrors.add(ee);
        }
        return null;
    }

    @Override
    public Type visit(Not notExpr) {
        Type type = notExpr.getExpr().accept(this);
        if (!(type instanceof BoolType)) {
            UnsupportOperand ee = new UnsupportOperand(notExpr.toString(),notExpr.line, notExpr.col);
            notExpr.relatedErrors.add(ee);
        }
        return null;
    }

    public Boolean objectIsValid(Expression memberCall, Type exprType, String memberName) {
        if (!(exprType instanceof UserDefinedType || (exprType instanceof ArrayType && memberName.equals("length")))) {
            UnsupportOperand exc = new UnsupportOperand(memberCall.toString(), memberCall.line, memberCall.col);
            memberCall.relatedErrors.add(exc);
            return true;
        }
        return false;
    }

    public Boolean foundClass(Tree node, String className) {
        SymbolTable root = SymbolTable.root;
        try {
            root.get(className);
        } catch (ItemNotFoundException excep) {
            ClassNotDef exc = new ClassNotDef(className, node.line, node.col);
            node.relatedErrors.add(exc);
            return false;
        }
        return true;
    }

    public Boolean fieldMethodCallCheck(Expression memberCall, Type exprType, String memberName) {
        if (!objectIsValid(memberCall, exprType, memberName))
            return false;
        else {
            UserDefinedType exprUserType = (UserDefinedType)exprType;
            return foundClass(memberCall, exprUserType.getClassDeclaration().getName().getName());
        }
    }

    @Override
    public Type visit(MethodCall methodCall) {
        Type exprType = methodCall.getInstance().accept(this);
        Boolean unDef = !fieldMethodCallCheck(methodCall, exprType, methodCall.getMethodName().getName());
        Type methodType = methodCall.getMethodName().accept(this);
        for (Expression arg : methodCall.getArgs()) {
            arg.accept(this);
        }
        if (unDef)
            return new UndefinedType();
        else
            return methodType;
    }

    @Override
    public Type visit(Identifier identifier) {
        try{
            SymbolTableItem sti =  SymbolTable.top().get(identifier.getName());
            if (sti instanceof FieldSymbolTableItem){
                return ((FieldSymbolTableItem) sti).getVarType();
            }
            else if (sti instanceof LocalVariableSymbolTableItem){
                LocalVariableSymbolTableItem LVST = (LocalVariableSymbolTableItem) sti;

                if (LVST.getIndex() > numVar){
                    VariableNotDeclared ee = new VariableNotDeclared(identifier.getName(),identifier.line,identifier.col);
                    identifier.relatedErrors.add(ee);
                }
                else
                    return LVST.getVarType();
            }
        }catch(Exception e){

        }

        return null;
    }

    @Override
    public Type visit(Self self) {
        return null;
    }

    @Override
    public Type visit(Break breakStat) {
        if (inFunc && loopDep==0) {
            BreakContinue exc = new BreakContinue(breakStat.toString(), breakStat.line, breakStat.col);
            breakStat.relatedErrors.add(exc);
        }
        return null;
    }

    @Override
    public Type visit(Continue continueStat) {
        if (inFunc && loopDep==0) {
            BreakContinue exc = new BreakContinue(continueStat.toString(), continueStat.line, continueStat.col);
            continueStat.relatedErrors.add(exc);
        }
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
        Type type = newArray.getLength().accept(this);
        if (!(type instanceof IntType)) {
            ArraySize exc = new ArraySize(newArray.line, newArray.col);
            newArray.relatedErrors.add(exc);
        }
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
        Type exprType = fieldCall.getInstance().accept(this);
        Boolean unDef = !fieldMethodCallCheck(fieldCall, exprType, fieldCall.getField().getName());
        Type methodType = fieldCall.getField().accept(this);
        if (unDef)
            return new UndefinedType();
        else
            return methodType;
    }

    @Override
    public Type visit(ArrayCall arrayCall) {
        Type exprType = arrayCall.getInstance().accept(this);
        Type indType = arrayCall.getIndex().accept(this);
        if (!(exprType instanceof ArrayType && indType instanceof IntType)) {
            UnsupportOperand exc = new UnsupportOperand(arrayCall.toString(), arrayCall.line, arrayCall.col);
            arrayCall.relatedErrors.add(exc);
        }
        return null;
    }

    @Override
    public Type visit(NotEquals notEquals) {
        Type lValue = notEquals.getLhs().accept(this);
        Type rValue = notEquals.getRhs().accept(this);
        if(rValue.toString() != lValue.toString()){
            UnsupportOperand ee = new UnsupportOperand(notEquals.toString(),notEquals.line,notEquals.col);
            notEquals.relatedErrors.add(ee);
        }
        return null;
    }

    @Override
    public Type visit(LocalVarDef localVarDef) {
        localVarDef.getLocalVarName().accept(this);
        Type type = localVarDef.getInitialValue().accept(this);
        try{
            LocalVariableSymbolTableItem localVarSybmbolTable =  (LocalVariableSymbolTableItem)SymbolTable.top().get(VarSymbolTableItem.var_modifier + localVarDef.getLocalVarName().getName());
            localVarSybmbolTable.setVarType(type);
        }catch (Exception e){

        }
        return null;
    }

    @Override
    public Type visit(IncStatement incStatement) {
        Type type = incStatement.getOperand().accept(this);

        Expression ex = incStatement.getOperand();
        if (!((ex instanceof FieldCall) || (ex instanceof Identifier && inFunc) || (ex instanceof ArrayCall))){
            RvalueDecInc ee = new RvalueDecInc(incStatement.toString(),incStatement.line,incStatement.col);
            incStatement.relatedErrors.add(ee);
        }

        if(!(type instanceof IntType )){
            UnsupportOperand ee = new UnsupportOperand(incStatement.toString(),incStatement.line,incStatement.col);
            incStatement.relatedErrors.add(ee);
        }
        return null;
    }

    @Override
    public Type visit(DecStatement decStatement) {
        Type type = decStatement.getOperand().accept(this);

        Expression ex = decStatement.getOperand();
        if (!((ex instanceof FieldCall) || (ex instanceof Identifier && inFunc) || (ex instanceof ArrayCall))){
            RvalueDecInc ee = new RvalueDecInc(decStatement.toString(),decStatement.line,decStatement.col);
            decStatement.relatedErrors.add(ee);
        }
        if(!(type instanceof IntType )){
            UnsupportOperand ee = new UnsupportOperand(decStatement.toString(),decStatement.line,decStatement.col);
            decStatement.relatedErrors.add(ee);
        }
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
            if (!foundClass(classDeclaration, classDeclaration.getParentName().getName())) {
                ClassNotDef exc = new ClassNotDef(classDeclaration.getParentName().getName(), classDeclaration.line, classDeclaration.col);
                classDeclaration.relatedErrors.add(exc);
            }
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
        numVar =-1;
        methodDeclaration.getName().accept(this);
        for (ParameterDeclaration pd : methodDeclaration.getArgs()) {
            pd.accept(this);
            numVar +=1;
        }
        inFunc = true;
        loopDep = 0;
        for (Statement stmt : methodDeclaration.getBody())
            stmt.accept(this);
        inFunc = false;
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
