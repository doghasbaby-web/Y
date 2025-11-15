package com.ylang.compiler;

import com.ylang.ast.*;
import org.springframework.stereotype.Component;

/**
 * Compiler from Y Language to TypeScript
 */
@Component
public class TypeScriptCompiler implements ASTVisitor<String> {

    private int indentLevel = 0;
    private static final String INDENT = "  ";

    public String compile(ProgramNode program) {
        return program.accept(this);
    }

    @Override
    public String visitProgram(ProgramNode node) {
        StringBuilder sb = new StringBuilder();

        for (ASTNode statement : node.getStatements()) {
            sb.append(statement.accept(this));
            sb.append("\n");
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
        sb.append("function ").append(node.getName()).append("(");

        // Parameters
        for (int i = 0; i < node.getParameters().size(); i++) {
            ParameterNode param = node.getParameters().get(i);
            sb.append(param.getName()).append(": ");
            sb.append(mapType(param.getType()));

            if (i < node.getParameters().size() - 1) {
                sb.append(", ");
            }
        }

        sb.append(")");

        // Return type
        if (node.getReturnType() != null) {
            sb.append(": ");
            if (node.isAsync()) {
                sb.append("Promise<");
                sb.append(mapType(node.getReturnType()));
                sb.append(">");
            } else {
                sb.append(mapType(node.getReturnType()));
            }
        }

        sb.append(" {\n");

        // Body
        indentLevel++;
        for (ASTNode stmt : node.getBody()) {
            sb.append(stmt.accept(this));
            if (!stmt.accept(this).trim().endsWith("}")) {
                sb.append(";");
            }
            sb.append("\n");
        }
        indentLevel--;

        sb.append(indent()).append("}");

        return sb.toString();
    }

    @Override
    public String visitStructDeclaration(StructDeclarationNode node) {
        StringBuilder sb = new StringBuilder();

        sb.append(indent()).append("interface ").append(node.getName()).append(" {\n");

        indentLevel++;
        for (FieldNode field : node.getFields()) {
            sb.append(indent());
            sb.append(field.getName()).append(": ");
            sb.append(mapType(field.getType()));
            sb.append(";\n");
        }
        indentLevel--;

        sb.append(indent()).append("}");

        return sb.toString();
    }

    @Override
    public String visitVariableDeclaration(VariableDeclarationNode node) {
        StringBuilder sb = new StringBuilder();

        sb.append(indent());
        if (node.isMutable()) {
            sb.append("let ");
        } else {
            sb.append("const ");
        }

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

        sb.append(indent()).append("if (");
        sb.append(node.getCondition().accept(this));
        sb.append(") {\n");

        indentLevel++;
        for (ASTNode stmt : node.getThenBlock()) {
            sb.append(stmt.accept(this));
            if (!stmt.accept(this).trim().endsWith("}")) {
                sb.append(";");
            }
            sb.append("\n");
        }
        indentLevel--;

        sb.append(indent()).append("}");

        if (!node.getElseBlock().isEmpty()) {
            sb.append(" else {\n");

            indentLevel++;
            for (ASTNode stmt : node.getElseBlock()) {
                sb.append(stmt.accept(this));
                if (!stmt.accept(this).trim().endsWith("}")) {
                    sb.append(";");
                }
                sb.append("\n");
            }
            indentLevel--;

            sb.append(indent()).append("}");
        }

        return sb.toString();
    }

    @Override
    public String visitForLoop(ForLoopNode node) {
        StringBuilder sb = new StringBuilder();

        sb.append(indent()).append("for (const ");
        sb.append(node.getVariable());
        sb.append(" of ");
        sb.append(node.getIterable().accept(this));
        sb.append(") {\n");

        indentLevel++;
        for (ASTNode stmt : node.getBody()) {
            sb.append(stmt.accept(this));
            if (!stmt.accept(this).trim().endsWith("}")) {
                sb.append(";");
            }
            sb.append("\n");
        }
        indentLevel--;

        sb.append(indent()).append("}");

        return sb.toString();
    }

    @Override
    public String visitWhileLoop(WhileLoopNode node) {
        StringBuilder sb = new StringBuilder();

        sb.append(indent()).append("while (");
        sb.append(node.getCondition().accept(this));
        sb.append(") {\n");

        indentLevel++;
        for (ASTNode stmt : node.getBody()) {
            sb.append(stmt.accept(this));
            if (!stmt.accept(this).trim().endsWith("}")) {
                sb.append(";");
            }
            sb.append("\n");
        }
        indentLevel--;

        sb.append(indent()).append("}");

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

        sb.append(node.getFunctionName()).append("(");

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
        }
        return node.getValue().toString();
    }

    @Override
    public String visitMethodDeclaration(MethodDeclarationNode node) {
        StringBuilder sb = new StringBuilder();

        sb.append(indent());
        sb.append(node.getName()).append("(");

        // Parameters
        for (int i = 0; i < node.getParameters().size(); i++) {
            ParameterNode param = node.getParameters().get(i);
            sb.append(param.getName()).append(": ");
            sb.append(mapType(param.getType()));

            if (i < node.getParameters().size() - 1) {
                sb.append(", ");
            }
        }

        sb.append(")");

        // Return type
        if (node.getReturnType() != null) {
            sb.append(": ");
            sb.append(mapType(node.getReturnType()));
        }

        sb.append(" {\n");

        // Body
        indentLevel++;
        for (ASTNode stmt : node.getBody()) {
            sb.append(stmt.accept(this));
            if (!stmt.accept(this).trim().endsWith("}")) {
                sb.append(";");
            }
            sb.append("\n");
        }
        indentLevel--;

        sb.append(indent()).append("}");

        return sb.toString();
    }

    @Override
    public String visitImplementBlock(ImplementBlockNode node) {
        StringBuilder sb = new StringBuilder();

        sb.append(indent()).append("class ").append(node.getStructName()).append(" {\n");

        indentLevel++;
        for (MethodDeclarationNode method : node.getMethods()) {
            sb.append(method.accept(this));
            sb.append("\n\n");
        }
        indentLevel--;

        sb.append(indent()).append("}");

        return sb.toString();
    }

    @Override
    public String visitTryCatch(TryCatchNode node) {
        StringBuilder sb = new StringBuilder();

        sb.append(indent()).append("try {\n");

        indentLevel++;
        for (ASTNode stmt : node.getTryBlock()) {
            sb.append(stmt.accept(this));
            if (!stmt.accept(this).trim().endsWith("}")) {
                sb.append(";");
            }
            sb.append("\n");
        }
        indentLevel--;

        sb.append(indent()).append("} catch (");
        sb.append(node.getErrorVariable());
        sb.append(") {\n");

        indentLevel++;
        for (ASTNode stmt : node.getCatchBlock()) {
            sb.append(stmt.accept(this));
            if (!stmt.accept(this).trim().endsWith("}")) {
                sb.append(";");
            }
            sb.append("\n");
        }
        indentLevel--;

        sb.append(indent()).append("}");

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

    // Helper methods

    private String mapType(TypeNode type) {
        if (type == null) {
            return "void";
        }

        String baseType = switch (type.getName().toLowerCase()) {
            case "number" -> "number";
            case "text" -> "string";
            case "truth value", "boolean" -> "boolean";
            case "nothing", "void" -> "void";
            case "list" -> "Array";
            case "map" -> "Map";
            case "set" -> "Set";
            default -> type.getName();
        };

        if (type.isOptional()) {
            baseType += " | null";
        }

        return baseType;
    }

    private String mapOperator(String operator) {
        return switch (operator.toLowerCase()) {
            case "plus" -> "+";
            case "minus" -> "-";
            case "multiplied by" -> "*";
            case "divided by" -> "/";
            case "equals", "is" -> "===";
            case "greater than" -> ">";
            case "less than" -> "<";
            case "and" -> "&&";
            case "or" -> "||";
            case "not" -> "!";
            default -> operator;
        };
    }

    private String indent() {
        return INDENT.repeat(indentLevel);
    }
}
