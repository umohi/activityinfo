package org.activityinfo.model.shared.expr;

import static org.junit.Assert.assertEquals;

import org.activityinfo.model.shared.expr.functions.ArithmeticFunctions;
import org.junit.Test;

public class ExprParserTest {

	
	@Test
	public void simpleTokenizing() {
		expect("1+2", 
				new Token(TokenType.NUMBER, 0, "1"),
				new Token(TokenType.SYMBOL, 1, "+"),
				new Token(TokenType.NUMBER, 2, "2"));
					
		expect("1 \n+ 2", 
				new Token(TokenType.NUMBER, 0, "1"),
				new Token(TokenType.WHITESPACE, 1, " \n"),
				new Token(TokenType.SYMBOL, 3, "+"),
				new Token(TokenType.WHITESPACE, 4, " "),
				new Token(TokenType.NUMBER, 5, "2"));
					
		
		expect("((1+3)*(44323+455))/66   ", 
			new Token(TokenType.PAREN_START, 0, "("),
			new Token(TokenType.PAREN_START, 1, "("),
			new Token(TokenType.NUMBER, 2, "1"),
			new Token(TokenType.SYMBOL, 3, "+"),
			new Token(TokenType.NUMBER, 4, "3"),
			new Token(TokenType.PAREN_END, 5, ")"),
			new Token(TokenType.SYMBOL, 6, "*"),
			new Token(TokenType.PAREN_START, 7, "("),
			new Token(TokenType.NUMBER, 8, "44323"),
			new Token(TokenType.SYMBOL, 13, "+"),
			new Token(TokenType.NUMBER, 14, "455"),
			new Token(TokenType.PAREN_END, 17, ")"),
			new Token(TokenType.PAREN_END, 18, ")"),
			new Token(TokenType.SYMBOL, 19, "/"),
			new Token(TokenType.NUMBER, 20, "66"),
			new Token(TokenType.WHITESPACE, 22, "   "));
	}
	
	@Test
	public void parseSimple() {
		expect("1", new ConstantExpr(1));
		expect("(1)", new GroupExpr(new ConstantExpr(1)));
		expect("1+2", new FunctionCallNode(ArithmeticFunctions.BINARY_PLUS,
				new ConstantExpr(1),
				new ConstantExpr(2)));	
	}
	
	@Test
	public void parseNested() {
		expect("(1+2)/3", 
				new FunctionCallNode(ArithmeticFunctions.DIVIDE, 
					new GroupExpr(
						new FunctionCallNode(ArithmeticFunctions.BINARY_PLUS,
							new ConstantExpr(1),
							new ConstantExpr(2))),
					new ConstantExpr(3)));	
	}
	
	@Test
	public void evaluateExpr() {
		evaluate("1", 1);
		evaluate("1+1", 2);
		evaluate("(5+5)/2", 5);
	}
	
	private void expect(String string, Token... tokens) {
		System.out.println("Tokenizing [" + string + "]");
		ExprLexer tokenizer = new ExprLexer(string);
		int expectedIndex = 0;
		while(!tokenizer.isEndOfInput()) {
			Token expected = tokens[expectedIndex++];
			Token actual = tokenizer.next();
			System.out.println(String.format("Expected: %15s, got %s", expected.toString(), actual.toString()));
			assertEquals("tokenStart", expected.getTokenStart(), actual.getTokenStart());
			assertEquals("text", expected.getString(), actual.getString());
			assertEquals("type", expected.getType(), actual.getType());
			
			if(!expected.equals(actual)) {
				System.err.println("Unexpected result!");
				throw new AssertionError();
			}
		}
	}
	
	private void expect(String string, ExprNode expr) {
		System.out.println("Parsing [" + string + "]");
		ExprLexer lexer = new ExprLexer(string);
		ExprParser parser = new ExprParser(lexer);
		ExprNode actual = parser.parse();
		
		assertEquals(expr, actual);
	}
	
	private void evaluate(String string, double expectedValue) {
		ExprLexer lexer = new ExprLexer(string);
		ExprParser parser = new ExprParser(lexer);	
		ExprNode expr = parser.parse();
		assertEquals(string, expectedValue, expr.evalReal(), 0);
	}
}
