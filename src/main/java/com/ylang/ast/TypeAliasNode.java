package com.ylang.ast;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AST node for type aliases
 * Represents Rust type aliases and TypeScript type declarations
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TypeAliasNode implements ASTNode {
    private String name;
    private TypeNode aliasedType;
    private String yummyAnnotation;

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitTypeAlias(this);
    }
}
