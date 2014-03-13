package org.activityinfo.core.shared.expr;

public enum TokenType {


    /**
     * "("
     */
    PAREN_START,

    /**
     * ")"
     */
    PAREN_END,

    /**
     * A named value, operator, or function
     */
    SYMBOL,

    /**
     * Numeric Literal
     */
    NUMBER,

    /**
     * Whitespace
     */
    WHITESPACE,

    EOF

}
