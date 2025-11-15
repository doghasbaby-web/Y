package com.ylang.ast;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AST node for variable declarations
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VariableDeclarationNode implements ASTNode {
    private String name;
    private TypeNode type;
    private ASTNode initializer;
    private boolean isMutable;
    private String yummyAnnotation;

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitVariableDeclaration(this);
    }
}
