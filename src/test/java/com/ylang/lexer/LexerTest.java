package com.ylang.lexer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LexerTest {

    private Lexer lexer;

    @BeforeEach
    void setUp() {
        lexer = new Lexer();
    }

    @Test
    void testSimpleFunctionDefinition() {
        String source = "Define a function called greet that takes name as text and returns text.";

        List<Token> tokens = lexer.tokenize(source);

        assertNotNull(tokens);
        assertTrue(tokens.size() > 0);

        // Check for key tokens
        assertTrue(tokens.stream().anyMatch(t -> t.getType() == TokenType.DEFINE));
        assertTrue(tokens.stream().anyMatch(t -> t.getType() == TokenType.FUNCTION));
        assertTrue(tokens.stream().anyMatch(t -> t.getType() == TokenType.CALLED));
        assertTrue(tokens.stream().anyMatch(t -> t.getValue().equals("greet")));
    }

    @Test
    void testYummyAnnotation() {
        String source = "Define a function called process (Yummy: generic with type T).";

        List<Token> tokens = lexer.tokenize(source);

        assertTrue(tokens.stream().anyMatch(t -> t.getType() == TokenType.YUMMY_ANNOTATION));

        Token yummyToken = tokens.stream()
                .filter(t -> t.getType() == TokenType.YUMMY_ANNOTATION)
                .findFirst()
                .orElse(null);

        assertNotNull(yummyToken);
        assertTrue(yummyToken.getValue().contains("generic with type T"));
    }

    @Test
    void testNumberLiteral() {
        String source = "Create a variable called age with value 42.";

        List<Token> tokens = lexer.tokenize(source);

        assertTrue(tokens.stream().anyMatch(t -> t.getType() == TokenType.NUMBER_LITERAL));

        Token numberToken = tokens.stream()
                .filter(t -> t.getType() == TokenType.NUMBER_LITERAL)
                .findFirst()
                .orElse(null);

        assertNotNull(numberToken);
        assertEquals("42", numberToken.getValue());
    }

    @Test
    void testStringLiteral() {
        String source = "Create a variable called message with value \"Hello, World!\".";

        List<Token> tokens = lexer.tokenize(source);

        assertTrue(tokens.stream().anyMatch(t -> t.getType() == TokenType.STRING_LITERAL));

        Token stringToken = tokens.stream()
                .filter(t -> t.getType() == TokenType.STRING_LITERAL)
                .findFirst()
                .orElse(null);

        assertNotNull(stringToken);
        assertEquals("Hello, World!", stringToken.getValue());
    }

    @Test
    void testIndentation() {
        String source = """
                Define a function called test.
                  Print "Hello".
                  Print "World".
                """;

        List<Token> tokens = lexer.tokenize(source);

        // Should have INDENT and DEDENT tokens
        assertTrue(tokens.stream().anyMatch(t -> t.getType() == TokenType.INDENT));
        assertTrue(tokens.stream().anyMatch(t -> t.getType() == TokenType.DEDENT));
    }

    @Test
    void testMultipleIndentationLevels() {
        String source = """
                Define a function called nested.
                  If x is true:
                    Print "Level 2".
                    If y is true:
                      Print "Level 3".
                """;

        List<Token> tokens = lexer.tokenize(source);

        // Count INDENT tokens
        long indentCount = tokens.stream().filter(t -> t.getType() == TokenType.INDENT).count();
        assertTrue(indentCount >= 2, "Should have at least 2 INDENT tokens");
    }

    @Test
    void testFloatingPointNumber() {
        String source = "Create variable called pi with value 3.14159.";

        List<Token> tokens = lexer.tokenize(source);

        Token numberToken = tokens.stream()
                .filter(t -> t.getType() == TokenType.NUMBER_LITERAL)
                .findFirst()
                .orElse(null);

        assertNotNull(numberToken);
        assertTrue(numberToken.getValue().contains("."));
    }

    @Test
    void testBooleanLiterals() {
        String source = "Create variable called flag1 with value true and flag2 with value false.";

        List<Token> tokens = lexer.tokenize(source);

        long boolCount = tokens.stream()
                .filter(t -> t.getType() == TokenType.BOOLEAN_LITERAL)
                .count();

        assertTrue(boolCount >= 2, "Should have at least 2 boolean literals");
    }

    @Test
    void testEmptyString() {
        String source = "Create variable called empty with value \"\".";

        List<Token> tokens = lexer.tokenize(source);

        Token stringToken = tokens.stream()
                .filter(t -> t.getType() == TokenType.STRING_LITERAL)
                .findFirst()
                .orElse(null);

        assertNotNull(stringToken);
        assertEquals("", stringToken.getValue());
    }

    @Test
    void testOperators() {
        String source = "x plus y minus z multiplied by w divided by v.";

        List<Token> tokens = lexer.tokenize(source);

        assertTrue(tokens.stream().anyMatch(t -> t.getType() == TokenType.PLUS));
        assertTrue(tokens.stream().anyMatch(t -> t.getType() == TokenType.MINUS));
        assertTrue(tokens.stream().anyMatch(t -> t.getType() == TokenType.MULTIPLIED_BY));
        assertTrue(tokens.stream().anyMatch(t -> t.getType() == TokenType.DIVIDED_BY));
    }

    @Test
    void testComparisonOperators() {
        String source = "If x equals y or x greater than z or w less than v.";

        List<Token> tokens = lexer.tokenize(source);

        assertTrue(tokens.stream().anyMatch(t -> t.getType() == TokenType.EQUALS || t.getType() == TokenType.IS));
        assertTrue(tokens.stream().anyMatch(t -> t.getType() == TokenType.GREATER_THAN));
        assertTrue(tokens.stream().anyMatch(t -> t.getType() == TokenType.LESS_THAN));
    }

    @Test
    void testLogicalOperators() {
        String source = "If x and y or not z.";

        List<Token> tokens = lexer.tokenize(source);

        assertTrue(tokens.stream().anyMatch(t -> t.getType() == TokenType.AND));
        assertTrue(tokens.stream().anyMatch(t -> t.getType() == TokenType.OR));
    }

    @Test
    void testSpecialCharacters() {
        String source = "Define function with (parentheses) and {braces} and [brackets].";

        List<Token> tokens = lexer.tokenize(source);

        assertTrue(tokens.stream().anyMatch(t -> t.getType() == TokenType.LPAREN));
        assertTrue(tokens.stream().anyMatch(t -> t.getType() == TokenType.RPAREN));
    }

    @Test
    void testColonAndComma() {
        String source = "Define function called test: with args x, y, z.";

        List<Token> tokens = lexer.tokenize(source);

        assertTrue(tokens.stream().anyMatch(t -> t.getType() == TokenType.COLON));
        assertTrue(tokens.stream().anyMatch(t -> t.getType() == TokenType.COMMA));
    }

    @Test
    void testKeywords() {
        String source = "Define function that takes x as Number and returns Number.";

        List<Token> tokens = lexer.tokenize(source);

        assertTrue(tokens.stream().anyMatch(t -> t.getType() == TokenType.DEFINE));
        assertTrue(tokens.stream().anyMatch(t -> t.getType() == TokenType.FUNCTION));
        assertTrue(tokens.stream().anyMatch(t -> t.getType() == TokenType.THAT));
        assertTrue(tokens.stream().anyMatch(t -> t.getType() == TokenType.TAKES));
        assertTrue(tokens.stream().anyMatch(t -> t.getType() == TokenType.AS));
        assertTrue(tokens.stream().anyMatch(t -> t.getType() == TokenType.AND));
        assertTrue(tokens.stream().anyMatch(t -> t.getType() == TokenType.RETURNS));
    }

    @Test
    void testControlFlowKeywords() {
        String source = "If condition: do something. Otherwise: do other. While loop. For each item. Try: risky. Catch error.";

        List<Token> tokens = lexer.tokenize(source);

        assertTrue(tokens.stream().anyMatch(t -> t.getType() == TokenType.IF));
        assertTrue(tokens.stream().anyMatch(t -> t.getType() == TokenType.OTHERWISE));
        assertTrue(tokens.stream().anyMatch(t -> t.getType() == TokenType.WHILE));
        assertTrue(tokens.stream().anyMatch(t -> t.getType() == TokenType.FOR));
        assertTrue(tokens.stream().anyMatch(t -> t.getType() == TokenType.TRY));
        assertTrue(tokens.stream().anyMatch(t -> t.getType() == TokenType.CATCH));
    }

    @Test
    void testStructureKeyword() {
        String source = "Define a structure called User with fields: name, age.";

        List<Token> tokens = lexer.tokenize(source);

        assertTrue(tokens.stream().anyMatch(t -> t.getType() == TokenType.STRUCTURE));
        assertTrue(tokens.stream().anyMatch(t -> t.getType() == TokenType.WITH));
        assertTrue(tokens.stream().anyMatch(t -> t.getType() == TokenType.FIELDS));
    }

    @Test
    void testTypeModifiers() {
        String source = "Create mutable variable as optional borrowed String.";

        List<Token> tokens = lexer.tokenize(source);

        assertTrue(tokens.stream().anyMatch(t -> t.getType() == TokenType.MUTABLE));
        assertTrue(tokens.stream().anyMatch(t -> t.getType() == TokenType.OPTIONAL));
        assertTrue(tokens.stream().anyMatch(t -> t.getType() == TokenType.BORROWED));
    }

    @Test
    void testAsyncKeyword() {
        String source = "Define async function called fetchData.";

        List<Token> tokens = lexer.tokenize(source);

        assertTrue(tokens.stream().anyMatch(t -> t.getType() == TokenType.ASYNC));
    }

    @Test
    void testNegativeNumber() {
        String source = "Create variable with value -42.";

        List<Token> tokens = lexer.tokenize(source);

        assertTrue(tokens.stream().anyMatch(t -> t.getType() == TokenType.NUMBER_LITERAL));
    }

    @Test
    void testStringWithSpecialCharacters() {
        String source = "Create message with value \"Hello\\nWorld\\t!\".";

        List<Token> tokens = lexer.tokenize(source);

        Token stringToken = tokens.stream()
                .filter(t -> t.getType() == TokenType.STRING_LITERAL)
                .findFirst()
                .orElse(null);

        assertNotNull(stringToken);
        assertTrue(stringToken.getValue().contains("\\n") || stringToken.getValue().contains("\n"));
    }

    @Test
    void testMultipleStatementsOnSeparateLines() {
        String source = """
                Create x with value 10.
                Create y with value 20.
                Create z with value 30.
                """;

        List<Token> tokens = lexer.tokenize(source);

        long createCount = tokens.stream()
                .filter(t -> t.getType() == TokenType.CREATE)
                .count();

        assertEquals(3, createCount, "Should have 3 CREATE tokens");
    }

    @Test
    void testEOFToken() {
        String source = "Define function called test.";

        List<Token> tokens = lexer.tokenize(source);

        Token lastToken = tokens.get(tokens.size() - 1);
        assertEquals(TokenType.EOF, lastToken.getType(), "Last token should be EOF");
    }

    @Test
    void testEmptySource() {
        String source = "";

        List<Token> tokens = lexer.tokenize(source);

        assertNotNull(tokens);
        // Should at least have EOF token
        assertTrue(tokens.size() >= 1);
        assertTrue(tokens.stream().anyMatch(t -> t.getType() == TokenType.EOF));
    }

    @Test
    void testWhitespaceHandling() {
        String source = "Define    function    called    test.";

        List<Token> tokens = lexer.tokenize(source);

        // Multiple spaces should not create extra tokens
        assertTrue(tokens.stream().anyMatch(t -> t.getType() == TokenType.DEFINE));
        assertTrue(tokens.stream().anyMatch(t -> t.getType() == TokenType.FUNCTION));
        assertTrue(tokens.stream().anyMatch(t -> t.getType() == TokenType.CALLED));
    }

    @Test
    void testNewlineTokens() {
        String source = "Line 1.\nLine 2.\nLine 3.";

        List<Token> tokens = lexer.tokenize(source);

        long newlineCount = tokens.stream()
                .filter(t -> t.getType() == TokenType.NEWLINE)
                .count();

        assertTrue(newlineCount >= 2, "Should have at least 2 newline tokens");
    }

    @Test
    void testComplexExpression() {
        String source = "If (x plus 5) multiplied by (y minus 2) greater than 100:";

        List<Token> tokens = lexer.tokenize(source);

        assertNotNull(tokens);
        assertTrue(tokens.size() > 10, "Complex expression should produce many tokens");
    }

    @Test
    void testMethodKeyword() {
        String source = "Define a method called getName.";

        List<Token> tokens = lexer.tokenize(source);

        assertTrue(tokens.stream().anyMatch(t -> t.getType() == TokenType.METHOD));
    }

    @Test
    void testImplementKeyword() {
        String source = "Implement methods for User:";

        List<Token> tokens = lexer.tokenize(source);

        assertTrue(tokens.stream().anyMatch(t -> t.getType() == TokenType.IMPLEMENT));
        assertTrue(tokens.stream().anyMatch(t -> t.getType() == TokenType.FOR));
    }

    @Test
    void testReturnKeyword() {
        String source = "Return result.";

        List<Token> tokens = lexer.tokenize(source);

        assertTrue(tokens.stream().anyMatch(t -> t.getType() == TokenType.RETURN));
    }
}
