package com.ylang.compiler;

import com.ylang.ast.*;
import org.springframework.stereotype.Component;

/**
 * Compiler from Y Language to Rust
 */
@Component
public class RustCompiler implements ASTVisitor<String> {

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
        sb.append("fn ").append(node.getName()).append("(");

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
        if (node.getReturnType() != null && !node.getReturnType().getName().equals("nothing")) {
            sb.append(" -> ");
            if (node.isAsync()) {
                sb.append("impl Future<Output = ");
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

        // Add derives if present in yummy annotation
        if (node.getYummyAnnotation() != null && node.getYummyAnnotation().contains("derive")) {
            sb.append(indent()).append("#[derive(Debug, Clone)]\n");
        }

        sb.append(indent()).append("struct ").append(node.getName()).append(" {\n");

        indentLevel++;
        for (FieldNode field : node.getFields()) {
            sb.append(indent());
            sb.append(field.getName()).append(": ");
            sb.append(mapType(field.getType()));
            sb.append(",\n");
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
            sb.append("let mut ");
        } else {
            sb.append("let ");
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

        sb.append(indent()).append("if ");
        sb.append(node.getCondition().accept(this));
        sb.append(" {\n");

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

        sb.append(indent()).append("for ");
        sb.append(node.getVariable());
        sb.append(" in ");
        sb.append(node.getIterable().accept(this));
        sb.append(" {\n");

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

        sb.append(indent()).append("while ");
        sb.append(node.getCondition().accept(this));
        sb.append(" {\n");

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

        sb.append(indent());

        if (node.getExpression() != null) {
            // In Rust, the last expression without semicolon is returned
            // But we'll use explicit return for clarity
            sb.append("return ");
            sb.append(node.getExpression().accept(this));
        } else {
            sb.append("return");
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

        // Handle print as println! macro
        if (node.getFunctionName().equalsIgnoreCase("print")) {
            sb.append("println!(");
            for (int i = 0; i < node.getArguments().size(); i++) {
                sb.append("\"{}\"");
                if (i < node.getArguments().size() - 1) {
                    sb.append(", ");
                }
            }
            if (!node.getArguments().isEmpty()) {
                sb.append(", ");
                for (int i = 0; i < node.getArguments().size(); i++) {
                    sb.append(node.getArguments().get(i).accept(this));
                    if (i < node.getArguments().size() - 1) {
                        sb.append(", ");
                    }
                }
            }
            sb.append(")");
        } else {
            if (node.isAwait()) {
                sb.append(node.getFunctionName()).append("(");

                for (int i = 0; i < node.getArguments().size(); i++) {
                    sb.append(node.getArguments().get(i).accept(this));
                    if (i < node.getArguments().size() - 1) {
                        sb.append(", ");
                    }
                }

                sb.append(").await");
            } else {
                sb.append(node.getFunctionName()).append("(");

                for (int i = 0; i < node.getArguments().size(); i++) {
                    sb.append(node.getArguments().get(i).accept(this));
                    if (i < node.getArguments().size() - 1) {
                        sb.append(", ");
                    }
                }

                sb.append(")");
            }
        }

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
        sb.append("fn ").append(node.getName()).append("(");

        // Add self parameter
        if (!node.isStatic()) {
            if (node.getYummyAnnotation() != null && node.getYummyAnnotation().contains("&mut self")) {
                sb.append("&mut self");
            } else if (node.getYummyAnnotation() != null && node.getYummyAnnotation().contains("&self")) {
                sb.append("&self");
            } else {
                sb.append("&self");
            }

            if (!node.getParameters().isEmpty()) {
                sb.append(", ");
            }
        }

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
        if (node.getReturnType() != null && !node.getReturnType().getName().equals("nothing")) {
            sb.append(" -> ");
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

        sb.append(indent()).append("impl ");

        if (node.getTraitName() != null) {
            sb.append(node.getTraitName()).append(" for ");
        }

        sb.append(node.getStructName()).append(" {\n");

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

        // Rust uses Result type and match for error handling
        sb.append(indent()).append("match (|| -> Result<(), Box<dyn std::error::Error>> {\n");

        indentLevel++;
        for (ASTNode stmt : node.getTryBlock()) {
            sb.append(stmt.accept(this));
            if (!stmt.accept(this).trim().endsWith("}")) {
                sb.append(";");
            }
            sb.append("\n");
        }
        sb.append(indent()).append("Ok(())\n");
        indentLevel--;

        sb.append(indent()).append("})() {\n");

        indentLevel++;
        sb.append(indent()).append("Ok(_) => {},\n");
        sb.append(indent()).append("Err(").append(node.getErrorVariable()).append(") => {\n");

        indentLevel++;
        for (ASTNode stmt : node.getCatchBlock()) {
            sb.append(stmt.accept(this));
            if (!stmt.accept(this).trim().endsWith("}")) {
                sb.append(";");
            }
            sb.append("\n");
        }
        indentLevel--;

        sb.append(indent()).append("}\n");
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
            return "()";
        }

        String baseType = switch (type.getName().toLowerCase()) {
            case "number" -> {
                // Check yummy annotation for specific number type
                if (type.getYummyAnnotation() != null) {
                    if (type.getYummyAnnotation().contains("i32")) yield "i32";
                    if (type.getYummyAnnotation().contains("i64")) yield "i64";
                    if (type.getYummyAnnotation().contains("u32")) yield "u32";
                    if (type.getYummyAnnotation().contains("u64")) yield "u64";
                    if (type.getYummyAnnotation().contains("f32")) yield "f32";
                    if (type.getYummyAnnotation().contains("f64")) yield "f64";
                }
                yield "i32"; // default
            }
            case "text" -> {
                if (type.isBorrowed()) {
                    yield "&str";
                }
                yield "String";
            }
            case "truth value", "boolean" -> "bool";
            case "nothing", "void" -> "()";
            case "list" -> "Vec";
            case "map" -> "HashMap";
            case "set" -> "HashSet";
            default -> type.getName();
        };

        if (type.isOptional()) {
            baseType = "Option<" + baseType + ">";
        }

        if (type.isBorrowed() && !baseType.startsWith("&")) {
            baseType = "&" + baseType;
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
