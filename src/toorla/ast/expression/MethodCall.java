package toorla.ast.expression;

import toorla.visitor.Visitor;

import java.util.ArrayList;

public class MethodCall extends Expression {
    private Expression instance;
    private Identifier methodName;

    public MethodCall(Expression instance, Identifier methodName) {
        this.instance = instance;
        this.methodName = methodName;
    }

    private ArrayList<Expression> args = new ArrayList<>();

    public Expression getInstance() {
        return instance;
    }


    public Identifier getMethodName() {
        return methodName;
    }


    public ArrayList<Expression> getArgs() {
        return args;
    }

    public void addArg(Expression arg) {
        this.args.add(arg);
    }

    @Override
    public String toString() {
        return "MethodCall";
    }

    public <R> R accept(Visitor<R> visitor) {
        return visitor.visit(this);
    }
}
