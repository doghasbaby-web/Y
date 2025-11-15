package com.ylang.ast;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AST node for assignments
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssignmentNode implements ASTNode {
    private String target;
    private ASTNode value;

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitAssignment(this);
    }
}
