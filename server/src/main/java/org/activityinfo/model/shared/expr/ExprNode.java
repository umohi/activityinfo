package org.activityinfo.model.shared.expr;

/**
 * Root of the expression hierarchy. Expressions are used for validation and
 * calculation by ActivityInfoNamespace
 */
public abstract class ExprNode {

	/**
	 * Evaluates the expression to a real value.
	 */
	public abstract double evalReal();

}
