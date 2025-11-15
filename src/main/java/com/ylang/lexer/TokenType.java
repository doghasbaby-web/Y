package com.ylang.lexer;

/**
 * Enumeration of all token types in Y Language
 */
public enum TokenType {
    // Keywords
    DEFINE,
    CREATE,
    IMPLEMENT,
    IMPORT,
    EXPORT,

    // Type keywords
    NUMBER,
    TEXT,
    TRUTH_VALUE,
    NOTHING,
    LIST,
    MAP,
    SET,
    OPTIONAL,

    // Modifiers
    MUTABLE,
    BORROWED,
    OWNED,
    ASYNC,

    // Control flow
    IF,
    OTHERWISE,
    FOR,
    WHILE,
    LOOP,
    MATCH,
    CASE,
    RETURN,
    BREAK,
    CONTINUE,

    // Function keywords
    TAKES,
    RETURNS,
    AND,
    THAT,
    WITH,
    AS,
    CALLED,

    // Operations
    PLUS,
    MINUS,
    MULTIPLIED_BY,
    DIVIDED_BY,
    EQUALS,
    IS,
    GREATER_THAN,
    LESS_THAN,
    NOT,
    OR,

    // Special
    TRY,
    CATCH,
    SPAWN,
    WAIT,
    SEND,
    RECEIVE,

    // Structure keywords
    FUNCTION,
    STRUCTURE,
    TRAIT,
    ENUMERATION,
    METHOD,
    METHODS,
    FIELD,
    FIELDS,
    VARIANTS,

    // Literals
    NUMBER_LITERAL,
    STRING_LITERAL,
    BOOLEAN_LITERAL,

    // Identifiers
    IDENTIFIER,

    // Punctuation
    LPAREN,
    RPAREN,
    LBRACE,
    RBRACE,
    COMMA,
    DOT,
    COLON,
    SEMICOLON,

    // Special markers
    YUMMY_ANNOTATION,
    NEWLINE,
    INDENT,
    DEDENT,
    EOF
}
