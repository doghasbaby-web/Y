package com.ylang.compiler;

import com.ylang.ast.*;
import org.springframework.stereotype.Component;

/**
 * Compiler from Y Language to JavaScript (ES6+)
 */
@Component
public class JavaScriptCompiler implements ASTVisitor<String> {

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
            sb.append(param.getName());

            if (i < node.getParameters().size() - 1) {
                sb.append(", ");
            }
        }

        sb.append(") {\n");

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
        // JavaScript doesn't have interfaces, but we can use JSDoc comments
        StringBuilder sb = new StringBuilder();

        sb.append(indent()).append("/**\n");
        sb.append(indent()).append(" * @typedef {Object} ").append(node.getName()).append("\n");

        for (FieldNode field : node.getFields()) {
            sb.append(indent()).append(" * @property {");
            sb.append(mapType(field.getType()));
            sb.append("} ");
            sb.append(field.getName());
            sb.append("\n");
        }

        sb.append(indent()).append(" */");

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

        // Handle console.log for print
        if (node.getFunctionName().equalsIgnoreCase("print")) {
            sb.append("console.log");
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
            sb.append(param.getName());

            if (i < node.getParameters().size() - 1) {
                sb.append(", ");
            }
        }

        sb.append(") {\n");

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

    @Override
    public String visitEnumDeclaration(EnumDeclarationNode node) {
        StringBuilder sb = new StringBuilder();

        sb.append(indent()).append("const ").append(node.getName()).append(" = Object.freeze({\n");

        indentLevel++;
        for (EnumVariantNode variant : node.getVariants()) {
            sb.append(indent()).append(variant.getName()).append(": \"").append(variant.getName()).append("\",\n");
        }
        indentLevel--;

        sb.append(indent()).append("})");

        return sb.toString();
    }

    @Override
    public String visitTraitDeclaration(TraitDeclarationNode node) {
        // JavaScript doesn't have interfaces, use JSDoc
        StringBuilder sb = new StringBuilder();

        sb.append(indent()).append("/**\n");
        sb.append(indent()).append(" * @interface ").append(node.getName()).append("\n");

        for (MethodSignatureNode method : node.getMethods()) {
            sb.append(indent()).append(" * @method ").append(method.getName()).append("(");
            for (int i = 0; i < method.getParameters().size(); i++) {
                ParameterNode param = method.getParameters().get(i);
                sb.append(param.getName());
                if (i < method.getParameters().size() - 1) {
                    sb.append(", ");
                }
            }
            sb.append(")\n");
        }

        sb.append(indent()).append(" */");

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
                sb.append(stmt.accept(this));
                if (!stmt.accept(this).trim().endsWith("}")) {
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

        sb.append("// Module: ").append(node.getName()).append("\n\n");

        for (ASTNode stmt : node.getStatements()) {
            sb.append(stmt.accept(this));
            sb.append("\n");
        }

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
        // JavaScript doesn't have type aliases, use JSDoc
        StringBuilder sb = new StringBuilder();

        sb.append(indent()).append("/**\n");
        sb.append(indent()).append(" * @typedef {").append(mapType(node.getAliasedType())).append("} ");
        sb.append(node.getName()).append("\n");
        sb.append(indent()).append(" */");

        return sb.toString();
    }

    @Override
    public String visitClosure(ClosureNode node) {
        StringBuilder sb = new StringBuilder();

        sb.append("(");
        for (int i = 0; i < node.getParameters().size(); i++) {
            ParameterNode param = node.getParameters().get(i);
            sb.append(param.getName());
            if (i < node.getParameters().size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(") => {\n");

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
    public String visitClassDeclaration(ClassDeclarationNode node) {
        StringBuilder sb = new StringBuilder();

        sb.append(indent()).append("class ").append(node.getName());

        if (node.getExtendsClass() != null) {
            sb.append(" extends ").append(node.getExtendsClass());
        }

        sb.append(" {\n");

        indentLevel++;

        // Constructor
        if (node.getConstructor() != null) {
            MethodDeclarationNode constructor = node.getConstructor();
            sb.append(indent()).append("constructor(");
            for (int i = 0; i < constructor.getParameters().size(); i++) {
                ParameterNode param = constructor.getParameters().get(i);
                sb.append(param.getName());
                if (i < constructor.getParameters().size() - 1) {
                    sb.append(", ");
                }
            }
            sb.append(") {\n");
            indentLevel++;

            // Initialize fields
            for (FieldNode field : node.getFields()) {
                sb.append(indent()).append("this.").append(field.getName()).append(";\n");
            }

            for (ASTNode stmt : constructor.getBody()) {
                sb.append(stmt.accept(this));
                if (!stmt.accept(this).trim().endsWith("}")) {
                    sb.append(";");
                }
                sb.append("\n");
            }
            indentLevel--;
            sb.append(indent()).append("}\n\n");
        }

        // Methods
        for (MethodDeclarationNode method : node.getMethods()) {
            sb.append(method.accept(this)).append("\n\n");
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

        // JavaScript doesn't have strict types, but for JSDoc:
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
