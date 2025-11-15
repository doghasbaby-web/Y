package com.ylang.ast;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * AST node for try-catch blocks
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TryCatchNode implements ASTNode {
    private List<ASTNode> tryBlock = new ArrayList<>();
    private String errorVariable;
    private List<ASTNode> catchBlock = new ArrayList<>();

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitTryCatch(this);
    }
}
