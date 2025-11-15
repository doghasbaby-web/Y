package com.ylang.ast;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AST node for binary expressions
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BinaryExpressionNode implements ASTNode {
    private ASTNode left;
    private String operator; // plus, minus, equals, etc.
    private ASTNode right;

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitBinaryExpression(this);
    }
}
