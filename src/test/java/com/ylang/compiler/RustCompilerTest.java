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

    @Test
    void testCompileForLoop() {
        ForLoopNode loop = new ForLoopNode();
        loop.setVariable("item");
        loop.setIterable(new IdentifierNode("items"));

        FunctionCallNode printCall = new FunctionCallNode();
        printCall.setFunctionName("print");
        printCall.setArguments(Arrays.asList(new IdentifierNode("item")));
        loop.setBody(Arrays.asList(printCall));

        String result = compiler.visitForLoop(loop);

        assertTrue(result.contains("for item in items"));
        assertTrue(result.contains("println!"));
    }

    @Test
    void testCompileWhileLoop() {
        WhileLoopNode loop = new WhileLoopNode();
        loop.setCondition(new BinaryExpressionNode(
                new IdentifierNode("count"),
                "less than",
                new LiteralNode(10, "number")
        ));

        BinaryExpressionNode increment = new BinaryExpressionNode(
                new IdentifierNode("count"),
                "plus",
                new LiteralNode(1, "number")
        );
        loop.setBody(Arrays.asList(increment));

        String result = compiler.visitWhileLoop(loop);

        assertTrue(result.contains("while count < 10"));
    }

    @Test
    void testCompileIfStatement() {
        IfStatementNode ifStmt = new IfStatementNode();
        ifStmt.setCondition(new BinaryExpressionNode(
                new IdentifierNode("x"),
                "greater than",
                new LiteralNode(5, "number")
        ));

        ReturnStatementNode returnStmt = new ReturnStatementNode();
        returnStmt.setExpression(new IdentifierNode("true"));
        ifStmt.setThenBlock(Arrays.asList(returnStmt));

        String result = compiler.visitIfStatement(ifStmt);

        assertTrue(result.contains("if x > 5"));
    }

    @Test
    void testCompileIfElseStatement() {
        IfStatementNode ifStmt = new IfStatementNode();
        ifStmt.setCondition(new IdentifierNode("condition"));

        ReturnStatementNode thenStmt = new ReturnStatementNode();
        thenStmt.setExpression(new LiteralNode(1, "number"));
        ifStmt.setThenBlock(Arrays.asList(thenStmt));

        ReturnStatementNode elseStmt = new ReturnStatementNode();
        elseStmt.setExpression(new LiteralNode(0, "number"));
        ifStmt.setElseBlock(Arrays.asList(elseStmt));

        String result = compiler.visitIfStatement(ifStmt);

        assertTrue(result.contains("if condition"));
        assertTrue(result.contains("else"));
    }

    @Test
    void testCompileTryCatch() {
        TryCatchNode tryCatch = new TryCatchNode();

        FunctionCallNode riskyCall = new FunctionCallNode();
        riskyCall.setFunctionName("riskyOperation");
        tryCatch.setTryBlock(Arrays.asList(riskyCall));

        FunctionCallNode errorHandler = new FunctionCallNode();
        errorHandler.setFunctionName("handleError");
        errorHandler.setArguments(Arrays.asList(new IdentifierNode("e")));
        tryCatch.setCatchBlock(Arrays.asList(errorHandler));
        tryCatch.setErrorVariable("e");

        String result = compiler.visitTryCatch(tryCatch);

        assertTrue(result.contains("match"));
        assertTrue(result.contains("Ok(") || result.contains("Err("));
    }

    @Test
    void testCompileEnumDeclaration() {
        EnumDeclarationNode enumNode = new EnumDeclarationNode();
        enumNode.setName("Status");

        EnumVariantNode variant1 = new EnumVariantNode();
        variant1.setName("Active");
        EnumVariantNode variant2 = new EnumVariantNode();
        variant2.setName("Inactive");

        enumNode.setVariants(Arrays.asList(variant1, variant2));

        String result = compiler.visitEnumDeclaration(enumNode);

        assertTrue(result.contains("enum Status"));
        assertTrue(result.contains("Active"));
        assertTrue(result.contains("Inactive"));
    }

    @Test
    void testCompileTraitDeclaration() {
        TraitDeclarationNode trait = new TraitDeclarationNode();
        trait.setName("Printable");

        MethodSignatureNode method = new MethodSignatureNode();
        method.setName("print");

        TypeNode returnType = new TypeNode();
        returnType.setName("Nothing");
        method.setReturnType(returnType);

        trait.setMethods(Arrays.asList(method));

        String result = compiler.visitTraitDeclaration(trait);

        assertTrue(result.contains("trait Printable"));
        assertTrue(result.contains("fn print"));
    }

    @Test
    void testCompileImplementBlock() {
        ImplementBlockNode impl = new ImplementBlockNode();
        impl.setStructName("User");

        MethodDeclarationNode method = new MethodDeclarationNode();
        method.setName("greet");
        ReturnStatementNode returnStmt = new ReturnStatementNode();
        returnStmt.setExpression(new LiteralNode("Hello", "string"));
        method.setBody(Arrays.asList(returnStmt));

        impl.setMethods(Arrays.asList(method));

        String result = compiler.visitImplementBlock(impl);

        assertTrue(result.contains("impl User"));
        assertTrue(result.contains("fn greet"));
    }

    @Test
    void testCompileImplementBlockWithTrait() {
        ImplementBlockNode impl = new ImplementBlockNode();
        impl.setStructName("User");
        impl.setTraitName("Printable");

        MethodDeclarationNode method = new MethodDeclarationNode();
        method.setName("print");
        method.setBody(new ArrayList<>());

        impl.setMethods(Arrays.asList(method));

        String result = compiler.visitImplementBlock(impl);

        assertTrue(result.contains("impl Printable for User"));
    }

    @Test
    void testCompileMatch() {
        MatchNode match = new MatchNode();
        match.setExpression(new IdentifierNode("status"));

        CaseNode case1 = new CaseNode();
        case1.setPattern(new IdentifierNode("Active"));
        ReturnStatementNode ret1 = new ReturnStatementNode();
        ret1.setExpression(new LiteralNode(1, "number"));
        case1.setBody(Arrays.asList(ret1));

        CaseNode defaultCase = new CaseNode();
        defaultCase.setDefault(true);
        ReturnStatementNode ret2 = new ReturnStatementNode();
        ret2.setExpression(new LiteralNode(0, "number"));
        defaultCase.setBody(Arrays.asList(ret2));

        match.setCases(Arrays.asList(case1, defaultCase));

        String result = compiler.visitMatch(match);

        assertTrue(result.contains("match status"));
        assertTrue(result.contains("Active =>");
        assertTrue(result.contains("_ =>"));
    }

    @Test
    void testCompileClassDeclaration() {
        ClassDeclarationNode classNode = new ClassDeclarationNode();
        classNode.setName("Person");

        FieldNode field = new FieldNode();
        field.setName("name");
        TypeNode fieldType = new TypeNode();
        fieldType.setName("text");
        field.setType(fieldType);
        classNode.setFields(Arrays.asList(field));

        String result = compiler.visitClassDeclaration(classNode);

        assertTrue(result.contains("struct Person"));
        assertTrue(result.contains("name: String"));
    }

    @Test
    void testCompileOptionalType() {
        VariableDeclarationNode var = new VariableDeclarationNode();
        var.setName("maybeValue");

        TypeNode type = new TypeNode();
        type.setName("text");
        type.setOptional(true);
        var.setType(type);

        String result = compiler.visitVariableDeclaration(var);

        assertTrue(result.contains("maybeValue"));
        assertTrue(result.contains("Option<String>"));
    }

    @Test
    void testCompileImmutableVariable() {
        VariableDeclarationNode var = new VariableDeclarationNode();
        var.setName("value");

        TypeNode type = new TypeNode();
        type.setName("number");
        type.setYummyAnnotation("i32");
        var.setType(type);
        var.setInitializer(new LiteralNode(42, "number"));
        var.setMutable(false);

        String result = compiler.visitVariableDeclaration(var);

        assertTrue(result.contains("let value"));
        assertFalse(result.contains("mut"));
    }

    @Test
    void testCompileBinaryExpression() {
        BinaryExpressionNode expr = new BinaryExpressionNode(
                new LiteralNode(5, "number"),
                "plus",
                new LiteralNode(3, "number")
        );

        String result = compiler.visitBinaryExpression(expr);

        assertTrue(result.contains("5 + 3"));
    }

    @Test
    void testCompileFunctionCall() {
        FunctionCallNode call = new FunctionCallNode();
        call.setFunctionName("print");
        call.setArguments(Arrays.asList(new LiteralNode("Hello", "string")));

        String result = compiler.visitFunctionCall(call);

        assertTrue(result.contains("println!"));
        assertTrue(result.contains("\"Hello\""));
    }

    @Test
    void testCompileReturnStatement() {
        ReturnStatementNode returnStmt = new ReturnStatementNode();
        returnStmt.setExpression(new LiteralNode(42, "number"));

        String result = compiler.visitReturnStatement(returnStmt);

        assertTrue(result.contains("return 42"));
    }

    @Test
    void testCompileStringLiteral() {
        LiteralNode literal = new LiteralNode("Hello World", "string");

        String result = compiler.visitLiteral(literal);

        assertTrue(result.contains("\"Hello World\""));
    }

    @Test
    void testCompileNumberLiteral() {
        LiteralNode literal = new LiteralNode(42, "number");

        String result = compiler.visitLiteral(literal);

        assertEquals("42", result);
    }

    @Test
    void testCompileBooleanLiteral() {
        LiteralNode trueLiteral = new LiteralNode(true, "boolean");
        LiteralNode falseLiteral = new LiteralNode(false, "boolean");

        String resultTrue = compiler.visitLiteral(trueLiteral);
        String resultFalse = compiler.visitLiteral(falseLiteral);

        assertEquals("true", resultTrue);
        assertEquals("false", resultFalse);
    }

    @Test
    void testCompileImport() {
        ImportNode importNode = new ImportNode();
        importNode.setModulePath("std::collections");
        importNode.setItems(Arrays.asList("HashMap", "HashSet"));

        String result = compiler.visitImport(importNode);

        assertTrue(result.contains("use std::collections::{HashMap, HashSet}"));
    }

    @Test
    void testCompileWildcardImport() {
        ImportNode importNode = new ImportNode();
        importNode.setModulePath("std::io");
        importNode.setWildcard(true);

        String result = compiler.visitImport(importNode);

        assertTrue(result.contains("use std::io::*"));
    }

    @Test
    void testCompileExport() {
        ExportNode exportNode = new ExportNode();

        FunctionDeclarationNode func = new FunctionDeclarationNode();
        func.setName("add");
        exportNode.setDeclaration(func);

        String result = compiler.visitExport(exportNode);

        assertTrue(result.contains("pub"));
        assertTrue(result.contains("fn add"));
    }

    @Test
    void testCompileModule() {
        ModuleNode module = new ModuleNode();
        module.setName("MyModule");

        VariableDeclarationNode var = new VariableDeclarationNode();
        var.setName("VERSION");
        var.setInitializer(new LiteralNode(1, "number"));

        module.setStatements(Arrays.asList(var));

        String result = compiler.visitModule(module);

        assertTrue(result.contains("mod MyModule"));
    }

    @Test
    void testCompileTypeAlias() {
        TypeAliasNode typeAlias = new TypeAliasNode();
        typeAlias.setName("UserId");

        TypeNode aliasedType = new TypeNode();
        aliasedType.setName("number");
        aliasedType.setYummyAnnotation("u64");
        typeAlias.setAliasedType(aliasedType);

        String result = compiler.visitTypeAlias(typeAlias);

        assertTrue(result.contains("type UserId = u64"));
    }

    @Test
    void testCompileClosure() {
        ClosureNode closure = new ClosureNode();

        ParameterNode param = new ParameterNode();
        param.setName("x");
        closure.setParameters(Arrays.asList(param));

        BinaryExpressionNode expr = new BinaryExpressionNode(
                new IdentifierNode("x"),
                "plus",
                new LiteralNode(1, "number")
        );
        closure.setBody(Arrays.asList(expr));

        String result = compiler.visitClosure(closure);

        assertTrue(result.contains("|x|"));
        assertTrue(result.contains("x + 1"));
    }

    @Test
    void testCompileMethodWithParameters() {
        MethodDeclarationNode method = new MethodDeclarationNode();
        method.setName("set_age");

        ParameterNode param = new ParameterNode();
        param.setName("age");
        TypeNode type = new TypeNode();
        type.setName("number");
        type.setYummyAnnotation("u32");
        param.setType(type);
        method.setParameters(Arrays.asList(param));

        method.setBody(new ArrayList<>());

        String result = compiler.visitMethodDeclaration(method);

        assertTrue(result.contains("fn set_age"));
        assertTrue(result.contains("age: u32"));
    }

    @Test
    void testCompileStaticMethod() {
        MethodDeclarationNode method = new MethodDeclarationNode();
        method.setName("new");
        method.setStatic(true);

        method.setBody(new ArrayList<>());

        String result = compiler.visitMethodDeclaration(method);

        assertTrue(result.contains("fn new"));
        assertFalse(result.contains("self"));
    }

    @Test
    void testCompileAssignment() {
        AssignmentNode assignment = new AssignmentNode();
        assignment.setTarget("counter");
        assignment.setValue(new LiteralNode(10, "number"));

        String result = compiler.visitAssignment(assignment);

        assertTrue(result.contains("counter = 10"));
    }

    @Test
    void testCompileEmptyFunctionBody() {
        FunctionDeclarationNode function = new FunctionDeclarationNode();
        function.setName("empty");
        function.setBody(new ArrayList<>());

        String result = compiler.visitFunctionDeclaration(function);

        assertTrue(result.contains("fn empty()"));
    }
}
