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
}
