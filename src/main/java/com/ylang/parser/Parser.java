package com.ylang.parser;

import com.ylang.ast.*;
import com.ylang.lexer.Token;
import com.ylang.lexer.TokenType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Parser for Y Language - converts tokens into AST
 */
@Component
public class Parser {

    private List<Token> tokens;
    private int position;

    public ProgramNode parse(List<Token> tokens) {
        this.tokens = tokens;
        this.position = 0;

        ProgramNode program = new ProgramNode();

        while (!isAtEnd()) {
            skipNewlines();
            if (isAtEnd()) break;

            ASTNode statement = parseStatement();
            if (statement != null) {
                program.addStatement(statement);
            }

            skipNewlines();
        }

        return program;
    }

    private ASTNode parseStatement() {
        Token current = peek();

        if (current.getType() == TokenType.DEFINE) {
            return parseDefinition();
        } else if (current.getType() == TokenType.CREATE) {
            return parseVariableDeclaration();
        } else if (current.getType() == TokenType.IMPLEMENT) {
            return parseImplementBlock();
        } else if (current.getType() == TokenType.IF) {
            return parseIfStatement();
        } else if (current.getType() == TokenType.FOR) {
            return parseForLoop();
        } else if (current.getType() == TokenType.WHILE) {
            return parseWhileLoop();
        } else if (current.getType() == TokenType.RETURN) {
            return parseReturnStatement();
        } else if (current.getType() == TokenType.TRY) {
            return parseTryCatch();
        } else if (current.getType() == TokenType.IDENTIFIER ||
                   current.getType() == TokenType.NUMBER_LITERAL ||
                   current.getType() == TokenType.STRING_LITERAL ||
                   current.getType() == TokenType.BOOLEAN_LITERAL) {
            // Could be assignment, function call, or literal expression
            return parseExpressionStatement();
        }

        // Skip unknown tokens
        advance();
        return null;
    }

    private ASTNode parseDefinition() {
        consume(TokenType.DEFINE); // "Define"

        Token next = peek();

        if (match(TokenType.FUNCTION) || matchSequence("a", "function")) {
            if (previous().getValue().equals("a")) {
                consume(TokenType.FUNCTION);
            }
            return parseFunctionDeclaration(false);
        } else if (match(TokenType.ASYNC)) {
            consume(TokenType.FUNCTION);
            return parseFunctionDeclaration(true);
        } else if (match(TokenType.STRUCTURE) || matchSequence("a", "structure")) {
            if (previous().getValue().equals("a")) {
                consume(TokenType.STRUCTURE);
            }
            return parseStructDeclaration();
        } else if (match(TokenType.METHOD) || matchSequence("a", "method")) {
            if (previous().getValue().equals("a")) {
                consume(TokenType.METHOD);
            }
            return parseMethodDeclaration();
        }

        return null;
    }

    private FunctionDeclarationNode parseFunctionDeclaration(boolean isAsync) {
        FunctionDeclarationNode function = new FunctionDeclarationNode();
        function.setAsync(isAsync);

        // "called" <name>
        consume(TokenType.CALLED);
        Token nameToken = consume(TokenType.IDENTIFIER);
        function.setName(nameToken.getValue());

        // Check for Yummy annotation
        if (peek().getType() == TokenType.YUMMY_ANNOTATION) {
            function.setYummyAnnotation(consume(TokenType.YUMMY_ANNOTATION).getValue());
        }

        // "that takes" parameters
        if (match(TokenType.THAT)) {
            consume(TokenType.TAKES);
            function.setParameters(parseParameters());
        }

        // "and returns" return type
        if (match(TokenType.AND)) {
            consume(TokenType.RETURNS);
            function.setReturnType(parseType());
        } else if (match(TokenType.RETURNS)) {
            function.setReturnType(parseType());
        }

        // Parse function body (indented block)
        skipNewlines();
        if (peek().getType() == TokenType.INDENT) {
            consume(TokenType.INDENT);
            function.setBody(parseBlock());
        }

        return function;
    }

    private StructDeclarationNode parseStructDeclaration() {
        StructDeclarationNode struct = new StructDeclarationNode();

        // "called" <name>
        consume(TokenType.CALLED);
        Token nameToken = consume(TokenType.IDENTIFIER);
        struct.setName(nameToken.getValue());

        // Check for Yummy annotation
        if (peek().getType() == TokenType.YUMMY_ANNOTATION) {
            struct.setYummyAnnotation(consume(TokenType.YUMMY_ANNOTATION).getValue());
        }

        // "with fields:"
        if (match(TokenType.WITH)) {
            consume(TokenType.FIELDS);
            consume(TokenType.COLON);
            skipNewlines();

            // Parse fields
            if (peek().getType() == TokenType.INDENT) {
                consume(TokenType.INDENT);
                struct.setFields(parseFields());
            }
        }

        return struct;
    }

    private List<FieldNode> parseFields() {
        List<FieldNode> fields = new ArrayList<>();

        while (!isAtEnd() && peek().getType() != TokenType.DEDENT) {
            skipNewlines();
            if (peek().getType() == TokenType.DEDENT) break;

            // Parse field: "- name as type"
            if (peek().getValue().equals("-")) {
                advance(); // skip "-"
            }

            Token nameToken = consume(TokenType.IDENTIFIER);
            consume(TokenType.AS);
            TypeNode type = parseType();

            FieldNode field = new FieldNode();
            field.setName(nameToken.getValue());
            field.setType(type);

            // Check for Yummy annotation
            if (peek().getType() == TokenType.YUMMY_ANNOTATION) {
                field.setYummyAnnotation(consume(TokenType.YUMMY_ANNOTATION).getValue());
            }

            fields.add(field);
            skipNewlines();
        }

        if (peek().getType() == TokenType.DEDENT) {
            consume(TokenType.DEDENT);
        }

        return fields;
    }

    private List<ParameterNode> parseParameters() {
        List<ParameterNode> params = new ArrayList<>();

        while (true) {
            Token nameToken = consume(TokenType.IDENTIFIER);
            consume(TokenType.AS);
            TypeNode type = parseType();

            ParameterNode param = new ParameterNode();
            param.setName(nameToken.getValue());
            param.setType(type);

            // Check for Yummy annotation
            if (peek().getType() == TokenType.YUMMY_ANNOTATION) {
                param.setYummyAnnotation(consume(TokenType.YUMMY_ANNOTATION).getValue());
            }

            params.add(param);

            // Check for "and" to continue parameters
            if (match(TokenType.AND)) {
                continue;
            } else {
                break;
            }
        }

        return params;
    }

    private TypeNode parseType() {
        TypeNode type = new TypeNode();

        // Check for modifiers
        if (match(TokenType.MUTABLE)) {
            type.setMutable(true);
        }
        if (match(TokenType.BORROWED)) {
            type.setBorrowed(true);
        }
        if (match(TokenType.OPTIONAL)) {
            type.setOptional(true);
        }

        // Parse base type
        Token typeToken = advance();
        type.setName(typeToken.getValue());

        // Check for Yummy annotation
        if (peek().getType() == TokenType.YUMMY_ANNOTATION) {
            type.setYummyAnnotation(consume(TokenType.YUMMY_ANNOTATION).getValue());
        }

        return type;
    }

    private VariableDeclarationNode parseVariableDeclaration() {
        consume(TokenType.CREATE); // "Create"

        VariableDeclarationNode var = new VariableDeclarationNode();

        // Check for "mutable"
        if (match(TokenType.MUTABLE)) {
            var.setMutable(true);
        }

        // "variable called" or just identifier
        if (match(TokenType.IDENTIFIER)) {
            if (previous().getValue().equalsIgnoreCase("variable")) {
                consume(TokenType.CALLED);
            } else {
                // Backtrack
                position--;
            }
        }

        Token nameToken = consume(TokenType.IDENTIFIER);
        var.setName(nameToken.getValue());

        // "with value" or "as type"
        if (match(TokenType.AS)) {
            var.setType(parseType());
        }

        if (matchSequence("with", "value") || matchSequence("with", "initial", "value")) {
            ASTNode initializer = parseExpression();
            var.setInitializer(initializer);
        }

        // Check for Yummy annotation
        if (peek().getType() == TokenType.YUMMY_ANNOTATION) {
            var.setYummyAnnotation(consume(TokenType.YUMMY_ANNOTATION).getValue());
        }

        return var;
    }

    private ImplementBlockNode parseImplementBlock() {
        consume(TokenType.IMPLEMENT); // "Implement"

        ImplementBlockNode impl = new ImplementBlockNode();

        // Parse "methods for StructName" or "TraitName for StructName"
        Token first = consume(TokenType.IDENTIFIER);

        if (match(TokenType.FOR)) {
            // Trait implementation
            impl.setTraitName(first.getValue());
            Token structName = consume(TokenType.IDENTIFIER);
            impl.setStructName(structName.getValue());
        } else if (first.getValue().equalsIgnoreCase("methods")) {
            // Just methods
            consume(TokenType.FOR);
            Token structName = consume(TokenType.IDENTIFIER);
            impl.setStructName(structName.getValue());
        }

        // Check for Yummy annotation
        if (peek().getType() == TokenType.YUMMY_ANNOTATION) {
            impl.setYummyAnnotation(consume(TokenType.YUMMY_ANNOTATION).getValue());
        }

        consume(TokenType.COLON);
        skipNewlines();

        // Parse methods
        if (peek().getType() == TokenType.INDENT) {
            consume(TokenType.INDENT);
            while (!isAtEnd() && peek().getType() != TokenType.DEDENT) {
                skipNewlines();
                if (peek().getType() == TokenType.DEDENT) break;

                if (peek().getType() == TokenType.DEFINE) {
                    ASTNode method = parseDefinition();
                    if (method instanceof MethodDeclarationNode) {
                        impl.getMethods().add((MethodDeclarationNode) method);
                    }
                }
                skipNewlines();
            }
            if (peek().getType() == TokenType.DEDENT) {
                consume(TokenType.DEDENT);
            }
        }

        return impl;
    }

    private MethodDeclarationNode parseMethodDeclaration() {
        MethodDeclarationNode method = new MethodDeclarationNode();

        // "called" <name>
        consume(TokenType.CALLED);
        Token nameToken = consume(TokenType.IDENTIFIER);
        method.setName(nameToken.getValue());

        // Check for Yummy annotation
        if (peek().getType() == TokenType.YUMMY_ANNOTATION) {
            method.setYummyAnnotation(consume(TokenType.YUMMY_ANNOTATION).getValue());
        }

        // "that takes" parameters
        if (match(TokenType.THAT)) {
            consume(TokenType.TAKES);
            method.setParameters(parseParameters());
        }

        // "and returns" return type
        if (match(TokenType.AND)) {
            consume(TokenType.RETURNS);
            method.setReturnType(parseType());
        } else if (match(TokenType.RETURNS)) {
            method.setReturnType(parseType());
        }

        // Parse method body
        skipNewlines();
        if (peek().getType() == TokenType.INDENT) {
            consume(TokenType.INDENT);
            method.setBody(parseBlock());
        }

        return method;
    }

    private IfStatementNode parseIfStatement() {
        consume(TokenType.IF); // "If"

        IfStatementNode ifStmt = new IfStatementNode();
        ifStmt.setCondition(parseExpression());

        consume(TokenType.COLON);
        skipNewlines();

        // Parse then block
        if (peek().getType() == TokenType.INDENT) {
            consume(TokenType.INDENT);
            ifStmt.setThenBlock(parseBlock());
        }

        // Parse else/otherwise block
        skipNewlines();
        if (match(TokenType.OTHERWISE)) {
            consume(TokenType.COLON);
            skipNewlines();
            if (peek().getType() == TokenType.INDENT) {
                consume(TokenType.INDENT);
                ifStmt.setElseBlock(parseBlock());
            }
        }

        return ifStmt;
    }

    private ForLoopNode parseForLoop() {
        consume(TokenType.FOR); // "For"

        ForLoopNode loop = new ForLoopNode();

        // "each <var> in <iterable>"
        if (match(TokenType.IDENTIFIER)) {
            if (previous().getValue().equalsIgnoreCase("each")) {
                // continue
            } else {
                position--;
            }
        }

        Token varToken = consume(TokenType.IDENTIFIER);
        loop.setVariable(varToken.getValue());

        // "in"
        if (match(TokenType.IDENTIFIER) && previous().getValue().equalsIgnoreCase("in")) {
            loop.setIterable(parseExpression());
        }

        consume(TokenType.COLON);
        skipNewlines();

        // Parse loop body
        if (peek().getType() == TokenType.INDENT) {
            consume(TokenType.INDENT);
            loop.setBody(parseBlock());
        }

        return loop;
    }

    private WhileLoopNode parseWhileLoop() {
        consume(TokenType.WHILE); // "While"

        WhileLoopNode loop = new WhileLoopNode();
        loop.setCondition(parseExpression());

        consume(TokenType.COLON);
        skipNewlines();

        // Parse loop body
        if (peek().getType() == TokenType.INDENT) {
            consume(TokenType.INDENT);
            loop.setBody(parseBlock());
        }

        return loop;
    }

    private ReturnStatementNode parseReturnStatement() {
        consume(TokenType.RETURN); // "Return"

        ReturnStatementNode ret = new ReturnStatementNode();
        ret.setExpression(parseExpression());

        return ret;
    }

    private TryCatchNode parseTryCatch() {
        consume(TokenType.TRY); // "Try"
        consume(TokenType.COLON);
        skipNewlines();

        TryCatchNode tryCatch = new TryCatchNode();

        // Parse try block
        if (peek().getType() == TokenType.INDENT) {
            consume(TokenType.INDENT);
            tryCatch.setTryBlock(parseBlock());
        }

        // Parse catch block
        skipNewlines();
        if (match(TokenType.CATCH)) {
            Token errorVar = consume(TokenType.IDENTIFIER);
            tryCatch.setErrorVariable(errorVar.getValue());

            consume(TokenType.COLON);
            skipNewlines();

            if (peek().getType() == TokenType.INDENT) {
                consume(TokenType.INDENT);
                tryCatch.setCatchBlock(parseBlock());
            }
        }

        return tryCatch;
    }

    private ASTNode parseExpressionStatement() {
        return parseExpression();
    }

    private ASTNode parseExpression() {
        return parseBinaryExpression();
    }

    private ASTNode parseBinaryExpression() {
        ASTNode left = parsePrimary();

        while (isBinaryOperator(peek())) {
            Token op = advance();
            ASTNode right = parsePrimary();
            left = new BinaryExpressionNode(left, op.getValue(), right);
        }

        return left;
    }

    private ASTNode parsePrimary() {
        Token token = peek();

        if (token.getType() == TokenType.NUMBER_LITERAL) {
            advance();
            return new LiteralNode(Double.parseDouble(token.getValue()), "number");
        } else if (token.getType() == TokenType.STRING_LITERAL) {
            advance();
            return new LiteralNode(token.getValue(), "string");
        } else if (token.getType() == TokenType.BOOLEAN_LITERAL) {
            advance();
            return new LiteralNode(Boolean.parseBoolean(token.getValue()), "boolean");
        } else if (token.getType() == TokenType.IDENTIFIER) {
            Token nameToken = advance();

            // Check for function call
            if (peek().getType() == TokenType.LPAREN) {
                return parseFunctionCall(nameToken.getValue());
            }

            return new IdentifierNode(nameToken.getValue());
        }

        // Unknown expression, skip token
        advance();
        return new IdentifierNode("unknown");
    }

    private FunctionCallNode parseFunctionCall(String functionName) {
        consume(TokenType.LPAREN);

        FunctionCallNode call = new FunctionCallNode();
        call.setFunctionName(functionName);

        // Parse arguments
        while (peek().getType() != TokenType.RPAREN && !isAtEnd()) {
            call.getArguments().add(parseExpression());

            if (peek().getType() == TokenType.COMMA) {
                consume(TokenType.COMMA);
            }
        }

        consume(TokenType.RPAREN);
        return call;
    }

    private List<ASTNode> parseBlock() {
        List<ASTNode> statements = new ArrayList<>();

        while (!isAtEnd() && peek().getType() != TokenType.DEDENT) {
            skipNewlines();
            if (peek().getType() == TokenType.DEDENT) break;

            ASTNode stmt = parseStatement();
            if (stmt != null) {
                statements.add(stmt);
            }

            skipNewlines();
        }

        if (peek().getType() == TokenType.DEDENT) {
            consume(TokenType.DEDENT);
        }

        return statements;
    }

    // Helper methods

    private boolean isBinaryOperator(Token token) {
        return token.getType() == TokenType.PLUS ||
               token.getType() == TokenType.MINUS ||
               token.getType() == TokenType.MULTIPLIED_BY ||
               token.getType() == TokenType.DIVIDED_BY ||
               token.getType() == TokenType.EQUALS ||
               token.getType() == TokenType.GREATER_THAN ||
               token.getType() == TokenType.LESS_THAN ||
               token.getType() == TokenType.IS ||
               token.getType() == TokenType.AND ||
               token.getType() == TokenType.OR;
    }

    private void skipNewlines() {
        while (!isAtEnd() && peek().getType() == TokenType.NEWLINE) {
            advance();
        }
    }

    private boolean match(TokenType type) {
        if (peek().getType() == type) {
            advance();
            return true;
        }
        return false;
    }

    private boolean matchSequence(String... words) {
        int saved = position;
        for (String word : words) {
            if (isAtEnd() || !peek().getValue().equalsIgnoreCase(word)) {
                position = saved;
                return false;
            }
            advance();
        }
        return true;
    }

    private Token peek() {
        if (position >= tokens.size()) {
            return tokens.get(tokens.size() - 1);
        }
        return tokens.get(position);
    }

    private Token previous() {
        return tokens.get(position - 1);
    }

    private Token advance() {
        if (!isAtEnd()) {
            position++;
        }
        return previous();
    }

    private Token consume(TokenType type) {
        if (peek().getType() == type) {
            return advance();
        }
        throw new RuntimeException("Expected " + type + " but got " + peek().getType() + " at " + peek());
    }

    private boolean isAtEnd() {
        return position >= tokens.size() || peek().getType() == TokenType.EOF;
    }
}
