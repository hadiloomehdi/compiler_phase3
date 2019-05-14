package toorla.ast.expression.unaryExpression;

import toorla.ast.expression.Expression;
import toorla.visitor.Visitor;

public class Neg extends UnaryExpression {

	public Neg(Expression expr) {
		super(expr);
	}

	public Neg() {
		super( null );
	}

	public <R> R accept(Visitor<R> visitor) {
		return visitor.visit(this);
	}

	@Override
	public String toString() {
		return "Neg";
	}
}
