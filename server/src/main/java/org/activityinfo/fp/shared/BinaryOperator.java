package org.activityinfo.fp.shared;

/**
 * Represents an operation upon two operands of the same type, producing a result of the same type as the operands.
 * This is a specialization of BiFunction for the case where the operands and the result are all of the same type.
 */
public abstract class BinaryOperator<T> extends BiFunction<T, T, T> {

    public static BinaryOperator<Void> VOID = new BinaryOperator<Void>() {
        @Override
        public Void apply(Void x, Void y) {
            return null;
        }
    };

}
