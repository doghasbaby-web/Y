package com.ylang.lexer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a token in Y Language
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Token {
    private TokenType type;
    private String value;
    private int line;
    private int column;

    public Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
        this.line = 0;
        this.column = 0;
    }

    @Override
    public String toString() {
        return String.format("Token(%s, '%s', %d:%d)", type, value, line, column);
    }
}
