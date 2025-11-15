package com.ylang.ast;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AST node for identifiers
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IdentifierNode implements ASTNode {
    private String name;

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitIdentifier(this);
    }
}
