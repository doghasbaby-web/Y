package com.ylang.compiler;

import com.ylang.ast.*;
import org.springframework.stereotype.Component;

/**
 * Compiler from Y Language to TypeScript
 * Note: This class is NOT a singleton. Create a new instance for each compilation
 * to ensure thread safety.
 */
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
            String compiled = stmt.accept(this);
            sb.append(compiled);
            if (!compiled.trim().endsWith("}")) {
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
            String compiled = stmt.accept(this);
            sb.append(compiled);
            if (!compiled.trim().endsWith("}")) {
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
                String compiled = stmt.accept(this);
                sb.append(compiled);
                if (!compiled.trim().endsWith("}")) {
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
            String compiled = stmt.accept(this);
            sb.append(compiled);
            if (!compiled.trim().endsWith("}")) {
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
            String compiled = stmt.accept(this);
            sb.append(compiled);
            if (!compiled.trim().endsWith("}")) {
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
            String compiled = stmt.accept(this);
            sb.append(compiled);
            if (!compiled.trim().endsWith("}")) {
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
            String compiled = stmt.accept(this);
            sb.append(compiled);
            if (!compiled.trim().endsWith("}")) {
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
            String compiled = stmt.accept(this);
            sb.append(compiled);
            if (!compiled.trim().endsWith("}")) {
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

    @Override
    public String visitEnumDeclaration(EnumDeclarationNode node) {
        StringBuilder sb = new StringBuilder();

        sb.append(indent()).append("enum ").append(node.getName()).append(" {\n");

        indentLevel++;
        for (int i = 0; i < node.getVariants().size(); i++) {
            EnumVariantNode variant = node.getVariants().get(i);
            sb.append(indent()).append(variant.getName());
            if (i < node.getVariants().size() - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }
        indentLevel--;

        sb.append(indent()).append("}");

        return sb.toString();
    }

    @Override
    public String visitTraitDeclaration(TraitDeclarationNode node) {
        StringBuilder sb = new StringBuilder();

        sb.append(indent()).append("interface ").append(node.getName()).append(" {\n");

        indentLevel++;
        for (MethodSignatureNode method : node.getMethods()) {
            sb.append(indent()).append(method.getName()).append("(");
            for (int i = 0; i < method.getParameters().size(); i++) {
                ParameterNode param = method.getParameters().get(i);
                sb.append(param.getName()).append(": ").append(mapType(param.getType()));
                if (i < method.getParameters().size() - 1) {
                    sb.append(", ");
                }
            }
            sb.append("): ").append(mapType(method.getReturnType())).append(";\n");
        }
        indentLevel--;

        sb.append(indent()).append("}");

        return sb.toString();
    }

    @Override
    public String visitMatch(MatchNode node) {
        StringBuilder sb = new StringBuilder();

        sb.append(indent()).append("switch (");
        sb.append(node.getExpression().accept(this));
        sb.append(") {\n");

        indentLevel++;
        for (CaseNode caseNode : node.getCases()) {
            if (caseNode.isDefault()) {
                sb.append(indent()).append("default:\n");
            } else {
                sb.append(indent()).append("case ");
                sb.append(caseNode.getPattern().accept(this));
                sb.append(":\n");
            }

            indentLevel++;
            for (ASTNode stmt : caseNode.getBody()) {
                String compiled = stmt.accept(this);
                sb.append(compiled);
                if (!compiled.trim().endsWith("}")) {
                    sb.append(";");
                }
                sb.append("\n");
            }
            sb.append(indent()).append("break;\n");
            indentLevel--;
        }
        indentLevel--;

        sb.append(indent()).append("}");

        return sb.toString();
    }

    @Override
    public String visitModule(ModuleNode node) {
        StringBuilder sb = new StringBuilder();

        sb.append(indent()).append("namespace ").append(node.getName()).append(" {\n");

        indentLevel++;
        for (ASTNode stmt : node.getStatements()) {
            sb.append(stmt.accept(this));
            sb.append("\n");
        }
        indentLevel--;

        sb.append(indent()).append("}");

        return sb.toString();
    }

    @Override
    public String visitImport(ImportNode node) {
        StringBuilder sb = new StringBuilder();

        sb.append(indent()).append("import ");

        if (node.isWildcard()) {
            sb.append("* as ").append(node.getAlias() != null ? node.getAlias() : "module");
        } else if (!node.getItems().isEmpty()) {
            sb.append("{ ");
            for (int i = 0; i < node.getItems().size(); i++) {
                sb.append(node.getItems().get(i));
                if (i < node.getItems().size() - 1) {
                    sb.append(", ");
                }
            }
            sb.append(" }");
        }

        sb.append(" from \"").append(node.getModulePath()).append("\"");

        return sb.toString();
    }

    @Override
    public String visitExport(ExportNode node) {
        StringBuilder sb = new StringBuilder();

        sb.append(indent()).append("export ");

        if (node.isDefault()) {
            sb.append("default ");
        }

        if (node.getDeclaration() != null) {
            sb.append(node.getDeclaration().accept(this));
        } else if (!node.getItems().isEmpty()) {
            sb.append("{ ");
            for (int i = 0; i < node.getItems().size(); i++) {
                sb.append(node.getItems().get(i));
                if (i < node.getItems().size() - 1) {
                    sb.append(", ");
                }
            }
            sb.append(" }");
        }

        return sb.toString();
    }

    @Override
    public String visitTypeAlias(TypeAliasNode node) {
        StringBuilder sb = new StringBuilder();

        sb.append(indent()).append("type ").append(node.getName());
        sb.append(" = ").append(mapType(node.getAliasedType()));

        return sb.toString();
    }

    @Override
    public String visitClosure(ClosureNode node) {
        StringBuilder sb = new StringBuilder();

        sb.append("(");
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
            sb.append(": ").append(mapType(node.getReturnType()));
        }

        sb.append(" => {\n");

        indentLevel++;
        for (ASTNode stmt : node.getBody()) {
            String compiled = stmt.accept(this);
            sb.append(compiled);
            if (!compiled.trim().endsWith("}")) {
                sb.append(";");
            }
            sb.append("\n");
        }
        indentLevel--;

        sb.append(indent()).append("}");

        return sb.toString();
    }

    @Override
    public String visitClassDeclaration(ClassDeclarationNode node) {
        StringBuilder sb = new StringBuilder();

        sb.append(indent()).append("class ").append(node.getName());

        if (node.getExtendsClass() != null) {
            sb.append(" extends ").append(node.getExtendsClass());
        }

        if (!node.getImplementsInterfaces().isEmpty()) {
            sb.append(" implements ");
            for (int i = 0; i < node.getImplementsInterfaces().size(); i++) {
                sb.append(node.getImplementsInterfaces().get(i));
                if (i < node.getImplementsInterfaces().size() - 1) {
                    sb.append(", ");
                }
            }
        }

        sb.append(" {\n");

        indentLevel++;

        // Fields
        for (FieldNode field : node.getFields()) {
            sb.append(indent()).append(field.getName()).append(": ");
            sb.append(mapType(field.getType())).append(";\n");
        }

        // Constructor
        if (node.getConstructor() != null) {
            sb.append("\n").append(indent()).append("constructor(");
            MethodDeclarationNode constructor = node.getConstructor();
            for (int i = 0; i < constructor.getParameters().size(); i++) {
                ParameterNode param = constructor.getParameters().get(i);
                sb.append(param.getName()).append(": ").append(mapType(param.getType()));
                if (i < constructor.getParameters().size() - 1) {
                    sb.append(", ");
                }
            }
            sb.append(") {\n");
            indentLevel++;
            for (ASTNode stmt : constructor.getBody()) {
                String compiled = stmt.accept(this);
                sb.append(compiled);
                if (!compiled.trim().endsWith("}")) {
                    sb.append(";");
                }
                sb.append("\n");
            }
            indentLevel--;
            sb.append(indent()).append("}\n");
        }

        // Methods
        for (MethodDeclarationNode method : node.getMethods()) {
            sb.append("\n").append(method.accept(this)).append("\n");
        }

        indentLevel--;

        sb.append(indent()).append("}");

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
