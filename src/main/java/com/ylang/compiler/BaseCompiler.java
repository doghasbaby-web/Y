package com.ylang.compiler;

import com.ylang.ast.*;

import java.util.List;

/**
 * Abstract base class for all compilers that provides common functionality
 * and reduces code duplication across target language compilers.
 */
public abstract class BaseCompiler implements ASTVisitor<String> {

    protected int indentLevel = 0;

    /**
     * Returns the indent string for this compiler (e.g., "  " for TypeScript, "    " for Python)
     */
    protected abstract String getIndentString();

    /**
     * Maps Y language type to target language type
     */
    protected abstract String mapType(TypeNode type);

    /**
     * Maps Y language operator to target language operator
     */
    protected abstract String mapOperator(String operator);

    /**
     * Whether this language uses semicolons to terminate statements
     */
    protected abstract boolean usesSemicolons();

    /**
     * Compiles a program node
     */
    public String compile(ProgramNode program) {
        return program.accept(this);
    }

    /**
     * Returns the current indentation string
     */
    protected String indent() {
        return getIndentString().repeat(indentLevel);
    }

    /**
     * Helper method to compile a list of statements with proper formatting
     */
    protected String compileStatements(List<ASTNode> statements) {
        StringBuilder sb = new StringBuilder();
        for (ASTNode stmt : statements) {
            String compiled = stmt.accept(this);
            sb.append(compiled);
            if (usesSemicolons() && !compiled.trim().endsWith("}")) {
                sb.append(";");
            }
            sb.append("\n");
        }
        return sb.toString();
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
    public String visitVariableDeclaration(VariableDeclarationNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent());
        sb.append(getVariableDeclarationKeyword(node.isMutable()));
        sb.append(" ");
        sb.append(node.getName());

        if (node.getType() != null && supportsTypeAnnotations()) {
            sb.append(getTypeAnnotationSeparator());
            sb.append(mapType(node.getType()));
        }

        if (node.getInitializer() != null) {
            sb.append(" = ");
            sb.append(node.getInitializer().accept(this));
        }

        return sb.toString();
    }

    /**
     * Returns the keyword for variable declaration (e.g., "let", "const", "var", "let mut")
     */
    protected abstract String getVariableDeclarationKeyword(boolean mutable);

    /**
     * Whether this language supports type annotations
     */
    protected boolean supportsTypeAnnotations() {
        return true;
    }

    /**
     * Returns the type annotation separator (e.g., ":" for TypeScript, " " for Rust)
     */
    protected String getTypeAnnotationSeparator() {
        return ": ";
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
    public String visitAssignment(AssignmentNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent());
        sb.append(node.getTarget());
        sb.append(" = ");
        sb.append(node.getValue().accept(this));
        return sb.toString();
    }
}
