package com.ylang.compiler;

import com.ylang.ast.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class RustCompilerTest {

    private RustCompiler compiler;

    @BeforeEach
    void setUp() {
        compiler = new RustCompiler();
    }

    @Test
    void testCompileFunctionDeclaration() {
        FunctionDeclarationNode function = new FunctionDeclarationNode();
        function.setName("add");

        TypeNode numType = new TypeNode("number");
        numType.setYummyAnnotation("i32");

        ParameterNode param1 = new ParameterNode("x", numType, null);
        ParameterNode param2 = new ParameterNode("y", numType, null);
        function.setParameters(Arrays.asList(param1, param2));
        function.setReturnType(numType);

        ReturnStatementNode returnStmt = new ReturnStatementNode(
                new BinaryExpressionNode(
                        new IdentifierNode("x"),
                        "plus",
                        new IdentifierNode("y")
                )
        );
        function.setBody(Arrays.asList(returnStmt));

        String result = compiler.visitFunctionDeclaration(function);

        assertTrue(result.contains("fn add"));
        assertTrue(result.contains("x: i32"));
        assertTrue(result.contains("y: i32"));
        assertTrue(result.contains("-> i32"));
        assertTrue(result.contains("return"));
    }

    @Test
    void testCompileStructDeclaration() {
        StructDeclarationNode struct = new StructDeclarationNode();
        struct.setName("Person");
        struct.setYummyAnnotation("derive(Debug, Clone)");

        TypeNode stringType = new TypeNode("text");
        TypeNode numType = new TypeNode("number");
        numType.setYummyAnnotation("u32");

        FieldNode field1 = new FieldNode("name", stringType, null);
        FieldNode field2 = new FieldNode("age", numType, null);
        struct.setFields(Arrays.asList(field1, field2));

        String result = compiler.visitStructDeclaration(struct);

        assertTrue(result.contains("#[derive(Debug, Clone)]"));
        assertTrue(result.contains("struct Person"));
        assertTrue(result.contains("name: String"));
        assertTrue(result.contains("age: u32"));
    }

    @Test
    void testCompileMutableVariable() {
        VariableDeclarationNode var = new VariableDeclarationNode();
        var.setName("counter");

        TypeNode numType = new TypeNode("number");
        numType.setYummyAnnotation("i32");

        var.setType(numType);
        var.setInitializer(new LiteralNode(0, "number"));
        var.setMutable(true);

        String result = compiler.visitVariableDeclaration(var);

        assertTrue(result.contains("let mut counter"));
        assertTrue(result.contains(": i32"));
        assertTrue(result.contains("= 0"));
    }

    @Test
    void testCompileBorrowedType() {
        TypeNode type = new TypeNode("text");
        type.setBorrowed(true);
        type.setYummyAnnotation("&str");

        VariableDeclarationNode var = new VariableDeclarationNode();
        var.setName("message");
        var.setType(type);
        var.setMutable(false);

        String result = compiler.visitVariableDeclaration(var);

        assertTrue(result.contains("let message"));
        assertTrue(result.contains("&str"));
    }

    @Test
    void testCompileMethodWithSelf() {
        MethodDeclarationNode method = new MethodDeclarationNode();
        method.setName("get_name");
        method.setStatic(false);
        method.setYummyAnnotation("&self");

        TypeNode stringType = new TypeNode("text");
        stringType.setBorrowed(true);

        method.setReturnType(stringType);
        method.setParameters(new ArrayList<>());
        method.setBody(new ArrayList<>());

        String result = compiler.visitMethodDeclaration(method);

        assertTrue(result.contains("fn get_name"));
        assertTrue(result.contains("&self"));
        assertTrue(result.contains("-> &String"));
    }
}
