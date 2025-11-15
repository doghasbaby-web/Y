package com.ylang.ast;

/**
 * Base interface for all AST nodes
 */
public interface ASTNode {
    /**
     * Accept a visitor
     */
    <T> T accept(ASTVisitor<T> visitor);
}
