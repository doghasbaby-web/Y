package com.ylang.ast;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * AST node for if statements
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IfStatementNode implements ASTNode {
    private ASTNode condition;
    private List<ASTNode> thenBlock = new ArrayList<>();
    private List<ASTNode> elseBlock = new ArrayList<>();

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitIfStatement(this);
    }
}
