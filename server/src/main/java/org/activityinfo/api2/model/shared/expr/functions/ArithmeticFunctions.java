package org.activityinfo.api2.model.shared.expr.functions;

import org.activityinfo.api2.model.shared.expr.ExprFunction;

public class ArithmeticFunctions {

    public static final ExprFunction BINARY_PLUS = new BinaryInfixFunction("+") {

        @Override
        public double applyReal(double x, double y) {
            return x + y;
        }
    };

    public static final ExprFunction DIVIDE = new BinaryInfixFunction("/") {

        @Override
        public double applyReal(double x, double y) {
            return x / y;
        }
    };

    public static ExprFunction getBinaryInfix(String name) {
        if (name.equals("+")) {
            return BINARY_PLUS;

        } else if (name.equals("/")) {
            return DIVIDE;

        } else {
            throw new IllegalArgumentException();
        }
    }
}
