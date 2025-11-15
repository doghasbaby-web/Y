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

        assertTrue(result.contains("for (const item of items)"));
        assertTrue(result.contains("console.log(item)"));
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

        assertTrue(result.contains("while (count < 10)"));
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

        assertTrue(result.contains("try"));
        assertTrue(result.contains("catch (e)"));
        assertTrue(result.contains("riskyOperation()"));
    }

    @Test
    void testCompileMethodDeclaration() {
        MethodDeclarationNode method = new MethodDeclarationNode();
        method.setName("getName");

        TypeNode returnType = new TypeNode();
        returnType.setName("text");
        method.setReturnType(returnType);

        ReturnStatementNode returnStmt = new ReturnStatementNode();
        returnStmt.setExpression(new IdentifierNode("this.name"));
        method.setBody(Arrays.asList(returnStmt));

        String result = compiler.visitMethodDeclaration(method);

        assertTrue(result.contains("getName()"));
        assertTrue(result.contains("string"));
        assertTrue(result.contains("return this.name"));
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
        assertTrue(result.contains("string | null"));
    }

    @Test
    void testCompileBorrowedType() {
        VariableDeclarationNode var = new VariableDeclarationNode();
        var.setName("ref");

        TypeNode type = new TypeNode();
        type.setName("text");
        type.setBorrowed(true);
        var.setType(type);

        String result = compiler.visitVariableDeclaration(var);

        assertTrue(result.contains("ref"));
        // TypeScript doesn't have borrowed types, but should still compile
        assertNotNull(result);
    }

    @Test
    void testCompileMutableType() {
        VariableDeclarationNode var = new VariableDeclarationNode();
        var.setName("data");

        TypeNode type = new TypeNode();
        type.setName("text");
        type.setMutable(true);
        var.setType(type);

        String result = compiler.visitVariableDeclaration(var);

        assertTrue(result.contains("data"));
        // TypeScript doesn't have explicit mutable types
        assertNotNull(result);
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
        trait.setMethods(Arrays.asList(method));

        String result = compiler.visitTraitDeclaration(trait);

        assertTrue(result.contains("interface Printable"));
        assertTrue(result.contains("print()"));
    }

    @Test
    void testCompileMatch() {
        MatchNode match = new MatchNode();
        match.setExpression(new IdentifierNode("status"));

        CaseNode case1 = new CaseNode();
        case1.setPattern(new LiteralNode("active", "string"));
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

        assertTrue(result.contains("switch (status)"));
        assertTrue(result.contains("case \"active\""));
        assertTrue(result.contains("default:"));
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

        MethodDeclarationNode constructor = new MethodDeclarationNode();
        ParameterNode param = new ParameterNode();
        param.setName("name");
        TypeNode paramType = new TypeNode();
        paramType.setName("text");
        param.setType(paramType);
        constructor.setParameters(Arrays.asList(param));
        constructor.setBody(new ArrayList<>());
        classNode.setConstructor(constructor);

        String result = compiler.visitClassDeclaration(classNode);

        assertTrue(result.contains("class Person"));
        assertTrue(result.contains("name: string"));
        assertTrue(result.contains("constructor(name: string)"));
    }

    @Test
    void testCompileClassWithInheritance() {
        ClassDeclarationNode classNode = new ClassDeclarationNode();
        classNode.setName("Employee");
        classNode.setExtendsClass("Person");

        String result = compiler.visitClassDeclaration(classNode);

        assertTrue(result.contains("class Employee extends Person"));
    }

    @Test
    void testCompileClassWithInterfaces() {
        ClassDeclarationNode classNode = new ClassDeclarationNode();
        classNode.setName("Document");
        classNode.setImplementsInterfaces(Arrays.asList("Printable", "Serializable"));

        String result = compiler.visitClassDeclaration(classNode);

        assertTrue(result.contains("class Document implements Printable, Serializable"));
    }

    @Test
    void testCompileImport() {
        ImportNode importNode = new ImportNode();
        importNode.setModulePath("./utils");
        importNode.setItems(Arrays.asList("add", "subtract"));

        String result = compiler.visitImport(importNode);

        assertTrue(result.contains("import { add, subtract } from \"./utils\""));
    }

    @Test
    void testCompileExport() {
        ExportNode exportNode = new ExportNode();

        FunctionDeclarationNode func = new FunctionDeclarationNode();
        func.setName("add");
        exportNode.setDeclaration(func);

        String result = compiler.visitExport(exportNode);

        assertTrue(result.contains("export"));
        assertTrue(result.contains("function add"));
    }

    @Test
    void testCompileTypeAlias() {
        TypeAliasNode typeAlias = new TypeAliasNode();
        typeAlias.setName("UserId");

        TypeNode aliasedType = new TypeNode();
        aliasedType.setName("number");
        typeAlias.setAliasedType(aliasedType);

        String result = compiler.visitTypeAlias(typeAlias);

        assertTrue(result.contains("type UserId = number"));
    }

    @Test
    void testCompileAwaitFunctionCall() {
        FunctionCallNode call = new FunctionCallNode();
        call.setFunctionName("fetchData");
        call.setAwait(true);

        String result = compiler.visitFunctionCall(call);

        assertTrue(result.contains("await fetchData()"));
    }

    @Test
    void testCompileNestedIfStatement() {
        IfStatementNode outerIf = new IfStatementNode();
        outerIf.setCondition(new IdentifierNode("x"));

        IfStatementNode innerIf = new IfStatementNode();
        innerIf.setCondition(new IdentifierNode("y"));
        innerIf.setThenBlock(Arrays.asList(new ReturnStatementNode(new LiteralNode(1, "number"))));

        outerIf.setThenBlock(Arrays.asList(innerIf));
        outerIf.setElseBlock(new ArrayList<>());

        String result = compiler.visitIfStatement(outerIf);

        assertTrue(result.contains("if (x)"));
        assertTrue(result.contains("if (y)"));
    }

    @Test
    void testCompileBinaryExpressionChain() {
        BinaryExpressionNode innerExpr = new BinaryExpressionNode(
                new IdentifierNode("b"),
                "multiplied by",
                new IdentifierNode("c")
        );

        BinaryExpressionNode outerExpr = new BinaryExpressionNode(
                new IdentifierNode("a"),
                "plus",
                innerExpr
        );

        String result = compiler.visitBinaryExpression(outerExpr);

        assertTrue(result.contains("a + b * c"));
    }

    @Test
    void testCompileEmptyFunctionBody() {
        FunctionDeclarationNode function = new FunctionDeclarationNode();
        function.setName("empty");
        function.setBody(new ArrayList<>());

        String result = compiler.visitFunctionDeclaration(function);

        assertTrue(result.contains("function empty()"));
        assertTrue(result.contains("{"));
        assertTrue(result.contains("}"));
    }

    @Test
    void testCompileConstVariable() {
        VariableDeclarationNode var = new VariableDeclarationNode();
        var.setName("PI");
        var.setMutable(false);
        var.setInitializer(new LiteralNode(3.14159, "number"));

        String result = compiler.visitVariableDeclaration(var);

        assertTrue(result.contains("const PI"));
    }

    @Test
    void testCompileMultipleParameters() {
        FunctionDeclarationNode function = new FunctionDeclarationNode();
        function.setName("calculate");

        ParameterNode param1 = new ParameterNode("a", new TypeNode("number"), null);
        ParameterNode param2 = new ParameterNode("b", new TypeNode("number"), null);
        ParameterNode param3 = new ParameterNode("c", new TypeNode("number"), null);
        function.setParameters(Arrays.asList(param1, param2, param3));

        function.setBody(new ArrayList<>());

        String result = compiler.visitFunctionDeclaration(function);

        assertTrue(result.contains("a: number, b: number, c: number"));
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
}
