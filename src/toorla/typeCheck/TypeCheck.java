package toorla.typeCheck;

import toorla.ast.Program;
import toorla.ast.Tree;
import toorla.ast.declaration.classDecs.ClassDeclaration;
import toorla.ast.declaration.classDecs.EntryClassDeclaration;
import toorla.ast.declaration.classDecs.classMembersDecs.AccessModifier;
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
import toorla.typeCheck.ClassMemberNotFoundException;
import toorla.typeCheck.Graph.ParentGraph;
import toorla.typeCheck.Graph.ParentNotFoundException;
import toorla.typeCheck.compileErrorException.*;
import toorla.types.AnonymousType;
import toorla.types.NoType;
import toorla.types.Type;
import toorla.types.UndefinedType;
import toorla.types.arrayType.ArrayType;
import toorla.types.singleType.SingleType;
import toorla.visitor.Visitor;
import toorla.types.singleType.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypeCheck implements Visitor<Type> {
    private Program program;
    private boolean inFunc;
    private Integer loopDep;
    private ParentGraph parents;
    Map<String, ClassDeclaration> classNameMap;

    public TypeCheck(Program program) {
        inFunc = false;
        parents = new ParentGraph(program);
        this.program = program;
        classNameMap = new HashMap<>();
    }

    @Override
    public Type visit(PrintLine printLine) {
        Type type =  printLine.getArg().accept(this);
        Boolean intArray = false;
        if (type instanceof ArrayType){
            ArrayType arrayType = (ArrayType)type;
            if(arrayType.getSingleType() instanceof IntType)
                intArray = true;
        }
        if( !((type instanceof UndefinedType) || intArray || (type instanceof IntType) || (type instanceof StringType)) ) {
            PrintArg ee = new PrintArg(printLine.line,printLine.col);
            printLine.relatedErrors.add(ee);
        }
        return new NoType();
    }

    @Override
    public Type visit(Block block) {
        SymbolTable.pushFromQueue();
        for (Statement s : block.body)
            s.accept(this);
        SymbolTable.pop();
        return new NoType();
    }

    @Override
    public Type visit(Conditional conditional) {
        Type cond = conditional.getCondition().accept(this);
        if (!(cond instanceof BoolType || cond instanceof UndefinedType)){
            ConditionNotBoolean ee = new ConditionNotBoolean(conditional.toString(),conditional.getCondition().line,conditional.getCondition().col);
            conditional.relatedErrors.add(ee);
        }
        SymbolTable.pushFromQueue();
        conditional.getThenStatement().accept(this);
        SymbolTable.pop();
        SymbolTable.pushFromQueue();
        conditional.getElseStatement().accept(this);
        SymbolTable.pop();
        return new NoType();
    }

    @Override
    public Type visit(While whileStat) {
        Type cond = whileStat.expr.accept(this);
        if (!(cond instanceof BoolType)){
            ConditionNotBoolean ee = new ConditionNotBoolean(whileStat.toString(),whileStat.expr.line, whileStat.expr.col);
            whileStat.relatedErrors.add(ee);
        }
        SymbolTable.pushFromQueue();
        loopDep += 1;
        whileStat.body.accept(this);
        loopDep -= 1;
        SymbolTable.pop();
        return new NoType();
    }

    @Override
    public Type visit(Return returnStat) {
        Type exprType = returnStat.getReturnedExpr().accept(this);
        Type retType = new NoType();
        try {
            retType = ((LocalVariableSymbolTableItem)SymbolTable.top().get("var_#RET")).getVarType();
        } catch (ItemNotFoundException exc) {
            // nothing
        }
        if (!isSubType(exprType, retType)) {
            ReturnType exc = new ReturnType(retType.toString(), returnStat.line, returnStat.col);
            returnStat.relatedErrors.add(exc);
        }
        return new NoType();
    }

    @Override
    public Type visit(Plus plusExpr) {
        Type lValue = plusExpr.getLhs().accept(this);
        Type rValue = plusExpr.getRhs().accept(this);
        if (lValue instanceof UndefinedType || rValue instanceof UndefinedType)
            return new UndefinedType();
        if(!(rValue instanceof IntType && lValue instanceof IntType)){
            UnsupportOperand ee = new UnsupportOperand(plusExpr.toString(),plusExpr.line,plusExpr.col);
            plusExpr.relatedErrors.add(ee);
            return new UndefinedType();
        }
        return new IntType();
    }

    @Override
    public Type visit(Minus minusExpr) {
        Type lValue = minusExpr.getLhs().accept(this);
        Type rValue = minusExpr.getRhs().accept(this);
        if (lValue instanceof UndefinedType || rValue instanceof UndefinedType)
            return new UndefinedType();
        if(!(rValue instanceof IntType && lValue instanceof IntType)){
            UnsupportOperand ee = new UnsupportOperand(minusExpr.toString(),minusExpr.line,minusExpr.col);
            minusExpr.relatedErrors.add(ee);
            return new UndefinedType();
        }
        return new IntType();
    }

    @Override
    public Type visit(Times timesExpr) {
        Type lValue = timesExpr.getLhs().accept(this);
        Type rValue = timesExpr.getRhs().accept(this);
        if (lValue instanceof UndefinedType || rValue instanceof UndefinedType)
            return new UndefinedType();
        if(!(rValue instanceof IntType  && lValue instanceof IntType)){
            UnsupportOperand ee = new UnsupportOperand(timesExpr.toString(),timesExpr.line,timesExpr.col);
            timesExpr.relatedErrors.add(ee);
            return new UndefinedType();
        }
        return new IntType();
    }

    @Override
    public Type visit(Division divisionExpr) {
        Type lValue = divisionExpr.getLhs().accept(this);
        Type rValue = divisionExpr.getRhs().accept(this);
        if (lValue instanceof UndefinedType || rValue instanceof UndefinedType)
            return new UndefinedType();
        if(!(rValue instanceof IntType  && lValue instanceof IntType)){
            UnsupportOperand ee = new UnsupportOperand(divisionExpr.toString(),divisionExpr.line,divisionExpr.col);
            divisionExpr.relatedErrors.add(ee);
            return new UndefinedType();
        }
        return new IntType();
    }

    @Override
    public Type visit(Modulo moduloExpr) {
        Type lValue = moduloExpr.getLhs().accept(this);
        Type rValue = moduloExpr.getRhs().accept(this);
        if (lValue instanceof UndefinedType || rValue instanceof UndefinedType)
            return new UndefinedType();
        if(!(rValue instanceof IntType && lValue instanceof IntType)){
            UnsupportOperand ee = new UnsupportOperand(moduloExpr.toString(),moduloExpr.line,moduloExpr.col);
            moduloExpr.relatedErrors.add(ee);
            return new UndefinedType();
        }
        return new IntType();
    }

    public boolean isSameObj(Type a, Type b)  {
        if (!(a instanceof UserDefinedType && b instanceof UserDefinedType))
            return false;
        String aName = ((UserDefinedType)a).getClassDeclaration().getName().getName();
        String bName = ((UserDefinedType)a).getClassDeclaration().getName().getName();
        return aName.equals(bName);
    }

    @Override
    public Type visit(Equals equalsExpr) {
        Type lValue = equalsExpr.getLhs().accept(this);
        Type rValue = equalsExpr.getRhs().accept(this);
        if (lValue instanceof UndefinedType || rValue instanceof UndefinedType)
            return new UndefinedType();
        if (!(hasSamePrimitiveType(lValue , rValue) || isSameObj(lValue, rValue))) {
            UnsupportOperand ee = new UnsupportOperand(equalsExpr.toString(),equalsExpr.line,equalsExpr.col);////array
            equalsExpr.relatedErrors.add(ee);
            return new UndefinedType();
        }
        return new BoolType();
    }

    @Override
    public Type visit(GreaterThan gtExpr) {
        Type lValue = gtExpr.getLhs().accept(this);
        Type rValue = gtExpr.getRhs().accept(this);
        if (lValue instanceof UndefinedType || rValue instanceof UndefinedType)
            return new UndefinedType();
        if(!(rValue instanceof IntType && lValue instanceof IntType)){
            UnsupportOperand ee = new UnsupportOperand(gtExpr.toString(),gtExpr.line,gtExpr.col);
            gtExpr.relatedErrors.add(ee);
            return new UndefinedType();
        }
        return new BoolType();
    }

    @Override
    public Type visit(LessThan ltExpr) {
        Type lValue = ltExpr.getLhs().accept(this);
        Type rValue = ltExpr.getRhs().accept(this);
        if (lValue instanceof UndefinedType || rValue instanceof UndefinedType)
            return new UndefinedType();
        if(!(rValue instanceof IntType && lValue instanceof IntType)){
            UnsupportOperand ee = new UnsupportOperand(ltExpr.toString(),ltExpr.line,ltExpr.col);
            ltExpr.relatedErrors.add(ee);
            return new UndefinedType();
        }
        return new BoolType();
    }

    @Override
    public Type visit(And andExpr) {
        Type lValue = andExpr.getLhs().accept(this);
        Type rValue = andExpr.getRhs().accept(this);
        if (lValue instanceof UndefinedType || rValue instanceof UndefinedType)
            return new UndefinedType();
        if(!(rValue instanceof BoolType && lValue instanceof BoolType)){
            UnsupportOperand ee = new UnsupportOperand(andExpr.toString(),andExpr.line,andExpr.col);
            andExpr.relatedErrors.add(ee);
            return new UndefinedType();
        }
        return new BoolType();
    }

    @Override
    public Type visit(Or orExpr) {
        Type lValue = orExpr.getLhs().accept(this);
        Type rValue = orExpr.getRhs().accept(this);
        if (lValue instanceof UndefinedType || rValue instanceof UndefinedType)
            return new UndefinedType();
        if(!(rValue instanceof BoolType && lValue instanceof BoolType)){
            UnsupportOperand ee = new UnsupportOperand(orExpr.toString(),orExpr.line,orExpr.col);
            orExpr.relatedErrors.add(ee);
            return new UndefinedType();
        }
        return new BoolType();
    }

    @Override
    public Type visit(Neg negExpr) {
        Type type = negExpr.getExpr().accept(this);
        if (type instanceof UndefinedType)
            return new UndefinedType();
        if (!(type instanceof IntType)) {
            UnsupportOperand ee = new UnsupportOperand(negExpr.toString(),negExpr.line, negExpr.col);
            negExpr.relatedErrors.add(ee);
            return new UndefinedType();
        }
        return new IntType();
    }

    @Override
    public Type visit(Not notExpr) {
        Type type = notExpr.getExpr().accept(this);
        if (type instanceof UndefinedType)
            return new UndefinedType();
        if (!(type instanceof BoolType)) {
            UnsupportOperand ee = new UnsupportOperand(notExpr.toString(),notExpr.line, notExpr.col);
            notExpr.relatedErrors.add(ee);
            return new UndefinedType();
        }
        return new BoolType();
    }

    public Boolean foundClass(Tree node, String className) {
        SymbolTable root = SymbolTable.root;
        try {
            root.get("class_" + className);
        } catch (ItemNotFoundException excep) {
            ClassNotDef exc = new ClassNotDef(className, node.line, node.col);
            node.relatedErrors.add(exc);
            return false;
        }
        return true;
    }

    public String getParentByName(String name) throws ParentNotFoundException {
        try {
            return parents.getParent(name);
        } catch (ParentNotFoundException exc) {
            throw exc;
        }
    }

    public Boolean hasSamePrimitiveType(Type a, Type b) {
        if (a instanceof IntType && b instanceof IntType)
            return true;
        if (a instanceof BoolType && b instanceof BoolType)
            return true;
        if (a instanceof StringType && b instanceof StringType)
            return true;

        return false;
    }

    public Boolean isSameArrayType(Type a, Type b) {
        if (!(a instanceof ArrayType && b instanceof ArrayType))
            return false;
        Type aSingle = ((ArrayType)a).getSingleType();
        Type bSingle = ((ArrayType)b).getSingleType();
        if (hasSamePrimitiveType(aSingle, bSingle))
            return true;
        if (aSingle instanceof UserDefinedType && bSingle instanceof UserDefinedType &&
                ((UserDefinedType)aSingle).getClassDeclaration().getName().getName().equals(((UserDefinedType)bSingle).getClassDeclaration().getName().getName()))
            return true;
        return false;
    }

    public Boolean isSubType(Type subType, Type superType) {
        if (subType instanceof UndefinedType || superType instanceof UndefinedType)
            return true;
        if (hasSamePrimitiveType(subType,superType))
            return true;
        if (isSameArrayType(subType, superType))
            return true;
        if (!(subType instanceof UserDefinedType && superType instanceof UserDefinedType))
            return false;
        UserDefinedType subClass = (UserDefinedType)subType;
        UserDefinedType superClass = (UserDefinedType)superType;
        String curName = subClass.getClassDeclaration().getName().getName();
        String finalName = superClass.getClassDeclaration().getName().getName();
        while (!curName.equals(finalName)) {
            try {
                curName = getParentByName(curName);
            } catch (ParentNotFoundException exc) {
                return false;
            }
        }
        return true;
    }


    public SymbolTable getClassSymbolByName(String name) {
        SymbolTable root = SymbolTable.root;
        ClassSymbolTableItem classSymbolItem = new ClassSymbolTableItem(name);
        try {
            classSymbolItem = (ClassSymbolTableItem) root.get("class_" + name);
        } catch (ItemNotFoundException exc) {
            // nothing
        }
        return classSymbolItem.getSymbolTable();
    }

    public MethodSymbolTableItem findMethod(String className, String methodName) throws ItemNotFoundException {
        SymbolTable classSymbol = getClassSymbolByName(className);
        return (MethodSymbolTableItem)classSymbol.get("method_" + methodName);
    }

    public FieldSymbolTableItem findField(String className, String fieldName) throws ItemNotFoundException {
        SymbolTable classSymbol = getClassSymbolByName(className);
        return (FieldSymbolTableItem)classSymbol.get("var_" + fieldName);
    }


    public Boolean objectIsValid(Expression memberCall, Type exprType, String memberName) {
        if (!(exprType instanceof UserDefinedType || (exprType instanceof ArrayType && memberName.equals("length")))) {
            UnsupportOperand exc = new UnsupportOperand(memberCall.toString(), memberCall.line, memberCall.col);
            memberCall.relatedErrors.add(exc);
            return false;
        }
        return true;
    }

    public Boolean fieldMethodCallCheck(Expression memberCall, Type exprType, String memberName) {
        if (!objectIsValid(memberCall, exprType, memberName))
            return false;
        else {
            if (exprType instanceof ArrayType)
                return true;
            UserDefinedType exprUserType = (UserDefinedType)exprType;
            return foundClass(memberCall, exprUserType.getClassDeclaration().getName().getName());
        }
    }

    @Override
    public Type visit(MethodCall methodCall) {
        Type exprType = methodCall.getInstance().accept(this);
        if (exprType instanceof UndefinedType)
            return new UndefinedType();
        if (!fieldMethodCallCheck(methodCall, exprType, methodCall.getMethodName().getName()))
            return new UndefinedType();
//        methodCall.getMethodName().accept(this);

        UserDefinedType exprUserType = (UserDefinedType)exprType;
        String className = exprUserType.getClassDeclaration().getName().getName();
        String methodName = methodCall.getMethodName().getName();
        Type methodType;
        MethodSymbolTableItem methodItem;
        try {
            methodItem = findMethod(className, methodName);
            methodType = methodItem.getReturnType();
        } catch (ItemNotFoundException exception) {
            MethodClassNotDeclared exc = new MethodClassNotDeclared(className, methodName, methodCall.line, methodCall.col);
            methodCall.relatedErrors.add(exc);
            return new UndefinedType();
        }
        // parameter type check
        List<Type> argTypes = methodItem.getArgumentsTypes();
        if (argTypes.size() != methodCall.getArgs().size()) {
            MethodClassNotDeclared exc = new MethodClassNotDeclared(className, methodName, methodCall.line, methodCall.col);
            methodCall.relatedErrors.add(exc);
            return new UndefinedType();
        } else {
            for(int i=0; i < argTypes.size();i+=1){
                Type subType = methodCall.getArgs().get(i).accept(this);
                if(!isSubType(subType,argTypes.get(i))){
                    MethodClassNotDeclared exc = new MethodClassNotDeclared(className, methodName, methodCall.line, methodCall.col);
                    methodCall.relatedErrors.add(exc);
                    return new UndefinedType();
                }
            }
        }
        // check access
        if (methodItem.getAccessModifier() == AccessModifier.ACCESS_MODIFIER_PRIVATE && !(methodCall.getInstance() instanceof Self)) {
            PrivateCall exc = new PrivateCall("Method", methodName, className, methodCall.line, methodCall.col);
            methodCall.relatedErrors.add(exc);
        }
        return methodType;
    }

    @Override
    public Type visit(FieldCall fieldCall) {
        Type exprType = fieldCall.getInstance().accept(this);
        if (exprType instanceof UndefinedType)
            return new UndefinedType();
        if (!fieldMethodCallCheck(fieldCall, exprType, fieldCall.getField().getName()))
            return new UndefinedType();
   //     fieldCall.getField().accept(this);

        if (exprType instanceof ArrayType)
            return new IntType();
        UserDefinedType exprUserType = (UserDefinedType)exprType;
        String className = exprUserType.getClassDeclaration().getName().getName();
        String fieldName = fieldCall.getField().getName();
        Type fieldType;
        try {
            FieldSymbolTableItem fieldItem = findField(className, fieldName);
            fieldType = fieldItem.getVarType();
            if (fieldItem.getAccessModifier() == AccessModifier.ACCESS_MODIFIER_PRIVATE && !(fieldCall.getInstance() instanceof Self)) {
                PrivateCall exc = new PrivateCall("Field", fieldName, className, fieldCall.line, fieldCall.col);
                fieldCall.relatedErrors.add(exc);
            }
        } catch (ItemNotFoundException exception) {
            FieldClassNotDeclared exc = new FieldClassNotDeclared(className, fieldName, fieldCall.line, fieldCall.col);
            fieldCall.relatedErrors.add(exc);
            return new UndefinedType();
        }

        return fieldType;
    }

    @Override
    public Type visit(Identifier identifier) {
        if (!inFunc)
            return new AnonymousType();
        try{
            SymbolTableItem sti =  SymbolTable.top().get("var_" + identifier.getName());
            if (sti instanceof FieldSymbolTableItem){
                return ((FieldSymbolTableItem) sti).getVarType();
            }
            else if (sti instanceof LocalVariableSymbolTableItem){
                LocalVariableSymbolTableItem localVar = (LocalVariableSymbolTableItem) sti;
                return localVar.getVarType();
            }
        } catch(ItemNotFoundException e){
            VariableNotDeclared ee = new VariableNotDeclared(identifier.getName(),identifier.line,identifier.col);
            identifier.relatedErrors.add(ee);
            return new UndefinedType();
        }
        return new NoType();
    }

    @Override
    public Type visit(Self self) {
        try {
            FieldSymbolTableItem fieldItem = (FieldSymbolTableItem)SymbolTable.top().get("var_#SELF");
            return fieldItem.getVarType();
        } catch (ItemNotFoundException exc) {
            // does not occur
        }
        return new NoType();
    }

    @Override
    public Type visit(Break breakStat) {
        if (inFunc && loopDep==0) {
            BreakContinue exc = new BreakContinue("Break", breakStat.line, breakStat.col);
            breakStat.relatedErrors.add(exc);
        }
        return null;
    }

    @Override
    public Type visit(Continue continueStat) {
        if (inFunc && loopDep==0) {
            BreakContinue exc = new BreakContinue("Continue", continueStat.line, continueStat.col);
            continueStat.relatedErrors.add(exc);
        }
        return new NoType();
    }

    @Override
    public Type visit(Skip skip) {
        return new NoType();
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
        return new ArrayType(newArray.getType());
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
        String className = newClassInstance.getClassName().getName();
        if (!classNameMap.containsKey(className)) {
            ClassNotDef exc = new ClassNotDef(className, newClassInstance.line, newClassInstance.col);
            newClassInstance.relatedErrors.add(exc);
        }
        return new UserDefinedType(classNameMap.get(className));
    }

    @Override
    public Type visit(ArrayCall arrayCall) {
        Type exprType = arrayCall.getInstance().accept(this);
        Type indType = arrayCall.getIndex().accept(this);
        if (exprType instanceof UndefinedType || indType instanceof UndefinedType)
            return new UndefinedType();
        if (!(exprType instanceof ArrayType && indType instanceof IntType)) {
            UnsupportOperand exc = new UnsupportOperand(arrayCall.toString(), arrayCall.line, arrayCall.col);
            arrayCall.relatedErrors.add(exc);
        }
        return ((ArrayType)exprType).getSingleType();
    }

    @Override
    public Type visit(NotEquals notEquals) {
        Type lValue = notEquals.getLhs().accept(this);
        Type rValue = notEquals.getRhs().accept(this);
        if (!(hasSamePrimitiveType(lValue,rValue) || isSameObj(lValue, rValue))) {
            UnsupportOperand ee = new UnsupportOperand(notEquals.toString(),notEquals.line,notEquals.col);
            notEquals.relatedErrors.add(ee);
        }
        return new BoolType();
    }

    @Override
    public Type visit(LocalVarDef localVarDef) {
//        localVarDef.getLocalVarName().accept(this);
        Type type = localVarDef.getInitialValue().accept(this);
        SymbolTable.increaseVarDef();
        try{
            LocalVariableSymbolTableItem localVarSybmbolTable =  (LocalVariableSymbolTableItem)SymbolTable.top().get(VarSymbolTableItem.var_modifier + localVarDef.getLocalVarName().getName());
            localVarSybmbolTable.setVarType(type);
        }catch (Exception e){
            // dont occur
        }
        return new NoType();
    }

    public Boolean isLValue(Expression exp)
    {
        if ((exp instanceof FieldCall && !(((FieldCall)exp).getField().getName().equals("length")) || (exp instanceof Identifier && inFunc) || (exp instanceof ArrayCall)))
            return true;
        else
            return false;
    }

    @Override
    public Type visit(IncStatement incStatement) {
        Type type = incStatement.getOperand().accept(this);
        Expression exp = incStatement.getOperand();
        if (!isLValue(exp)) {
            RvalueDecInc ee = new RvalueDecInc("Inc", incStatement.line, incStatement.col);
            incStatement.relatedErrors.add(ee);
        }
        if (type instanceof UndefinedType)
            return null;
        if(!(type instanceof IntType )){
            UnsupportOperand ee = new UnsupportOperand(incStatement.toString(),incStatement.line,incStatement.col);
            incStatement.relatedErrors.add(ee);
        }
        return null;
    }

    @Override
    public Type visit(DecStatement decStatement) {
        Type type = decStatement.getOperand().accept(this);
        Expression exp = decStatement.getOperand();
        if (!isLValue(exp)) {
            RvalueDecInc ee = new RvalueDecInc("Dec",decStatement.line,decStatement.col);
            decStatement.relatedErrors.add(ee);
        }
        if (type instanceof UndefinedType)
            return null;
        if(!(type instanceof IntType )){
            UnsupportOperand ee = new UnsupportOperand(decStatement.toString(),decStatement.line,decStatement.col);
            decStatement.relatedErrors.add(ee);
        }
        return new NoType();
    }

    public String getLValueName(Expression exp) {
        if (exp instanceof FieldCall)
            return ((FieldCall)exp).getField().getName();
        if (exp instanceof Identifier)
            return ((Identifier)exp).getName();
        return ""; // Does not occur
    }

    @Override
    public Type visit(Assign assign) {
        Type lValueType = assign.getLvalue().accept(this);
        Type rValueType = assign.getRvalue().accept(this);
        Expression exp = assign.getLvalue();
        // lValue check
        if (!isLValue(exp)) {
            Rvalue ee = new Rvalue(assign.line,assign.col);
            assign.relatedErrors.add(ee);
        }
        if (lValueType instanceof UndefinedType || rValueType instanceof UndefinedType)
            return new NoType();
        // type check
        if (!isSubType(rValueType, lValueType)) {
            AssignNotTypeCheck exc = new AssignNotTypeCheck(assign.line,assign.col);
            assign.relatedErrors.add(exc);
        }
        if (!(lValueType instanceof AnonymousType))
            return new NoType();
        // set type
        String lValueName = getLValueName(exp);
        try {
            VarSymbolTableItem varItem =  (VarSymbolTableItem)SymbolTable.top().get("var_" + lValueName);
            varItem.setVarType(rValueType);
        } catch (ItemNotFoundException exc) {
            // Does not occur
        }
        return new NoType();
    }

    @Override
    public Type visit(ClassDeclaration classDeclaration) {
        SymbolTable.pushFromQueue();
        visitClassBody(classDeclaration);
        for (ClassMemberDeclaration md : classDeclaration.getClassMembers())
            md.accept(this);
        SymbolTable.pop();
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
    }

    @Override
    public Type visit(EntryClassDeclaration entryClassDeclaration) {
        SymbolTable.pushFromQueue();
        visitClassBody(entryClassDeclaration);
        for (ClassMemberDeclaration md : entryClassDeclaration.getClassMembers())
            md.accept(this);
        try {
            MethodSymbolTableItem mst = (MethodSymbolTableItem) SymbolTable.top().get("method_main");
            Type methodType = mst.getReturnType();
            if (!(mst.getAccessModifier() == AccessModifier.ACCESS_MODIFIER_PUBLIC && methodType instanceof IntType && mst.getArgumentsTypes().size()==0)){
                MainClassInEntry ee = new MainClassInEntry();
                entryClassDeclaration.relatedErrors.add(ee);
            }

        }catch (ItemNotFoundException exc){
            MainClassInEntry ee = new MainClassInEntry();
            entryClassDeclaration.relatedErrors.add(ee);
        }
        SymbolTable.pop();
        return null;
    }

    @Override
    public Type visit(FieldDeclaration fieldDeclaration) {
        fieldDeclaration.getIdentifier().accept(this);
        return null;
    }

    @Override
    public Type visit(ParameterDeclaration parameterDeclaration) {
        if (parameterDeclaration.getType() instanceof UserDefinedType) {
            ClassDeclaration obj = ((UserDefinedType)parameterDeclaration.getType()).getClassDeclaration();
            String className = obj.getName().getName();
            if (!classNameMap.containsKey(className)) {
                ClassNotDef exc = new ClassNotDef(className, parameterDeclaration.line, parameterDeclaration.col);
                parameterDeclaration.relatedErrors.add(exc);
            }
        }
        SymbolTable.increaseVarDef();
        Type type = parameterDeclaration.getType();
        try{
            LocalVariableSymbolTableItem localVarSybmbolTable =  (LocalVariableSymbolTableItem)SymbolTable.top().get(VarSymbolTableItem.var_modifier + parameterDeclaration.getIdentifier().getName());
            localVarSybmbolTable.setVarType(type);
        } catch (Exception e){
            // dont occur
        }
        return null;
    }

    @Override
    public Type visit(MethodDeclaration methodDeclaration) {
        SymbolTable.resetVarDef();
        methodDeclaration.getName().accept(this);
        SymbolTable.pushFromQueue();
        for (ParameterDeclaration pd : methodDeclaration.getArgs()) {
            pd.accept(this);
        }
        inFunc = true;
        loopDep = 0;
        for (Statement stmt : methodDeclaration.getBody())
            stmt.accept(this);
        SymbolTable.pop();
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
        SymbolTable.pushFromQueue();
        for (ClassDeclaration cd : program.getClasses())
            cd.accept(this);
        SymbolTable.pop();
        return new NoType();
    }

    public void analyze() {
        for (ClassDeclaration itrClass : program.getClasses())
            if ( !classNameMap.containsKey(itrClass.getName().getName()) )
                classNameMap.put(itrClass.getName().getName(), itrClass);
        this.visit( program );
        ReportingPass errorPrinter = new ReportingPass();
        errorPrinter.analyze(program);
    }
}
