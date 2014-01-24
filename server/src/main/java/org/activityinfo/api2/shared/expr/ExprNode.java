package org.activityinfo.api2.shared.expr;

/**
 * Root of the expression hierarchy. Expressions are used for validation and
 * calculation by AI
 */
public abstract class ExprNode {

    /**
     * Evaluates the expression to a real value.
     */
    public abstract double evalReal();

}
