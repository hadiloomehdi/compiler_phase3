package toorla.ast.statement;

import toorla.ast.expression.Expression;
import toorla.visitor.Visitor;

public class While extends Statement {
	public Expression expr;
	public Statement body;

	public While(Expression expr, Statement body) {
		this.expr = expr;
		this.body = body;
	}

	public <R> R accept(Visitor<R> visitor) {
		return visitor.visit(this);
	}

	@Override
	public String toString() {
		return "While";
	}
}