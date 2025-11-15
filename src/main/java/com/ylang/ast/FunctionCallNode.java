package com.ylang.ast;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * AST node for function calls
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FunctionCallNode implements ASTNode {
    private String functionName;
    private List<ASTNode> arguments = new ArrayList<>();
    private boolean isAwait;

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitFunctionCall(this);
    }
}
