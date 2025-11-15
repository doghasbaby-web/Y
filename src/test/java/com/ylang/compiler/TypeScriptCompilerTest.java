package com.ylang.compiler;

import com.ylang.ast.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class TypeScriptCompilerTest {

    private TypeScriptCompiler compiler;

    @BeforeEach
    void setUp() {
        compiler = new TypeScriptCompiler();
    }

    @Test
    void testCompileFunctionDeclaration() {
        FunctionDeclarationNode function = new FunctionDeclarationNode();
        function.setName("add");

        ParameterNode param1 = new ParameterNode("x", new TypeNode("number"), null);
        ParameterNode param2 = new ParameterNode("y", new TypeNode("number"), null);
        function.setParameters(Arrays.asList(param1, param2));
        function.setReturnType(new TypeNode("number"));

        ReturnStatementNode returnStmt = new ReturnStatementNode(
                new BinaryExpressionNode(
                        new IdentifierNode("x"),
                        "plus",
                        new IdentifierNode("y")
                )
        );
        function.setBody(Arrays.asList(returnStmt));

        String result = compiler.visitFunctionDeclaration(function);

        assertTrue(result.contains("function add"));
        assertTrue(result.contains("x: number"));
        assertTrue(result.contains("y: number"));
        assertTrue(result.contains(": number"));
        assertTrue(result.contains("return"));
    }

    @Test
    void testCompileStructDeclaration() {
        StructDeclarationNode struct = new StructDeclarationNode();
        struct.setName("Person");

        FieldNode field1 = new FieldNode("name", new TypeNode("text"), null);
        FieldNode field2 = new FieldNode("age", new TypeNode("number"), null);
        struct.setFields(Arrays.asList(field1, field2));

        String result = compiler.visitStructDeclaration(struct);

        assertTrue(result.contains("interface Person"));
        assertTrue(result.contains("name: string"));
        assertTrue(result.contains("age: number"));
    }

    @Test
    void testCompileVariableDeclaration() {
        VariableDeclarationNode var = new VariableDeclarationNode();
        var.setName("counter");
        var.setType(new TypeNode("number"));
        var.setInitializer(new LiteralNode(0, "number"));
        var.setMutable(true);

        String result = compiler.visitVariableDeclaration(var);

        assertTrue(result.contains("let counter"));
        assertTrue(result.contains(": number"));
        assertTrue(result.contains("= 0"));
    }

    @Test
    void testCompileIfStatement() {
        IfStatementNode ifStmt = new IfStatementNode();
        ifStmt.setCondition(new BinaryExpressionNode(
                new IdentifierNode("x"),
                "greater than",
                new LiteralNode(10, "number")
        ));

        FunctionCallNode printCall = new FunctionCallNode();
        printCall.setFunctionName("print");
        printCall.setArguments(Arrays.asList(new LiteralNode("Greater", "string")));

        ifStmt.setThenBlock(Arrays.asList(printCall));
        ifStmt.setElseBlock(new ArrayList<>());

        String result = compiler.visitIfStatement(ifStmt);

        assertTrue(result.contains("if ("));
        assertTrue(result.contains("x > 10"));
        assertTrue(result.contains("print(\"Greater\")"));
    }

    @Test
    void testCompileAsyncFunction() {
        FunctionDeclarationNode function = new FunctionDeclarationNode();
        function.setName("fetchData");
        function.setAsync(true);
        function.setReturnType(new TypeNode("text"));
        function.setParameters(new ArrayList<>());
        function.setBody(new ArrayList<>());

        String result = compiler.visitFunctionDeclaration(function);

        assertTrue(result.contains("async function fetchData"));
        assertTrue(result.contains("Promise<string>"));
    }
}
