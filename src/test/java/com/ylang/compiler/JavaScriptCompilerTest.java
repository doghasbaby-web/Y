package com.ylang.compiler;

import com.ylang.ast.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for the JavaScriptCompiler
 */
class JavaScriptCompilerTest {

    private JavaScriptCompiler compiler;

    @BeforeEach
    void setUp() {
        compiler = new JavaScriptCompiler();
    }

    @Test
    void testCompileFunctionDeclaration() {
        FunctionDeclarationNode func = new FunctionDeclarationNode();
        func.setName("add");

        ParameterNode param1 = new ParameterNode();
        param1.setName("x");
        ParameterNode param2 = new ParameterNode();
        param2.setName("y");
        func.setParameters(Arrays.asList(param1, param2));

        ReturnStatementNode returnStmt = new ReturnStatementNode();
        returnStmt.setExpression(new IdentifierNode("result"));
        func.setBody(Arrays.asList(returnStmt));

        String result = compiler.compile(createProgram(func));

        assertTrue(result.contains("function add(x, y)"));
        assertTrue(result.contains("return result"));
    }

    @Test
    void testCompileAsyncFunction() {
        FunctionDeclarationNode func = new FunctionDeclarationNode();
        func.setName("fetchData");
        func.setAsync(true);

        ReturnStatementNode returnStmt = new ReturnStatementNode();
        returnStmt.setExpression(new IdentifierNode("data"));
        func.setBody(Arrays.asList(returnStmt));

        String result = compiler.compile(createProgram(func));

        assertTrue(result.contains("async function fetchData"));
    }

    @Test
    void testCompileStructDeclaration() {
        StructDeclarationNode struct = new StructDeclarationNode();
        struct.setName("User");

        FieldNode field1 = new FieldNode();
        field1.setName("name");
        TypeNode type1 = new TypeNode();
        type1.setName("String");
        field1.setType(type1);

        FieldNode field2 = new FieldNode();
        field2.setName("age");
        TypeNode type2 = new TypeNode();
        type2.setName("Number");
        field2.setType(type2);

        struct.setFields(Arrays.asList(field1, field2));

        String result = compiler.compile(createProgram(struct));

        assertTrue(result.contains("@typedef {Object} User"));
        assertTrue(result.contains("@property {string} name"));
        assertTrue(result.contains("@property {number} age"));
    }

    @Test
    void testCompileVariableDeclaration() {
        VariableDeclarationNode var = new VariableDeclarationNode();
        var.setName("count");
        var.setMutable(false);
        var.setInitializer(new LiteralNode(10.0, "number"));

        String result = compiler.compile(createProgram(var));

        assertTrue(result.contains("const count = 10"));
    }

    @Test
    void testCompileMutableVariable() {
        VariableDeclarationNode var = new VariableDeclarationNode();
        var.setName("counter");
        var.setMutable(true);
        var.setInitializer(new LiteralNode(0.0, "number"));

        String result = compiler.compile(createProgram(var));

        assertTrue(result.contains("let counter = 0"));
    }

    @Test
    void testCompileIfStatement() {
        IfStatementNode ifStmt = new IfStatementNode();
        BinaryExpressionNode condition = new BinaryExpressionNode(
                new IdentifierNode("x"),
                "greater than",
                new LiteralNode(5.0, "number")
        );
        ifStmt.setCondition(condition);

        ReturnStatementNode returnStmt = new ReturnStatementNode();
        returnStmt.setExpression(new IdentifierNode("true"));
        ifStmt.setThenBlock(Arrays.asList(returnStmt));

        String result = compiler.compile(createProgram(ifStmt));

        assertTrue(result.contains("if (x > 5)"));
        assertTrue(result.contains("return true"));
    }

    @Test
    void testCompileIfElseStatement() {
        IfStatementNode ifStmt = new IfStatementNode();
        ifStmt.setCondition(new IdentifierNode("condition"));

        ReturnStatementNode thenStmt = new ReturnStatementNode();
        thenStmt.setExpression(new LiteralNode(1.0, "number"));
        ifStmt.setThenBlock(Arrays.asList(thenStmt));

        ReturnStatementNode elseStmt = new ReturnStatementNode();
        elseStmt.setExpression(new LiteralNode(0.0, "number"));
        ifStmt.setElseBlock(Arrays.asList(elseStmt));

        String result = compiler.compile(createProgram(ifStmt));

        assertTrue(result.contains("if (condition)"));
        assertTrue(result.contains("else"));
        assertTrue(result.contains("return 0"));
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

        String result = compiler.compile(createProgram(loop));

        assertTrue(result.contains("for (const item of items)"));
        assertTrue(result.contains("console.log(item)"));
    }

    @Test
    void testCompileWhileLoop() {
        WhileLoopNode loop = new WhileLoopNode();
        BinaryExpressionNode condition = new BinaryExpressionNode(
                new IdentifierNode("count"),
                "less than",
                new LiteralNode(10.0, "number")
        );
        loop.setCondition(condition);

        BinaryExpressionNode increment = new BinaryExpressionNode(
                new IdentifierNode("count"),
                "plus",
                new LiteralNode(1.0, "number")
        );
        loop.setBody(Arrays.asList(increment));

        String result = compiler.compile(createProgram(loop));

        assertTrue(result.contains("while (count < 10)"));
    }

    @Test
    void testCompileFunctionCall() {
        FunctionCallNode call = new FunctionCallNode();
        call.setFunctionName("print");
        call.setArguments(Arrays.asList(new LiteralNode("Hello World", "string")));

        String result = compiler.compile(createProgram(call));

        assertTrue(result.contains("console.log(\"Hello World\")"));
    }

    @Test
    void testCompileAwaitFunctionCall() {
        FunctionCallNode call = new FunctionCallNode();
        call.setFunctionName("fetchData");
        call.setAwait(true);

        String result = compiler.compile(createProgram(call));

        assertTrue(result.contains("await fetchData()"));
    }

    @Test
    void testCompileBinaryExpression() {
        BinaryExpressionNode expr = new BinaryExpressionNode(
                new LiteralNode(5.0, "number"),
                "plus",
                new LiteralNode(3.0, "number")
        );

        String result = compiler.compile(createProgram(expr));

        assertTrue(result.contains("5 + 3"));
    }

    @Test
    void testCompileBinaryOperators() {
        // Test various operators
        String[][] operators = {
                {"plus", "+"},
                {"minus", "-"},
                {"multiplied by", "*"},
                {"divided by", "/"},
                {"equals", "==="},
                {"is", "==="},
                {"greater than", ">"},
                {"less than", "<"},
                {"and", "&&"},
                {"or", "||"}
        };

        for (String[] op : operators) {
            BinaryExpressionNode expr = new BinaryExpressionNode(
                    new IdentifierNode("a"),
                    op[0],
                    new IdentifierNode("b")
            );
            String result = compiler.compile(createProgram(expr));
            assertTrue(result.contains("a " + op[1] + " b"),
                    "Failed for operator: " + op[0]);
        }
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

        String result = compiler.compile(createProgram(tryCatch));

        assertTrue(result.contains("try {"));
        assertTrue(result.contains("catch (e)"));
        assertTrue(result.contains("riskyOperation()"));
        assertTrue(result.contains("handleError(e)"));
    }

    @Test
    void testCompileMethodDeclaration() {
        MethodDeclarationNode method = new MethodDeclarationNode();
        method.setName("getName");

        ReturnStatementNode returnStmt = new ReturnStatementNode();
        returnStmt.setExpression(new IdentifierNode("this.name"));
        method.setBody(Arrays.asList(returnStmt));

        String result = compiler.compile(createProgram(method));

        assertTrue(result.contains("getName()"));
        assertTrue(result.contains("return this.name"));
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

        String result = compiler.compile(createProgram(impl));

        assertTrue(result.contains("class User"));
        assertTrue(result.contains("greet()"));
    }

    @Test
    void testCompileAssignment() {
        AssignmentNode assignment = new AssignmentNode();
        assignment.setTarget("counter");
        assignment.setValue(new LiteralNode(10.0, "number"));

        String result = compiler.compile(createProgram(assignment));

        assertTrue(result.contains("counter = 10"));
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

        String result = compiler.compile(createProgram(enumNode));

        assertTrue(result.contains("const Status = Object.freeze({"));
        assertTrue(result.contains("Active: \"Active\""));
        assertTrue(result.contains("Inactive: \"Inactive\""));
    }

    @Test
    void testCompileTraitDeclaration() {
        TraitDeclarationNode trait = new TraitDeclarationNode();
        trait.setName("Printable");

        MethodSignatureNode method = new MethodSignatureNode();
        method.setName("print");
        trait.setMethods(Arrays.asList(method));

        String result = compiler.compile(createProgram(trait));

        assertTrue(result.contains("@interface Printable"));
        assertTrue(result.contains("@method print()"));
    }

    @Test
    void testCompileMatch() {
        MatchNode match = new MatchNode();
        match.setExpression(new IdentifierNode("status"));

        CaseNode case1 = new CaseNode();
        case1.setPattern(new LiteralNode("active", "string"));
        ReturnStatementNode ret1 = new ReturnStatementNode();
        ret1.setExpression(new LiteralNode(1.0, "number"));
        case1.setBody(Arrays.asList(ret1));

        CaseNode defaultCase = new CaseNode();
        defaultCase.setDefault(true);
        ReturnStatementNode ret2 = new ReturnStatementNode();
        ret2.setExpression(new LiteralNode(0.0, "number"));
        defaultCase.setBody(Arrays.asList(ret2));

        match.setCases(Arrays.asList(case1, defaultCase));

        String result = compiler.compile(createProgram(match));

        assertTrue(result.contains("switch (status)"));
        assertTrue(result.contains("case \"active\""));
        assertTrue(result.contains("default:"));
        assertTrue(result.contains("break;"));
    }

    @Test
    void testCompileModule() {
        ModuleNode module = new ModuleNode();
        module.setName("MyModule");

        VariableDeclarationNode var = new VariableDeclarationNode();
        var.setName("version");
        var.setInitializer(new LiteralNode(1.0, "number"));

        module.setStatements(Arrays.asList(var));

        String result = compiler.compile(createProgram(module));

        assertTrue(result.contains("// Module: MyModule"));
        assertTrue(result.contains("version"));
    }

    @Test
    void testCompileImport() {
        ImportNode importNode = new ImportNode();
        importNode.setModulePath("./utils");
        importNode.setItems(Arrays.asList("add", "subtract"));

        String result = compiler.compile(createProgram(importNode));

        assertTrue(result.contains("import { add, subtract } from \"./utils\""));
    }

    @Test
    void testCompileWildcardImport() {
        ImportNode importNode = new ImportNode();
        importNode.setModulePath("./utils");
        importNode.setWildcard(true);
        importNode.setAlias("Utils");

        String result = compiler.compile(createProgram(importNode));

        assertTrue(result.contains("import * as Utils from \"./utils\""));
    }

    @Test
    void testCompileExport() {
        ExportNode exportNode = new ExportNode();

        FunctionDeclarationNode func = new FunctionDeclarationNode();
        func.setName("add");
        exportNode.setDeclaration(func);

        String result = compiler.compile(createProgram(exportNode));

        assertTrue(result.contains("export"));
        assertTrue(result.contains("function add"));
    }

    @Test
    void testCompileDefaultExport() {
        ExportNode exportNode = new ExportNode();
        exportNode.setDefault(true);

        FunctionDeclarationNode func = new FunctionDeclarationNode();
        func.setName("main");
        exportNode.setDeclaration(func);

        String result = compiler.compile(createProgram(exportNode));

        assertTrue(result.contains("export default"));
    }

    @Test
    void testCompileTypeAlias() {
        TypeAliasNode typeAlias = new TypeAliasNode();
        typeAlias.setName("UserId");

        TypeNode aliasedType = new TypeNode();
        aliasedType.setName("Number");
        typeAlias.setAliasedType(aliasedType);

        String result = compiler.compile(createProgram(typeAlias));

        assertTrue(result.contains("@typedef {number} UserId"));
    }

    @Test
    void testCompileClosure() {
        ClosureNode closure = new ClosureNode();

        ParameterNode param = new ParameterNode();
        param.setName("x");
        closure.setParameters(Arrays.asList(param));

        ReturnStatementNode returnStmt = new ReturnStatementNode();
        returnStmt.setExpression(new IdentifierNode("x"));
        closure.setBody(Arrays.asList(returnStmt));

        String result = compiler.compile(createProgram(closure));

        assertTrue(result.contains("(x) => {"));
        assertTrue(result.contains("return x"));
    }

    @Test
    void testCompileClassDeclaration() {
        ClassDeclarationNode classNode = new ClassDeclarationNode();
        classNode.setName("Person");

        FieldNode field = new FieldNode();
        field.setName("name");
        classNode.setFields(Arrays.asList(field));

        MethodDeclarationNode constructor = new MethodDeclarationNode();
        ParameterNode param = new ParameterNode();
        param.setName("name");
        constructor.setParameters(Arrays.asList(param));
        constructor.setBody(new ArrayList<>());
        classNode.setConstructor(constructor);

        MethodDeclarationNode method = new MethodDeclarationNode();
        method.setName("greet");
        method.setBody(new ArrayList<>());
        classNode.setMethods(Arrays.asList(method));

        String result = compiler.compile(createProgram(classNode));

        assertTrue(result.contains("class Person"));
        assertTrue(result.contains("constructor(name)"));
        assertTrue(result.contains("this.name"));
        assertTrue(result.contains("greet()"));
    }

    @Test
    void testCompileClassWithInheritance() {
        ClassDeclarationNode classNode = new ClassDeclarationNode();
        classNode.setName("Employee");
        classNode.setExtendsClass("Person");

        String result = compiler.compile(createProgram(classNode));

        assertTrue(result.contains("class Employee extends Person"));
    }

    @Test
    void testCompileLiterals() {
        // Test number literal
        LiteralNode numLiteral = new LiteralNode(42.0, "number");
        String result1 = compiler.compile(createProgram(numLiteral));
        assertTrue(result1.contains("42"));

        // Test string literal
        LiteralNode strLiteral = new LiteralNode("test", "string");
        String result2 = compiler.compile(createProgram(strLiteral));
        assertTrue(result2.contains("\"test\""));

        // Test boolean literal
        LiteralNode boolLiteral = new LiteralNode(true, "boolean");
        String result3 = compiler.compile(createProgram(boolLiteral));
        assertTrue(result3.contains("true"));
    }

    @Test
    void testCompileOptionalType() {
        VariableDeclarationNode var = new VariableDeclarationNode();
        var.setName("maybeValue");

        TypeNode type = new TypeNode();
        type.setName("String");
        type.setOptional(true);
        var.setType(type);

        // Although JavaScript doesn't have types, this tests the type mapping
        String result = compiler.compile(createProgram(var));
        assertNotNull(result);
    }

    @Test
    void testCompileEmptyProgram() {
        ProgramNode program = new ProgramNode();

        String result = compiler.compile(program);

        assertNotNull(result);
        assertTrue(result.trim().isEmpty());
    }

    @Test
    void testCompileMultipleStatements() {
        VariableDeclarationNode var1 = new VariableDeclarationNode();
        var1.setName("x");
        var1.setInitializer(new LiteralNode(10.0, "number"));

        VariableDeclarationNode var2 = new VariableDeclarationNode();
        var2.setName("y");
        var2.setInitializer(new LiteralNode(20.0, "number"));

        ProgramNode program = new ProgramNode();
        program.addStatement(var1);
        program.addStatement(var2);

        String result = compiler.compile(program);

        assertTrue(result.contains("x"));
        assertTrue(result.contains("y"));
    }

    // Helper method to create a program with a single statement
    private ProgramNode createProgram(ASTNode statement) {
        ProgramNode program = new ProgramNode();
        program.addStatement(statement);
        return program;
    }
}
