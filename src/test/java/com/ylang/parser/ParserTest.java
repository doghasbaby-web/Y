package com.ylang.parser;

import com.ylang.ast.*;
import com.ylang.lexer.Token;
import com.ylang.lexer.TokenType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for the Parser
 */
class ParserTest {

    private Parser parser;

    @BeforeEach
    void setUp() {
        parser = new Parser();
    }

    private List<Token> createTokens(Object... tokenDefs) {
        List<Token> tokens = new ArrayList<>();
        for (int i = 0; i < tokenDefs.length; i += 2) {
            TokenType type = (TokenType) tokenDefs[i];
            String value = (String) tokenDefs[i + 1];
            tokens.add(new Token(type, value, 0));
        }
        tokens.add(new Token(TokenType.EOF, "", 0));
        return tokens;
    }

    @Test
    void testParseFunctionDeclaration() {
        List<Token> tokens = createTokens(
                TokenType.DEFINE, "Define",
                TokenType.FUNCTION, "function",
                TokenType.CALLED, "called",
                TokenType.IDENTIFIER, "add",
                TokenType.THAT, "that",
                TokenType.TAKES, "takes",
                TokenType.IDENTIFIER, "x",
                TokenType.AS, "as",
                TokenType.IDENTIFIER, "Number",
                TokenType.AND, "and",
                TokenType.IDENTIFIER, "y",
                TokenType.AS, "as",
                TokenType.IDENTIFIER, "Number",
                TokenType.AND, "and",
                TokenType.RETURNS, "returns",
                TokenType.IDENTIFIER, "Number",
                TokenType.NEWLINE, "\n",
                TokenType.INDENT, "INDENT",
                TokenType.RETURN, "Return",
                TokenType.IDENTIFIER, "x",
                TokenType.PLUS, "plus",
                TokenType.IDENTIFIER, "y",
                TokenType.NEWLINE, "\n",
                TokenType.DEDENT, "DEDENT"
        );

        ProgramNode program = parser.parse(tokens);

        assertNotNull(program);
        assertEquals(1, program.getStatements().size());
        assertTrue(program.getStatements().get(0) instanceof FunctionDeclarationNode);

        FunctionDeclarationNode func = (FunctionDeclarationNode) program.getStatements().get(0);
        assertEquals("add", func.getName());
        assertFalse(func.isAsync());
        assertEquals(2, func.getParameters().size());
        assertEquals("x", func.getParameters().get(0).getName());
        assertEquals("y", func.getParameters().get(1).getName());
        assertNotNull(func.getReturnType());
        assertEquals("Number", func.getReturnType().getName());
    }

    @Test
    void testParseAsyncFunction() {
        List<Token> tokens = createTokens(
                TokenType.DEFINE, "Define",
                TokenType.ASYNC, "async",
                TokenType.FUNCTION, "function",
                TokenType.CALLED, "called",
                TokenType.IDENTIFIER, "fetchData",
                TokenType.RETURNS, "returns",
                TokenType.IDENTIFIER, "Promise",
                TokenType.NEWLINE, "\n",
                TokenType.INDENT, "INDENT",
                TokenType.RETURN, "Return",
                TokenType.IDENTIFIER, "data",
                TokenType.NEWLINE, "\n",
                TokenType.DEDENT, "DEDENT"
        );

        ProgramNode program = parser.parse(tokens);

        assertNotNull(program);
        assertEquals(1, program.getStatements().size());
        assertTrue(program.getStatements().get(0) instanceof FunctionDeclarationNode);

        FunctionDeclarationNode func = (FunctionDeclarationNode) program.getStatements().get(0);
        assertEquals("fetchData", func.getName());
        assertTrue(func.isAsync());
    }

    @Test
    void testParseFunctionWithYummyAnnotation() {
        List<Token> tokens = createTokens(
                TokenType.DEFINE, "Define",
                TokenType.FUNCTION, "function",
                TokenType.CALLED, "called",
                TokenType.IDENTIFIER, "test",
                TokenType.YUMMY_ANNOTATION, "@yummy: This is a test function",
                TokenType.RETURNS, "returns",
                TokenType.IDENTIFIER, "Void"
        );

        ProgramNode program = parser.parse(tokens);

        FunctionDeclarationNode func = (FunctionDeclarationNode) program.getStatements().get(0);
        assertEquals("test", func.getName());
        assertEquals("@yummy: This is a test function", func.getYummyAnnotation());
    }

    @Test
    void testParseStructDeclaration() {
        List<Token> tokens = createTokens(
                TokenType.DEFINE, "Define",
                TokenType.STRUCTURE, "structure",
                TokenType.CALLED, "called",
                TokenType.IDENTIFIER, "User",
                TokenType.WITH, "with",
                TokenType.FIELDS, "fields",
                TokenType.COLON, ":",
                TokenType.NEWLINE, "\n",
                TokenType.INDENT, "INDENT",
                TokenType.IDENTIFIER, "name",
                TokenType.AS, "as",
                TokenType.IDENTIFIER, "String",
                TokenType.NEWLINE, "\n",
                TokenType.IDENTIFIER, "age",
                TokenType.AS, "as",
                TokenType.IDENTIFIER, "Number",
                TokenType.NEWLINE, "\n",
                TokenType.DEDENT, "DEDENT"
        );

        ProgramNode program = parser.parse(tokens);

        assertNotNull(program);
        assertEquals(1, program.getStatements().size());
        assertTrue(program.getStatements().get(0) instanceof StructDeclarationNode);

        StructDeclarationNode struct = (StructDeclarationNode) program.getStatements().get(0);
        assertEquals("User", struct.getName());
        assertEquals(2, struct.getFields().size());
        assertEquals("name", struct.getFields().get(0).getName());
        assertEquals("String", struct.getFields().get(0).getType().getName());
        assertEquals("age", struct.getFields().get(1).getName());
        assertEquals("Number", struct.getFields().get(1).getType().getName());
    }

    @Test
    void testParseVariableDeclaration() {
        List<Token> tokens = createTokens(
                TokenType.CREATE, "Create",
                TokenType.IDENTIFIER, "variable",
                TokenType.CALLED, "called",
                TokenType.IDENTIFIER, "count",
                TokenType.AS, "as",
                TokenType.IDENTIFIER, "Number",
                TokenType.IDENTIFIER, "with",
                TokenType.IDENTIFIER, "value",
                TokenType.NUMBER_LITERAL, "10"
        );

        ProgramNode program = parser.parse(tokens);

        assertNotNull(program);
        assertEquals(1, program.getStatements().size());
        assertTrue(program.getStatements().get(0) instanceof VariableDeclarationNode);

        VariableDeclarationNode var = (VariableDeclarationNode) program.getStatements().get(0);
        assertEquals("count", var.getName());
        assertNotNull(var.getType());
        assertEquals("Number", var.getType().getName());
        assertNotNull(var.getInitializer());
    }

    @Test
    void testParseMutableVariable() {
        List<Token> tokens = createTokens(
                TokenType.CREATE, "Create",
                TokenType.MUTABLE, "mutable",
                TokenType.IDENTIFIER, "variable",
                TokenType.CALLED, "called",
                TokenType.IDENTIFIER, "counter",
                TokenType.AS, "as",
                TokenType.IDENTIFIER, "Number"
        );

        ProgramNode program = parser.parse(tokens);

        VariableDeclarationNode var = (VariableDeclarationNode) program.getStatements().get(0);
        assertEquals("counter", var.getName());
        assertTrue(var.isMutable());
    }

    @Test
    void testParseIfStatement() {
        List<Token> tokens = createTokens(
                TokenType.IF, "If",
                TokenType.IDENTIFIER, "x",
                TokenType.GREATER_THAN, "greater than",
                TokenType.NUMBER_LITERAL, "5",
                TokenType.COLON, ":",
                TokenType.NEWLINE, "\n",
                TokenType.INDENT, "INDENT",
                TokenType.RETURN, "Return",
                TokenType.IDENTIFIER, "true",
                TokenType.NEWLINE, "\n",
                TokenType.DEDENT, "DEDENT"
        );

        ProgramNode program = parser.parse(tokens);

        assertNotNull(program);
        assertEquals(1, program.getStatements().size());
        assertTrue(program.getStatements().get(0) instanceof IfStatementNode);

        IfStatementNode ifStmt = (IfStatementNode) program.getStatements().get(0);
        assertNotNull(ifStmt.getCondition());
        assertNotNull(ifStmt.getThenBlock());
        assertEquals(1, ifStmt.getThenBlock().size());
    }

    @Test
    void testParseIfElseStatement() {
        List<Token> tokens = createTokens(
                TokenType.IF, "If",
                TokenType.IDENTIFIER, "x",
                TokenType.IS, "is",
                TokenType.IDENTIFIER, "true",
                TokenType.COLON, ":",
                TokenType.NEWLINE, "\n",
                TokenType.INDENT, "INDENT",
                TokenType.RETURN, "Return",
                TokenType.NUMBER_LITERAL, "1",
                TokenType.NEWLINE, "\n",
                TokenType.DEDENT, "DEDENT",
                TokenType.NEWLINE, "\n",
                TokenType.OTHERWISE, "Otherwise",
                TokenType.COLON, ":",
                TokenType.NEWLINE, "\n",
                TokenType.INDENT, "INDENT",
                TokenType.RETURN, "Return",
                TokenType.NUMBER_LITERAL, "0",
                TokenType.NEWLINE, "\n",
                TokenType.DEDENT, "DEDENT"
        );

        ProgramNode program = parser.parse(tokens);

        IfStatementNode ifStmt = (IfStatementNode) program.getStatements().get(0);
        assertNotNull(ifStmt.getThenBlock());
        assertNotNull(ifStmt.getElseBlock());
        assertEquals(1, ifStmt.getElseBlock().size());
    }

    @Test
    void testParseForLoop() {
        List<Token> tokens = createTokens(
                TokenType.FOR, "For",
                TokenType.IDENTIFIER, "each",
                TokenType.IDENTIFIER, "item",
                TokenType.IDENTIFIER, "in",
                TokenType.IDENTIFIER, "items",
                TokenType.COLON, ":",
                TokenType.NEWLINE, "\n",
                TokenType.INDENT, "INDENT",
                TokenType.IDENTIFIER, "print",
                TokenType.LPAREN, "(",
                TokenType.IDENTIFIER, "item",
                TokenType.RPAREN, ")",
                TokenType.NEWLINE, "\n",
                TokenType.DEDENT, "DEDENT"
        );

        ProgramNode program = parser.parse(tokens);

        assertNotNull(program);
        assertEquals(1, program.getStatements().size());
        assertTrue(program.getStatements().get(0) instanceof ForLoopNode);

        ForLoopNode loop = (ForLoopNode) program.getStatements().get(0);
        assertEquals("item", loop.getVariable());
        assertNotNull(loop.getIterable());
        assertNotNull(loop.getBody());
    }

    @Test
    void testParseWhileLoop() {
        List<Token> tokens = createTokens(
                TokenType.WHILE, "While",
                TokenType.IDENTIFIER, "count",
                TokenType.LESS_THAN, "less than",
                TokenType.NUMBER_LITERAL, "10",
                TokenType.COLON, ":",
                TokenType.NEWLINE, "\n",
                TokenType.INDENT, "INDENT",
                TokenType.IDENTIFIER, "count",
                TokenType.PLUS, "plus",
                TokenType.NUMBER_LITERAL, "1",
                TokenType.NEWLINE, "\n",
                TokenType.DEDENT, "DEDENT"
        );

        ProgramNode program = parser.parse(tokens);

        assertNotNull(program);
        assertEquals(1, program.getStatements().size());
        assertTrue(program.getStatements().get(0) instanceof WhileLoopNode);

        WhileLoopNode loop = (WhileLoopNode) program.getStatements().get(0);
        assertNotNull(loop.getCondition());
        assertNotNull(loop.getBody());
    }

    @Test
    void testParseTryCatch() {
        List<Token> tokens = createTokens(
                TokenType.TRY, "Try",
                TokenType.COLON, ":",
                TokenType.NEWLINE, "\n",
                TokenType.INDENT, "INDENT",
                TokenType.IDENTIFIER, "riskyOperation",
                TokenType.LPAREN, "(",
                TokenType.RPAREN, ")",
                TokenType.NEWLINE, "\n",
                TokenType.DEDENT, "DEDENT",
                TokenType.NEWLINE, "\n",
                TokenType.CATCH, "Catch",
                TokenType.IDENTIFIER, "error",
                TokenType.COLON, ":",
                TokenType.NEWLINE, "\n",
                TokenType.INDENT, "INDENT",
                TokenType.IDENTIFIER, "handleError",
                TokenType.LPAREN, "(",
                TokenType.IDENTIFIER, "error",
                TokenType.RPAREN, ")",
                TokenType.NEWLINE, "\n",
                TokenType.DEDENT, "DEDENT"
        );

        ProgramNode program = parser.parse(tokens);

        assertNotNull(program);
        assertEquals(1, program.getStatements().size());
        assertTrue(program.getStatements().get(0) instanceof TryCatchNode);

        TryCatchNode tryCatch = (TryCatchNode) program.getStatements().get(0);
        assertNotNull(tryCatch.getTryBlock());
        assertNotNull(tryCatch.getCatchBlock());
        assertEquals("error", tryCatch.getErrorVariable());
    }

    @Test
    void testParseMethodDeclaration() {
        List<Token> tokens = createTokens(
                TokenType.DEFINE, "Define",
                TokenType.METHOD, "method",
                TokenType.CALLED, "called",
                TokenType.IDENTIFIER, "getName",
                TokenType.RETURNS, "returns",
                TokenType.IDENTIFIER, "String",
                TokenType.NEWLINE, "\n",
                TokenType.INDENT, "INDENT",
                TokenType.RETURN, "Return",
                TokenType.IDENTIFIER, "self",
                TokenType.NEWLINE, "\n",
                TokenType.DEDENT, "DEDENT"
        );

        ProgramNode program = parser.parse(tokens);

        assertNotNull(program);
        assertEquals(1, program.getStatements().size());
        assertTrue(program.getStatements().get(0) instanceof MethodDeclarationNode);

        MethodDeclarationNode method = (MethodDeclarationNode) program.getStatements().get(0);
        assertEquals("getName", method.getName());
        assertNotNull(method.getReturnType());
    }

    @Test
    void testParseImplementBlock() {
        List<Token> tokens = createTokens(
                TokenType.IMPLEMENT, "Implement",
                TokenType.IDENTIFIER, "methods",
                TokenType.FOR, "for",
                TokenType.IDENTIFIER, "User",
                TokenType.COLON, ":",
                TokenType.NEWLINE, "\n",
                TokenType.INDENT, "INDENT",
                TokenType.DEFINE, "Define",
                TokenType.METHOD, "method",
                TokenType.CALLED, "called",
                TokenType.IDENTIFIER, "greet",
                TokenType.RETURNS, "returns",
                TokenType.IDENTIFIER, "String",
                TokenType.NEWLINE, "\n",
                TokenType.INDENT, "INDENT",
                TokenType.RETURN, "Return",
                TokenType.STRING_LITERAL, "\"Hello\"",
                TokenType.NEWLINE, "\n",
                TokenType.DEDENT, "DEDENT",
                TokenType.NEWLINE, "\n",
                TokenType.DEDENT, "DEDENT"
        );

        ProgramNode program = parser.parse(tokens);

        assertNotNull(program);
        assertEquals(1, program.getStatements().size());
        assertTrue(program.getStatements().get(0) instanceof ImplementBlockNode);

        ImplementBlockNode impl = (ImplementBlockNode) program.getStatements().get(0);
        assertEquals("User", impl.getStructName());
        assertEquals(1, impl.getMethods().size());
        assertEquals("greet", impl.getMethods().get(0).getName());
    }

    @Test
    void testParseFunctionCall() {
        List<Token> tokens = createTokens(
                TokenType.IDENTIFIER, "print",
                TokenType.LPAREN, "(",
                TokenType.STRING_LITERAL, "\"Hello World\"",
                TokenType.RPAREN, ")"
        );

        ProgramNode program = parser.parse(tokens);

        assertNotNull(program);
        assertEquals(1, program.getStatements().size());
        assertTrue(program.getStatements().get(0) instanceof FunctionCallNode);

        FunctionCallNode call = (FunctionCallNode) program.getStatements().get(0);
        assertEquals("print", call.getFunctionName());
        assertEquals(1, call.getArguments().size());
    }

    @Test
    void testParseFunctionCallWithMultipleArguments() {
        List<Token> tokens = createTokens(
                TokenType.IDENTIFIER, "add",
                TokenType.LPAREN, "(",
                TokenType.NUMBER_LITERAL, "5",
                TokenType.COMMA, ",",
                TokenType.NUMBER_LITERAL, "10",
                TokenType.COMMA, ",",
                TokenType.NUMBER_LITERAL, "15",
                TokenType.RPAREN, ")"
        );

        ProgramNode program = parser.parse(tokens);

        FunctionCallNode call = (FunctionCallNode) program.getStatements().get(0);
        assertEquals("add", call.getFunctionName());
        assertEquals(3, call.getArguments().size());
    }

    @Test
    void testParseBinaryExpression() {
        List<Token> tokens = createTokens(
                TokenType.IDENTIFIER, "x",
                TokenType.PLUS, "plus",
                TokenType.IDENTIFIER, "y",
                TokenType.MULTIPLIED_BY, "multiplied by",
                TokenType.IDENTIFIER, "z"
        );

        ProgramNode program = parser.parse(tokens);

        assertNotNull(program);
        assertEquals(1, program.getStatements().size());
        assertTrue(program.getStatements().get(0) instanceof BinaryExpressionNode);
    }

    @Test
    void testParseLiterals() {
        List<Token> tokens = createTokens(
                TokenType.NUMBER_LITERAL, "42",
                TokenType.NEWLINE, "\n",
                TokenType.STRING_LITERAL, "\"test\"",
                TokenType.NEWLINE, "\n",
                TokenType.BOOLEAN_LITERAL, "true"
        );

        ProgramNode program = parser.parse(tokens);

        assertNotNull(program);
        assertEquals(3, program.getStatements().size());
        assertTrue(program.getStatements().get(0) instanceof LiteralNode);
        assertTrue(program.getStatements().get(1) instanceof LiteralNode);
        assertTrue(program.getStatements().get(2) instanceof LiteralNode);

        LiteralNode numLiteral = (LiteralNode) program.getStatements().get(0);
        assertEquals(42.0, numLiteral.getValue());

        LiteralNode strLiteral = (LiteralNode) program.getStatements().get(1);
        assertEquals("\"test\"", strLiteral.getValue());

        LiteralNode boolLiteral = (LiteralNode) program.getStatements().get(2);
        assertEquals(true, boolLiteral.getValue());
    }

    @Test
    void testParseTypeWithModifiers() {
        List<Token> tokens = createTokens(
                TokenType.CREATE, "Create",
                TokenType.IDENTIFIER, "variable",
                TokenType.CALLED, "called",
                TokenType.IDENTIFIER, "data",
                TokenType.AS, "as",
                TokenType.MUTABLE, "mutable",
                TokenType.OPTIONAL, "optional",
                TokenType.IDENTIFIER, "String"
        );

        ProgramNode program = parser.parse(tokens);

        VariableDeclarationNode var = (VariableDeclarationNode) program.getStatements().get(0);
        assertNotNull(var.getType());
        assertTrue(var.getType().isMutable());
        assertTrue(var.getType().isOptional());
        assertEquals("String", var.getType().getName());
    }

    @Test
    void testParseBorrowedType() {
        List<Token> tokens = createTokens(
                TokenType.CREATE, "Create",
                TokenType.IDENTIFIER, "variable",
                TokenType.CALLED, "called",
                TokenType.IDENTIFIER, "ref",
                TokenType.AS, "as",
                TokenType.BORROWED, "borrowed",
                TokenType.IDENTIFIER, "String"
        );

        ProgramNode program = parser.parse(tokens);

        VariableDeclarationNode var = (VariableDeclarationNode) program.getStatements().get(0);
        assertNotNull(var.getType());
        assertTrue(var.getType().isBorrowed());
        assertEquals("String", var.getType().getName());
    }

    @Test
    void testParseEmptyProgram() {
        List<Token> tokens = createTokens();

        ProgramNode program = parser.parse(tokens);

        assertNotNull(program);
        assertEquals(0, program.getStatements().size());
    }

    @Test
    void testParseMultipleStatements() {
        List<Token> tokens = createTokens(
                TokenType.CREATE, "Create",
                TokenType.IDENTIFIER, "x",
                TokenType.AS, "as",
                TokenType.IDENTIFIER, "Number",
                TokenType.NEWLINE, "\n",
                TokenType.CREATE, "Create",
                TokenType.IDENTIFIER, "y",
                TokenType.AS, "as",
                TokenType.IDENTIFIER, "Number",
                TokenType.NEWLINE, "\n",
                TokenType.IDENTIFIER, "print",
                TokenType.LPAREN, "(",
                TokenType.IDENTIFIER, "x",
                TokenType.RPAREN, ")"
        );

        ProgramNode program = parser.parse(tokens);

        assertNotNull(program);
        assertEquals(3, program.getStatements().size());
    }

    @Test
    void testParseReturnStatement() {
        List<Token> tokens = createTokens(
                TokenType.RETURN, "Return",
                TokenType.NUMBER_LITERAL, "42"
        );

        ProgramNode program = parser.parse(tokens);

        assertNotNull(program);
        assertEquals(1, program.getStatements().size());
        assertTrue(program.getStatements().get(0) instanceof ReturnStatementNode);

        ReturnStatementNode ret = (ReturnStatementNode) program.getStatements().get(0);
        assertNotNull(ret.getExpression());
    }

    @Test
    void testParseFieldsWithYummyAnnotations() {
        List<Token> tokens = createTokens(
                TokenType.DEFINE, "Define",
                TokenType.STRUCTURE, "structure",
                TokenType.CALLED, "called",
                TokenType.IDENTIFIER, "Product",
                TokenType.WITH, "with",
                TokenType.FIELDS, "fields",
                TokenType.COLON, ":",
                TokenType.NEWLINE, "\n",
                TokenType.INDENT, "INDENT",
                TokenType.IDENTIFIER, "id",
                TokenType.AS, "as",
                TokenType.IDENTIFIER, "Number",
                TokenType.YUMMY_ANNOTATION, "@yummy: Unique identifier",
                TokenType.NEWLINE, "\n",
                TokenType.DEDENT, "DEDENT"
        );

        ProgramNode program = parser.parse(tokens);

        StructDeclarationNode struct = (StructDeclarationNode) program.getStatements().get(0);
        assertEquals(1, struct.getFields().size());
        assertEquals("@yummy: Unique identifier", struct.getFields().get(0).getYummyAnnotation());
    }

    @Test
    void testParseParametersWithYummyAnnotations() {
        List<Token> tokens = createTokens(
                TokenType.DEFINE, "Define",
                TokenType.FUNCTION, "function",
                TokenType.CALLED, "called",
                TokenType.IDENTIFIER, "test",
                TokenType.THAT, "that",
                TokenType.TAKES, "takes",
                TokenType.IDENTIFIER, "x",
                TokenType.AS, "as",
                TokenType.IDENTIFIER, "Number",
                TokenType.YUMMY_ANNOTATION, "@yummy: Input value"
        );

        ProgramNode program = parser.parse(tokens);

        FunctionDeclarationNode func = (FunctionDeclarationNode) program.getStatements().get(0);
        assertEquals(1, func.getParameters().size());
        assertEquals("@yummy: Input value", func.getParameters().get(0).getYummyAnnotation());
    }
}
