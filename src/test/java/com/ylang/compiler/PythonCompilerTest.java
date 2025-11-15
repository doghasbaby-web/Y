package com.ylang.compiler;

import com.ylang.ast.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for the PythonCompiler
 */
class PythonCompilerTest {

    private PythonCompiler compiler;

    @BeforeEach
    void setUp() {
        compiler = new PythonCompiler();
    }

    @Test
    void testCompileFunctionDeclaration() {
        FunctionDeclarationNode func = new FunctionDeclarationNode();
        func.setName("add");

        ParameterNode param1 = new ParameterNode();
        param1.setName("x");
        TypeNode type1 = new TypeNode();
        type1.setName("Number");
        param1.setType(type1);

        ParameterNode param2 = new ParameterNode();
        param2.setName("y");
        TypeNode type2 = new TypeNode();
        type2.setName("Number");
        param2.setType(type2);

        func.setParameters(Arrays.asList(param1, param2));

        TypeNode returnType = new TypeNode();
        returnType.setName("Number");
        func.setReturnType(returnType);

        ReturnStatementNode returnStmt = new ReturnStatementNode();
        returnStmt.setExpression(new IdentifierNode("result"));
        func.setBody(Arrays.asList(returnStmt));

        String result = compiler.compile(createProgram(func));

        assertTrue(result.contains("def add(x: int, y: int) -> int:"));
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

        assertTrue(result.contains("async def fetchData"));
    }

    @Test
    void testCompileFunctionWithEmptyBody() {
        FunctionDeclarationNode func = new FunctionDeclarationNode();
        func.setName("emptyFunc");
        func.setBody(new ArrayList<>());

        String result = compiler.compile(createProgram(func));

        assertTrue(result.contains("def emptyFunc"));
        assertTrue(result.contains("pass"));
    }

    @Test
    void testCompileStructDeclaration() {
        StructDeclarationNode struct = new StructDeclarationNode();
        struct.setName("User");

        FieldNode field1 = new FieldNode();
        field1.setName("name");
        TypeNode type1 = new TypeNode();
        type1.setName("Text");
        field1.setType(type1);

        FieldNode field2 = new FieldNode();
        field2.setName("age");
        TypeNode type2 = new TypeNode();
        type2.setName("Number");
        field2.setType(type2);

        struct.setFields(Arrays.asList(field1, field2));

        String result = compiler.compile(createProgram(struct));

        assertTrue(result.contains("class User:"));
        assertTrue(result.contains("name: str"));
        assertTrue(result.contains("age: int"));
    }

    @Test
    void testCompileVariableDeclaration() {
        VariableDeclarationNode var = new VariableDeclarationNode();
        var.setName("count");

        TypeNode type = new TypeNode();
        type.setName("Number");
        var.setType(type);
        var.setInitializer(new LiteralNode(10.0, "number"));

        String result = compiler.compile(createProgram(var));

        assertTrue(result.contains("count: int = 10"));
    }

    @Test
    void testCompileVariableWithoutTypeAnnotation() {
        VariableDeclarationNode var = new VariableDeclarationNode();
        var.setName("value");
        var.setInitializer(new LiteralNode(42.0, "number"));

        String result = compiler.compile(createProgram(var));

        assertTrue(result.contains("value = 42"));
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
        returnStmt.setExpression(new IdentifierNode("True"));
        ifStmt.setThenBlock(Arrays.asList(returnStmt));

        String result = compiler.compile(createProgram(ifStmt));

        assertTrue(result.contains("if x > 5:"));
        assertTrue(result.contains("return True"));
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

        assertTrue(result.contains("if condition:"));
        assertTrue(result.contains("else:"));
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

        assertTrue(result.contains("for item in items:"));
        assertTrue(result.contains("print(item)"));
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

        assertTrue(result.contains("while count < 10:"));
    }

    @Test
    void testCompileFunctionCall() {
        FunctionCallNode call = new FunctionCallNode();
        call.setFunctionName("print");
        call.setArguments(Arrays.asList(new LiteralNode("Hello World", "string")));

        String result = compiler.compile(createProgram(call));

        assertTrue(result.contains("print(\"Hello World\")"));
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
                {"equals", "=="},
                {"is", "=="},
                {"greater than", ">"},
                {"less than", "<"},
                {"and", "and"},
                {"or", "or"}
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

        assertTrue(result.contains("try:"));
        assertTrue(result.contains("except Exception as e:"));
        assertTrue(result.contains("riskyOperation()"));
        assertTrue(result.contains("handleError(e)"));
    }

    @Test
    void testCompileMethodDeclaration() {
        MethodDeclarationNode method = new MethodDeclarationNode();
        method.setName("getName");

        TypeNode returnType = new TypeNode();
        returnType.setName("Text");
        method.setReturnType(returnType);

        ReturnStatementNode returnStmt = new ReturnStatementNode();
        returnStmt.setExpression(new IdentifierNode("self.name"));
        method.setBody(Arrays.asList(returnStmt));

        String result = compiler.compile(createProgram(method));

        assertTrue(result.contains("def getName(self) -> str:"));
        assertTrue(result.contains("return self.name"));
    }

    @Test
    void testCompileMethodWithParameters() {
        MethodDeclarationNode method = new MethodDeclarationNode();
        method.setName("setAge");

        ParameterNode param = new ParameterNode();
        param.setName("age");
        TypeNode type = new TypeNode();
        type.setName("Number");
        param.setType(type);
        method.setParameters(Arrays.asList(param));

        method.setBody(new ArrayList<>());

        String result = compiler.compile(createProgram(method));

        assertTrue(result.contains("def setAge(self, age: int):"));
        assertTrue(result.contains("pass"));
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

        assertTrue(result.contains("class User:"));
        assertTrue(result.contains("def greet(self):"));
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

        String result = compiler.compile(createProgram(impl));

        assertTrue(result.contains("class User(Printable):"));
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

        assertTrue(result.contains("from enum import Enum"));
        assertTrue(result.contains("class Status(Enum):"));
        assertTrue(result.contains("Active = 1"));
        assertTrue(result.contains("Inactive = 2"));
    }

    @Test
    void testCompileTraitDeclaration() {
        TraitDeclarationNode trait = new TraitDeclarationNode();
        trait.setName("Printable");

        MethodSignatureNode method = new MethodSignatureNode();
        method.setName("print");

        ParameterNode param = new ParameterNode();
        param.setName("message");
        TypeNode type = new TypeNode();
        type.setName("Text");
        param.setType(type);
        method.setParameters(Arrays.asList(param));

        TypeNode returnType = new TypeNode();
        returnType.setName("Nothing");
        method.setReturnType(returnType);

        trait.setMethods(Arrays.asList(method));

        String result = compiler.compile(createProgram(trait));

        assertTrue(result.contains("from abc import ABC, abstractmethod"));
        assertTrue(result.contains("class Printable(ABC):"));
        assertTrue(result.contains("@abstractmethod"));
        assertTrue(result.contains("def print(self, message: str) -> None:"));
        assertTrue(result.contains("pass"));
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

        assertTrue(result.contains("match status:"));
        assertTrue(result.contains("case \"active\":"));
        assertTrue(result.contains("case _:"));
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

        assertTrue(result.contains("# Module: MyModule"));
        assertTrue(result.contains("version"));
    }

    @Test
    void testCompileImport() {
        ImportNode importNode = new ImportNode();
        importNode.setModulePath("utils");
        importNode.setItems(Arrays.asList("add", "subtract"));

        String result = compiler.compile(createProgram(importNode));

        assertTrue(result.contains("from utils import add, subtract"));
    }

    @Test
    void testCompileWildcardImport() {
        ImportNode importNode = new ImportNode();
        importNode.setModulePath("utils");
        importNode.setWildcard(true);

        String result = compiler.compile(createProgram(importNode));

        assertTrue(result.contains("from utils import *"));
    }

    @Test
    void testCompileImportWithAlias() {
        ImportNode importNode = new ImportNode();
        importNode.setModulePath("numpy");
        importNode.setItems(Arrays.asList());
        importNode.setAlias("np");

        String result = compiler.compile(createProgram(importNode));

        assertTrue(result.contains("as np"));
    }

    @Test
    void testCompileExport() {
        ExportNode exportNode = new ExportNode();
        exportNode.setItems(Arrays.asList("add", "subtract", "multiply"));

        String result = compiler.compile(createProgram(exportNode));

        assertTrue(result.contains("__all__ = [\"add\", \"subtract\", \"multiply\"]"));
    }

    @Test
    void testCompileTypeAlias() {
        TypeAliasNode typeAlias = new TypeAliasNode();
        typeAlias.setName("UserId");

        TypeNode aliasedType = new TypeNode();
        aliasedType.setName("Number");
        typeAlias.setAliasedType(aliasedType);

        String result = compiler.compile(createProgram(typeAlias));

        assertTrue(result.contains("UserId = int"));
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
                new LiteralNode(1.0, "number")
        );
        closure.setBody(Arrays.asList(expr));

        String result = compiler.compile(createProgram(closure));

        assertTrue(result.contains("lambda x: x + 1"));
    }

    @Test
    void testCompileClassDeclaration() {
        ClassDeclarationNode classNode = new ClassDeclarationNode();
        classNode.setName("Person");

        FieldNode field = new FieldNode();
        field.setName("name");
        TypeNode fieldType = new TypeNode();
        fieldType.setName("Text");
        field.setType(fieldType);
        classNode.setFields(Arrays.asList(field));

        MethodDeclarationNode constructor = new MethodDeclarationNode();
        ParameterNode param = new ParameterNode();
        param.setName("name");
        TypeNode paramType = new TypeNode();
        paramType.setName("Text");
        param.setType(paramType);
        constructor.setParameters(Arrays.asList(param));
        constructor.setBody(new ArrayList<>());
        classNode.setConstructor(constructor);

        MethodDeclarationNode method = new MethodDeclarationNode();
        method.setName("greet");
        method.setBody(new ArrayList<>());
        classNode.setMethods(Arrays.asList(method));

        String result = compiler.compile(createProgram(classNode));

        assertTrue(result.contains("class Person:"));
        assertTrue(result.contains("def __init__(self, name: str):"));
        assertTrue(result.contains("self.name: str"));
        assertTrue(result.contains("def greet(self):"));
    }

    @Test
    void testCompileClassWithInheritance() {
        ClassDeclarationNode classNode = new ClassDeclarationNode();
        classNode.setName("Employee");
        classNode.setExtendsClass("Person");

        String result = compiler.compile(createProgram(classNode));

        assertTrue(result.contains("class Employee(Person):"));
    }

    @Test
    void testCompileClassWithInterfaces() {
        ClassDeclarationNode classNode = new ClassDeclarationNode();
        classNode.setName("Document");
        classNode.setImplementsInterfaces(Arrays.asList("Printable", "Serializable"));

        String result = compiler.compile(createProgram(classNode));

        assertTrue(result.contains("class Document(Printable, Serializable):"));
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

        // Test boolean literals
        LiteralNode trueLiteral = new LiteralNode(true, "boolean");
        String result3 = compiler.compile(createProgram(trueLiteral));
        assertTrue(result3.contains("True"));

        LiteralNode falseLiteral = new LiteralNode(false, "boolean");
        String result4 = compiler.compile(createProgram(falseLiteral));
        assertTrue(result4.contains("False"));
    }

    @Test
    void testCompileOptionalType() {
        VariableDeclarationNode var = new VariableDeclarationNode();
        var.setName("maybeValue");

        TypeNode type = new TypeNode();
        type.setName("Text");
        type.setOptional(true);
        var.setType(type);
        var.setInitializer(new IdentifierNode("None"));

        String result = compiler.compile(createProgram(var));

        assertTrue(result.contains("maybeValue: Optional[str]"));
    }

    @Test
    void testCompileEmptyProgram() {
        ProgramNode program = new ProgramNode();

        String result = compiler.compile(program);

        assertNotNull(result);
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

        assertTrue(result.contains("x = 10"));
        assertTrue(result.contains("y = 20"));
    }

    @Test
    void testCompileReturnStatementWithoutExpression() {
        ReturnStatementNode returnStmt = new ReturnStatementNode();

        String result = compiler.compile(createProgram(returnStmt));

        assertTrue(result.contains("return"));
    }

    // Helper method to create a program with a single statement
    private ProgramNode createProgram(ASTNode statement) {
        ProgramNode program = new ProgramNode();
        program.addStatement(statement);
        return program;
    }
}
