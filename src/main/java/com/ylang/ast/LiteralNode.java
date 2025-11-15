package com.ylang.ast;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AST node for literals (numbers, strings, booleans)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LiteralNode implements ASTNode {
    private Object value;
    private String type; // "number", "string", "boolean"

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitLiteral(this);
    }
}
