package com.ylang.parser;

import com.ylang.lexer.Token;
import com.ylang.lexer.TokenType;

/**
 * Exception thrown during parsing when a syntax error is encountered.
 * Provides detailed error information including token position and expected type.
 */
public class ParseException extends RuntimeException {

    private final Token token;
    private final TokenType expected;
    private final int line;
    private final int column;

    /**
     * Creates a ParseException with expected and actual token information
     */
    public ParseException(String message, Token token, TokenType expected) {
        super(formatMessage(message, token, expected));
        this.token = token;
        this.expected = expected;
        this.line = token != null ? token.getLine() : -1;
        this.column = token != null ? token.getColumn() : -1;
    }

    /**
     * Creates a ParseException with just a message and token
     */
    public ParseException(String message, Token token) {
        super(formatMessage(message, token, null));
        this.token = token;
        this.expected = null;
        this.line = token != null ? token.getLine() : -1;
        this.column = token != null ? token.getColumn() : -1;
    }

    /**
     * Creates a ParseException with just a message
     */
    public ParseException(String message) {
        super(message);
        this.token = null;
        this.expected = null;
        this.line = -1;
        this.column = -1;
    }

    private static String formatMessage(String message, Token token, TokenType expected) {
        StringBuilder sb = new StringBuilder();
        sb.append("Parse error");

        if (token != null) {
            sb.append(" at line ").append(token.getLine());
            sb.append(", column ").append(token.getColumn());
        }

        sb.append(": ").append(message);

        if (expected != null && token != null) {
            sb.append(" (expected ").append(expected);
            sb.append(", but got ").append(token.getType()).append(")");
        }

        if (token != null && token.getValue() != null) {
            sb.append("\n  Token: '").append(token.getValue()).append("'");
        }

        return sb.toString();
    }

    public Token getToken() {
        return token;
    }

    public TokenType getExpected() {
        return expected;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }
}
