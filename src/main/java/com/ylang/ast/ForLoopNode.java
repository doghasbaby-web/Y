package com.ylang.ast;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * AST node for for loops
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForLoopNode implements ASTNode {
    private String variable;
    private ASTNode iterable;
    private List<ASTNode> body = new ArrayList<>();

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitForLoop(this);
    }
}
