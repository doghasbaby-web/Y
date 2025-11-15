package com.ylang.ast;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AST node for return statements
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReturnStatementNode implements ASTNode {
    private ASTNode expression;

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitReturnStatement(this);
    }
}
