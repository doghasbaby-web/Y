package com.ylang.lexer;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Lexer for Y Language - converts source code into tokens
 */
@Component
public class Lexer {

    private static final Map<String, TokenType> KEYWORDS = new HashMap<>();

    static {
        // Declaration keywords
        KEYWORDS.put("define", TokenType.DEFINE);
        KEYWORDS.put("create", TokenType.CREATE);
        KEYWORDS.put("implement", TokenType.IMPLEMENT);
        KEYWORDS.put("import", TokenType.IMPORT);
        KEYWORDS.put("export", TokenType.EXPORT);

        // Type keywords
        KEYWORDS.put("number", TokenType.NUMBER);
        KEYWORDS.put("text", TokenType.TEXT);
        KEYWORDS.put("nothing", TokenType.NOTHING);
        KEYWORDS.put("list", TokenType.LIST);
        KEYWORDS.put("map", TokenType.MAP);
        KEYWORDS.put("set", TokenType.SET);
        KEYWORDS.put("optional", TokenType.OPTIONAL);

        // Modifiers
        KEYWORDS.put("mutable", TokenType.MUTABLE);
        KEYWORDS.put("borrowed", TokenType.BORROWED);
        KEYWORDS.put("owned", TokenType.OWNED);
        KEYWORDS.put("async", TokenType.ASYNC);

        // Control flow
        KEYWORDS.put("if", TokenType.IF);
        KEYWORDS.put("otherwise", TokenType.OTHERWISE);
        KEYWORDS.put("for", TokenType.FOR);
        KEYWORDS.put("while", TokenType.WHILE);
        KEYWORDS.put("loop", TokenType.LOOP);
        KEYWORDS.put("match", TokenType.MATCH);
        KEYWORDS.put("case", TokenType.CASE);
        KEYWORDS.put("return", TokenType.RETURN);
        KEYWORDS.put("break", TokenType.BREAK);
        KEYWORDS.put("continue", TokenType.CONTINUE);

        // Function keywords
        KEYWORDS.put("takes", TokenType.TAKES);
        KEYWORDS.put("returns", TokenType.RETURNS);
        KEYWORDS.put("and", TokenType.AND);
        KEYWORDS.put("that", TokenType.THAT);
        KEYWORDS.put("with", TokenType.WITH);
        KEYWORDS.put("as", TokenType.AS);
        KEYWORDS.put("called", TokenType.CALLED);

        // Operations
        KEYWORDS.put("plus", TokenType.PLUS);
        KEYWORDS.put("minus", TokenType.MINUS);
        KEYWORDS.put("is", TokenType.IS);
        KEYWORDS.put("equals", TokenType.EQUALS);
        KEYWORDS.put("not", TokenType.NOT);
        KEYWORDS.put("or", TokenType.OR);

        // Special
        KEYWORDS.put("try", TokenType.TRY);
        KEYWORDS.put("catch", TokenType.CATCH);
        KEYWORDS.put("spawn", TokenType.SPAWN);
        KEYWORDS.put("wait", TokenType.WAIT);
        KEYWORDS.put("send", TokenType.SEND);
        KEYWORDS.put("receive", TokenType.RECEIVE);

        // Structure keywords
        KEYWORDS.put("function", TokenType.FUNCTION);
        KEYWORDS.put("structure", TokenType.STRUCTURE);
        KEYWORDS.put("trait", TokenType.TRAIT);
        KEYWORDS.put("enumeration", TokenType.ENUMERATION);
        KEYWORDS.put("method", TokenType.METHOD);
        KEYWORDS.put("methods", TokenType.METHODS);
        KEYWORDS.put("field", TokenType.FIELD);
        KEYWORDS.put("fields", TokenType.FIELDS);
        KEYWORDS.put("variants", TokenType.VARIANTS);

        // Boolean literals
        KEYWORDS.put("true", TokenType.BOOLEAN_LITERAL);
        KEYWORDS.put("false", TokenType.BOOLEAN_LITERAL);
    }

    private static final Pattern YUMMY_PATTERN = Pattern.compile("\\(Yummy:([^)]+)\\)");

    // Validation constants
    private static final int MAX_SOURCE_LENGTH = 1_000_000; // 1MB
    private static final int MAX_NESTING_DEPTH = 100;
    private static final int MAX_LINE_LENGTH = 10_000;

    /**
     * Tokenize Y language source code
     */
    public List<Token> tokenize(String source) {
        // Input validation
        if (source == null) {
            throw new IllegalArgumentException("Source code cannot be null");
        }

        if (source.length() > MAX_SOURCE_LENGTH) {
            throw new IllegalArgumentException(
                "Source code too large: " + source.length() + " characters (max: " + MAX_SOURCE_LENGTH + ")"
            );
        }

        List<Token> tokens = new ArrayList<>();
        String[] lines = source.split("\n");

        Stack<Integer> indentStack = new Stack<>();
        indentStack.push(0);

        for (int lineNum = 0; lineNum < lines.length; lineNum++) {
            String line = lines[lineNum];

            // Validate line length
            if (line.length() > MAX_LINE_LENGTH) {
                throw new IllegalArgumentException(
                    "Line " + (lineNum + 1) + " is too long: " + line.length() + " characters (max: " + MAX_LINE_LENGTH + ")"
                );
            }

            // Skip empty lines
            if (line.trim().isEmpty()) {
                continue;
            }

            // Calculate indentation
            int indent = 0;
            while (indent < line.length() && line.charAt(indent) == ' ') {
                indent++;
            }

            // Handle indentation changes
            if (indent > indentStack.peek()) {
                // Validate nesting depth
                if (indentStack.size() >= MAX_NESTING_DEPTH) {
                    throw new IllegalArgumentException(
                        "Nesting too deep at line " + (lineNum + 1) + " (max depth: " + MAX_NESTING_DEPTH + ")"
                    );
                }
                tokens.add(new Token(TokenType.INDENT, "", lineNum + 1, indent));
                indentStack.push(indent);
            } else if (indent < indentStack.peek()) {
                while (indentStack.size() > 1 && indent < indentStack.peek()) {
                    tokens.add(new Token(TokenType.DEDENT, "", lineNum + 1, indent));
                    indentStack.pop();
                }
            }

            // Tokenize the line
            String trimmedLine = line.trim();
            tokens.addAll(tokenizeLine(trimmedLine, lineNum + 1));

            // Add newline token
            tokens.add(new Token(TokenType.NEWLINE, "\\n", lineNum + 1, line.length()));
        }

        // Add remaining DEDENTs
        while (indentStack.size() > 1) {
            tokens.add(new Token(TokenType.DEDENT, "", lines.length, 0));
            indentStack.pop();
        }

        tokens.add(new Token(TokenType.EOF, "", lines.length + 1, 0));
        return tokens;
    }

    /**
     * Tokenize a single line
     */
    private List<Token> tokenizeLine(String line, int lineNum) {
        List<Token> tokens = new ArrayList<>();
        int col = 0;

        while (col < line.length()) {
            char ch = line.charAt(col);

            // Skip whitespace
            if (Character.isWhitespace(ch)) {
                col++;
                continue;
            }

            // Check for Yummy annotations
            if (ch == '(' && line.substring(col).startsWith("(Yummy:")) {
                Matcher matcher = YUMMY_PATTERN.matcher(line.substring(col));
                if (matcher.find()) {
                    String annotation = matcher.group(1).trim();
                    tokens.add(new Token(TokenType.YUMMY_ANNOTATION, annotation, lineNum, col));
                    col += matcher.group(0).length();
                    continue;
                }
            }

            // String literals
            if (ch == '"') {
                StringBuilder sb = new StringBuilder();
                col++; // Skip opening quote
                while (col < line.length() && line.charAt(col) != '"') {
                    if (line.charAt(col) == '\\' && col + 1 < line.length()) {
                        col++; // Skip escape character
                    }
                    sb.append(line.charAt(col));
                    col++;
                }
                if (col < line.length()) {
                    col++; // Skip closing quote
                }
                tokens.add(new Token(TokenType.STRING_LITERAL, sb.toString(), lineNum, col));
                continue;
            }

            // Number literals
            if (Character.isDigit(ch) || (ch == '-' && col + 1 < line.length() && Character.isDigit(line.charAt(col + 1)))) {
                StringBuilder sb = new StringBuilder();
                if (ch == '-') {
                    sb.append(ch);
                    col++;
                }
                while (col < line.length() && (Character.isDigit(line.charAt(col)) || line.charAt(col) == '.')) {
                    sb.append(line.charAt(col));
                    col++;
                }
                tokens.add(new Token(TokenType.NUMBER_LITERAL, sb.toString(), lineNum, col));
                continue;
            }

            // Punctuation
            switch (ch) {
                case '(':
                    tokens.add(new Token(TokenType.LPAREN, "(", lineNum, col));
                    col++;
                    continue;
                case ')':
                    tokens.add(new Token(TokenType.RPAREN, ")", lineNum, col));
                    col++;
                    continue;
                case ',':
                    tokens.add(new Token(TokenType.COMMA, ",", lineNum, col));
                    col++;
                    continue;
                case '.':
                    tokens.add(new Token(TokenType.DOT, ".", lineNum, col));
                    col++;
                    continue;
                case ':':
                    tokens.add(new Token(TokenType.COLON, ":", lineNum, col));
                    col++;
                    continue;
            }

            // Keywords and identifiers
            if (Character.isLetter(ch) || ch == '_') {
                StringBuilder sb = new StringBuilder();
                int startCol = col;

                // Read multi-word phrases
                while (col < line.length()) {
                    if (Character.isLetterOrDigit(line.charAt(col)) || line.charAt(col) == '_' || line.charAt(col) == ' ') {
                        sb.append(line.charAt(col));
                        col++;
                    } else {
                        break;
                    }
                }

                String word = sb.toString().trim();

                // Try to match multi-word keywords
                TokenType tokenType = matchKeyword(word);
                if (tokenType != null) {
                    tokens.add(new Token(tokenType, word, lineNum, startCol));
                } else {
                    // Split into individual words if not a keyword
                    String[] words = word.split("\\s+");
                    for (String w : words) {
                        TokenType type = KEYWORDS.get(w.toLowerCase());
                        if (type != null) {
                            tokens.add(new Token(type, w, lineNum, startCol));
                        } else {
                            tokens.add(new Token(TokenType.IDENTIFIER, w, lineNum, startCol));
                        }
                    }
                }
                continue;
            }

            // Unknown character - skip
            col++;
        }

        return tokens;
    }

    /**
     * Match multi-word keywords
     */
    private TokenType matchKeyword(String phrase) {
        String lower = phrase.toLowerCase().trim();

        // Multi-word operators
        if (lower.equals("multiplied by") || lower.contains("multiplied by")) {
            return TokenType.MULTIPLIED_BY;
        }
        if (lower.equals("divided by") || lower.contains("divided by")) {
            return TokenType.DIVIDED_BY;
        }
        if (lower.equals("greater than") || lower.contains("greater than")) {
            return TokenType.GREATER_THAN;
        }
        if (lower.equals("less than") || lower.contains("less than")) {
            return TokenType.LESS_THAN;
        }
        if (lower.equals("truth value") || lower.contains("truth value")) {
            return TokenType.TRUTH_VALUE;
        }

        // Single word keywords
        return KEYWORDS.get(lower);
    }
}
