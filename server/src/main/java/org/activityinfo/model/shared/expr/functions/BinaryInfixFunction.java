package org.activityinfo.model.shared.expr.functions;

import java.util.List;

import org.activityinfo.model.shared.expr.ExprFunction;
import org.activityinfo.model.shared.expr.ExprNode;

public abstract class BinaryInfixFunction extends ExprFunction {

	private String symbol;

	public BinaryInfixFunction(String symbol) {
		super();
		this.symbol = symbol;
	}

	@Override
	public final String getName() {
		return symbol;
	}

	@Override
	public double applyReal(List<ExprNode> arguments) {
		return applyReal(arguments.get(0).evalReal(), arguments.get(1).evalReal());
	}
	
	public abstract double applyReal(double x, double y);
}
