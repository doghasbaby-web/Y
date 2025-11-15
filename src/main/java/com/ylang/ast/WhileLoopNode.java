package com.ylang.ast;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * AST node for while loops
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WhileLoopNode implements ASTNode {
    private ASTNode condition;
    private List<ASTNode> body = new ArrayList<>();

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitWhileLoop(this);
    }
}
