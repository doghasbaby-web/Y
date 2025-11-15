package com.ylang.compiler;

import com.ylang.ast.*;
import org.springframework.stereotype.Component;

/**
 * Compiler from Y Language to Python
 * Note: This class is NOT a singleton. Create a new instance for each compilation
 * to ensure thread safety.
 */
public class PythonCompiler implements ASTVisitor<String> {

    private int indentLevel = 0;
    private static final String INDENT = "    ";

    public String compile(ProgramNode program) {
        return program.accept(this);
    }

    @Override
    public String visitProgram(ProgramNode node) {
        StringBuilder sb = new StringBuilder();

        for (ASTNode statement : node.getStatements()) {
            sb.append(statement.accept(this));
            sb.append("\n\n");
        }

        return sb.toString();
    }

    @Override
    public String visitFunctionDeclaration(FunctionDeclarationNode node) {
        StringBuilder sb = new StringBuilder();

        sb.append(indent());
        if (node.isAsync()) {
            sb.append("async ");
        }
        sb.append("def ").append(node.getName()).append("(");

        // Parameters
        for (int i = 0; i < node.getParameters().size(); i++) {
            ParameterNode param = node.getParameters().get(i);
            sb.append(param.getName());
            if (param.getType() != null) {
                sb.append(": ").append(mapType(param.getType()));
            }

            if (i < node.getParameters().size() - 1) {
                sb.append(", ");
            }
        }

        sb.append(")");

        // Return type hint
        if (node.getReturnType() != null) {
            sb.append(" -> ");
            sb.append(mapType(node.getReturnType()));
        }

        sb.append(":\n");

        // Body
        indentLevel++;
        if (node.getBody().isEmpty()) {
            sb.append(indent()).append("pass\n");
        } else {
            for (ASTNode stmt : node.getBody()) {
                sb.append(stmt.accept(this));
                sb.append("\n");
            }
        }
        indentLevel--;

        return sb.toString();
    }

    @Override
    public String visitStructDeclaration(StructDeclarationNode node) {
        StringBuilder sb = new StringBuilder();

        sb.append(indent()).append("class ").append(node.getName()).append(":\n");

        indentLevel++;
        for (FieldNode field : node.getFields()) {
            sb.append(indent());
            sb.append(field.getName()).append(": ");
            sb.append(mapType(field.getType()));
            sb.append("\n");
        }
        indentLevel--;

        return sb.toString();
    }

    @Override
    public String visitVariableDeclaration(VariableDeclarationNode node) {
        StringBuilder sb = new StringBuilder();

        sb.append(indent());
        sb.append(node.getName());

        if (node.getType() != null) {
            sb.append(": ").append(mapType(node.getType()));
        }

        if (node.getInitializer() != null) {
            sb.append(" = ");
            sb.append(node.getInitializer().accept(this));
        }

        return sb.toString();
    }

    @Override
    public String visitIfStatement(IfStatementNode node) {
        StringBuilder sb = new StringBuilder();

        sb.append(indent()).append("if ");
        sb.append(node.getCondition().accept(this));
        sb.append(":\n");

        indentLevel++;
        for (ASTNode stmt : node.getThenBlock()) {
            sb.append(stmt.accept(this));
            sb.append("\n");
        }
        indentLevel--;

        if (!node.getElseBlock().isEmpty()) {
            sb.append(indent()).append("else:\n");

            indentLevel++;
            for (ASTNode stmt : node.getElseBlock()) {
                sb.append(stmt.accept(this));
                sb.append("\n");
            }
            indentLevel--;
        }

        return sb.toString();
    }

    @Override
    public String visitForLoop(ForLoopNode node) {
        StringBuilder sb = new StringBuilder();

        sb.append(indent()).append("for ");
        sb.append(node.getVariable());
        sb.append(" in ");
        sb.append(node.getIterable().accept(this));
        sb.append(":\n");

        indentLevel++;
        for (ASTNode stmt : node.getBody()) {
            sb.append(stmt.accept(this));
            sb.append("\n");
        }
        indentLevel--;

        return sb.toString();
    }

    @Override
    public String visitWhileLoop(WhileLoopNode node) {
        StringBuilder sb = new StringBuilder();

        sb.append(indent()).append("while ");
        sb.append(node.getCondition().accept(this));
        sb.append(":\n");

        indentLevel++;
        for (ASTNode stmt : node.getBody()) {
            sb.append(stmt.accept(this));
            sb.append("\n");
        }
        indentLevel--;

        return sb.toString();
    }

    @Override
    public String visitReturnStatement(ReturnStatementNode node) {
        StringBuilder sb = new StringBuilder();

        sb.append(indent()).append("return");

        if (node.getExpression() != null) {
            sb.append(" ");
            sb.append(node.getExpression().accept(this));
        }

        return sb.toString();
    }

    @Override
    public String visitBinaryExpression(BinaryExpressionNode node) {
        StringBuilder sb = new StringBuilder();

        sb.append(node.getLeft().accept(this));
        sb.append(" ");
        sb.append(mapOperator(node.getOperator()));
        sb.append(" ");
        sb.append(node.getRight().accept(this));

        return sb.toString();
    }

    @Override
    public String visitFunctionCall(FunctionCallNode node) {
        StringBuilder sb = new StringBuilder();

        if (node.isAwait()) {
            sb.append("await ");
        }

        // Handle print function
        if (node.getFunctionName().equalsIgnoreCase("print")) {
            sb.append("print");
        } else {
            sb.append(node.getFunctionName());
        }

        sb.append("(");

        for (int i = 0; i < node.getArguments().size(); i++) {
            sb.append(node.getArguments().get(i).accept(this));
            if (i < node.getArguments().size() - 1) {
                sb.append(", ");
            }
        }

        sb.append(")");

        return sb.toString();
    }

    @Override
    public String visitIdentifier(IdentifierNode node) {
        return node.getName();
    }

    @Override
    public String visitLiteral(LiteralNode node) {
        if (node.getType().equals("string")) {
            return "\"" + node.getValue() + "\"";
        } else if (node.getType().equals("boolean")) {
            return node.getValue().toString().toLowerCase().equals("true") ? "True" : "False";
        }
        return node.getValue().toString();
    }

    @Override
    public String visitMethodDeclaration(MethodDeclarationNode node) {
        StringBuilder sb = new StringBuilder();

        sb.append(indent());
        sb.append("def ").append(node.getName()).append("(self");

        if (!node.getParameters().isEmpty()) {
            sb.append(", ");
        }

        // Parameters
        for (int i = 0; i < node.getParameters().size(); i++) {
            ParameterNode param = node.getParameters().get(i);
            sb.append(param.getName());
            if (param.getType() != null) {
                sb.append(": ").append(mapType(param.getType()));
            }

            if (i < node.getParameters().size() - 1) {
                sb.append(", ");
            }
        }

        sb.append(")");

        if (node.getReturnType() != null) {
            sb.append(" -> ").append(mapType(node.getReturnType()));
        }

        sb.append(":\n");

        // Body
        indentLevel++;
        if (node.getBody().isEmpty()) {
            sb.append(indent()).append("pass\n");
        } else {
            for (ASTNode stmt : node.getBody()) {
                sb.append(stmt.accept(this));
                sb.append("\n");
            }
        }
        indentLevel--;

        return sb.toString();
    }

    @Override
    public String visitImplementBlock(ImplementBlockNode node) {
        StringBuilder sb = new StringBuilder();

        sb.append(indent()).append("class ").append(node.getStructName());

        if (node.getTraitName() != null) {
            sb.append("(").append(node.getTraitName()).append(")");
        }

        sb.append(":\n");

        indentLevel++;
        for (MethodDeclarationNode method : node.getMethods()) {
            sb.append(method.accept(this));
            sb.append("\n");
        }
        indentLevel--;

        return sb.toString();
    }

    @Override
    public String visitTryCatch(TryCatchNode node) {
        StringBuilder sb = new StringBuilder();

        sb.append(indent()).append("try:\n");

        indentLevel++;
        for (ASTNode stmt : node.getTryBlock()) {
            sb.append(stmt.accept(this));
            sb.append("\n");
        }
        indentLevel--;

        sb.append(indent()).append("except Exception as ");
        sb.append(node.getErrorVariable());
        sb.append(":\n");

        indentLevel++;
        for (ASTNode stmt : node.getCatchBlock()) {
            sb.append(stmt.accept(this));
            sb.append("\n");
        }
        indentLevel--;

        return sb.toString();
    }

    @Override
    public String visitAssignment(AssignmentNode node) {
        StringBuilder sb = new StringBuilder();

        sb.append(indent());
        sb.append(node.getTarget());
        sb.append(" = ");
        sb.append(node.getValue().accept(this));

        return sb.toString();
    }

    @Override
    public String visitEnumDeclaration(EnumDeclarationNode node) {
        StringBuilder sb = new StringBuilder();

        sb.append(indent()).append("from enum import Enum\n\n");
        sb.append(indent()).append("class ").append(node.getName()).append("(Enum):\n");

        indentLevel++;
        for (int i = 0; i < node.getVariants().size(); i++) {
            EnumVariantNode variant = node.getVariants().get(i);
            sb.append(indent()).append(variant.getName()).append(" = ").append(i + 1);
            sb.append("\n");
        }
        indentLevel--;

        return sb.toString();
    }

    @Override
    public String visitTraitDeclaration(TraitDeclarationNode node) {
        StringBuilder sb = new StringBuilder();

        sb.append(indent()).append("from abc import ABC, abstractmethod\n\n");
        sb.append(indent()).append("class ").append(node.getName()).append("(ABC):\n");

        indentLevel++;
        for (MethodSignatureNode method : node.getMethods()) {
            sb.append(indent()).append("@abstractmethod\n");
            sb.append(indent()).append("def ").append(method.getName()).append("(self");
            if (!method.getParameters().isEmpty()) {
                sb.append(", ");
                for (int i = 0; i < method.getParameters().size(); i++) {
                    ParameterNode param = method.getParameters().get(i);
                    sb.append(param.getName());
                    if (param.getType() != null) {
                        sb.append(": ").append(mapType(param.getType()));
                    }
                    if (i < method.getParameters().size() - 1) {
                        sb.append(", ");
                    }
                }
            }
            sb.append(")");
            if (method.getReturnType() != null) {
                sb.append(" -> ").append(mapType(method.getReturnType()));
            }
            sb.append(":\n");
            indentLevel++;
            sb.append(indent()).append("pass\n");
            indentLevel--;
        }
        indentLevel--;

        return sb.toString();
    }

    @Override
    public String visitMatch(MatchNode node) {
        StringBuilder sb = new StringBuilder();

        // Python 3.10+ match statement
        sb.append(indent()).append("match ");
        sb.append(node.getExpression().accept(this));
        sb.append(":\n");

        indentLevel++;
        for (CaseNode caseNode : node.getCases()) {
            if (caseNode.isDefault()) {
                sb.append(indent()).append("case _:\n");
            } else {
                sb.append(indent()).append("case ");
                sb.append(caseNode.getPattern().accept(this));
                sb.append(":\n");
            }

            indentLevel++;
            for (ASTNode stmt : caseNode.getBody()) {
                sb.append(stmt.accept(this));
                sb.append("\n");
            }
            indentLevel--;
        }
        indentLevel--;

        return sb.toString();
    }

    @Override
    public String visitModule(ModuleNode node) {
        StringBuilder sb = new StringBuilder();

        sb.append("# Module: ").append(node.getName()).append("\n\n");

        for (ASTNode stmt : node.getStatements()) {
            sb.append(stmt.accept(this));
            sb.append("\n");
        }

        return sb.toString();
    }

    @Override
    public String visitImport(ImportNode node) {
        StringBuilder sb = new StringBuilder();

        sb.append(indent()).append("from ");
        sb.append(node.getModulePath().replace("::", "."));

        if (node.isWildcard()) {
            sb.append(" import *");
        } else if (!node.getItems().isEmpty()) {
            sb.append(" import ");
            for (int i = 0; i < node.getItems().size(); i++) {
                sb.append(node.getItems().get(i));
                if (i < node.getItems().size() - 1) {
                    sb.append(", ");
                }
            }
        } else {
            sb.append(" import ").append(node.getModulePath().substring(node.getModulePath().lastIndexOf("::") + 2));
        }

        if (node.getAlias() != null) {
            sb.append(" as ").append(node.getAlias());
        }

        return sb.toString();
    }

    @Override
    public String visitExport(ExportNode node) {
        // Python doesn't have explicit export, but we can add to __all__
        StringBuilder sb = new StringBuilder();

        sb.append(indent()).append("__all__ = [");

        for (int i = 0; i < node.getItems().size(); i++) {
            sb.append("\"").append(node.getItems().get(i)).append("\"");
            if (i < node.getItems().size() - 1) {
                sb.append(", ");
            }
        }

        sb.append("]");

        return sb.toString();
    }

    @Override
    public String visitTypeAlias(TypeAliasNode node) {
        StringBuilder sb = new StringBuilder();

        sb.append(indent()).append(node.getName());
        sb.append(" = ").append(mapType(node.getAliasedType()));

        return sb.toString();
    }

    @Override
    public String visitClosure(ClosureNode node) {
        StringBuilder sb = new StringBuilder();

        sb.append("lambda ");
        for (int i = 0; i < node.getParameters().size(); i++) {
            ParameterNode param = node.getParameters().get(i);
            sb.append(param.getName());
            if (i < node.getParameters().size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(": ");

        // For single expression
        if (node.getBody().size() == 1) {
            sb.append(node.getBody().get(0).accept(this));
        }

        return sb.toString();
    }

    @Override
    public String visitClassDeclaration(ClassDeclarationNode node) {
        StringBuilder sb = new StringBuilder();

        sb.append(indent()).append("class ").append(node.getName());

        if (node.getExtendsClass() != null || !node.getImplementsInterfaces().isEmpty()) {
            sb.append("(");
            if (node.getExtendsClass() != null) {
                sb.append(node.getExtendsClass());
                if (!node.getImplementsInterfaces().isEmpty()) {
                    sb.append(", ");
                }
            }
            for (int i = 0; i < node.getImplementsInterfaces().size(); i++) {
                sb.append(node.getImplementsInterfaces().get(i));
                if (i < node.getImplementsInterfaces().size() - 1) {
                    sb.append(", ");
                }
            }
            sb.append(")");
        }

        sb.append(":\n");

        indentLevel++;

        // Constructor
        if (node.getConstructor() != null) {
            MethodDeclarationNode constructor = node.getConstructor();
            sb.append(indent()).append("def __init__(self");
            if (!constructor.getParameters().isEmpty()) {
                sb.append(", ");
            }
            for (int i = 0; i < constructor.getParameters().size(); i++) {
                ParameterNode param = constructor.getParameters().get(i);
                sb.append(param.getName());
                if (param.getType() != null) {
                    sb.append(": ").append(mapType(param.getType()));
                }
                if (i < constructor.getParameters().size() - 1) {
                    sb.append(", ");
                }
            }
            sb.append("):\n");
            indentLevel++;

            // Initialize fields
            for (FieldNode field : node.getFields()) {
                sb.append(indent()).append("self.").append(field.getName());
                sb.append(": ").append(mapType(field.getType())).append("\n");
            }

            for (ASTNode stmt : constructor.getBody()) {
                sb.append(stmt.accept(this));
                sb.append("\n");
            }
            indentLevel--;
            sb.append("\n");
        }

        // Methods
        for (MethodDeclarationNode method : node.getMethods()) {
            sb.append(method.accept(this)).append("\n");
        }

        indentLevel--;

        return sb.toString();
    }

    // Helper methods

    private String mapType(TypeNode type) {
        if (type == null) {
            return "None";
        }

        String baseType = switch (type.getName().toLowerCase()) {
            case "number" -> "int";
            case "text" -> "str";
            case "truth value", "boolean" -> "bool";
            case "nothing", "void" -> "None";
            case "list" -> "List";
            case "map" -> "Dict";
            case "set" -> "Set";
            default -> type.getName();
        };

        if (type.isOptional()) {
            baseType = "Optional[" + baseType + "]";
        }

        return baseType;
    }

    private String mapOperator(String operator) {
        return switch (operator.toLowerCase()) {
            case "plus" -> "+";
            case "minus" -> "-";
            case "multiplied by" -> "*";
            case "divided by" -> "/";
            case "equals", "is" -> "==";
            case "greater than" -> ">";
            case "less than" -> "<";
            case "and" -> "and";
            case "or" -> "or";
            case "not" -> "not";
            default -> operator;
        };
    }

    private String indent() {
        return INDENT.repeat(indentLevel);
    }
}
