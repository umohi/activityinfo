package org.activityinfo.model.shared.expr;

import java.util.Iterator;
import java.util.Set;

import org.activityinfo.model.shared.expr.functions.ArithmeticFunctions;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;
import com.google.common.collect.Sets;

public class ExprParser {

	private static final Set<String> INFIX_OPERATORS = Sets.newHashSet("+", "-", "*", "/");
	
	private PeekingIterator<Token> lexer;

	public ExprParser(Iterator<Token> tokens) {
		this.lexer = Iterators.peekingIterator(Iterators.filter(tokens, new Predicate<Token>() {

			@Override
			public boolean apply(Token token) {
				return token.getType() != TokenType.WHITESPACE;
			}
		}));
	}
	
	public ExprNode parse() {
		ExprNode expr = parseSimple();
		if(!lexer.hasNext()) {
			return expr;
		}
		Token token = lexer.peek();
		if(isInfixOperator(token)) {
			lexer.next();
			ExprFunction function = ArithmeticFunctions.getBinaryInfix(token.getString());
			ExprNode right = parse();
			
			return new FunctionCallNode(function, expr, right);
			
		} else {
			return expr;
		}
	}
//	
//	throw new ExprSyntaxException(String.format("Expected +, -, /, or *, but found '%s' at %d",
//			token.getString(), token.getTokenStart()));
	

	private boolean isInfixOperator(Token token) {
		return token.getType() == TokenType.SYMBOL &&
				INFIX_OPERATORS.contains(token.getString());
	}

	public ExprNode parseSimple() {
		Token token = lexer.next();
		if(token.getType() == TokenType.PAREN_START) {
			return parseGroup();
			
		} else if(token.getType() == TokenType.NUMBER) {
			return new ConstantExpr(Double.parseDouble(token.getString()));
		
		} else {
			throw new ExprSyntaxException(String.format("Unexpected token '%s' at position %d'",
					token.getString(), token.getTokenStart()));
		}
	}

	private ExprNode parseGroup() {
		ExprNode expr = parse();
		expectNext(TokenType.PAREN_END, "')'");
		return new GroupExpr(expr);
	}

	/**
	 * Retrieves the next token, and throws an exception if it does not match
	 * the expected type.
	 */
	private Token expectNext(TokenType expectedType, String description) {
		Token token = lexer.next();
		if(token.getType() != expectedType) {
			throw new ExprSyntaxException(String.format("Syntax error at %d: expected %s but found '%s'", 
					token.getTokenStart(), 
					description,
					token.getString()));
		}
		return token;
	}
	
}
