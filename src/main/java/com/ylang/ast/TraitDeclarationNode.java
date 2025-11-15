package com.ylang.ast;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * AST node for trait/interface declarations
 * Represents Rust traits and TypeScript interfaces
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TraitDeclarationNode implements ASTNode {
    private String name;
    private List<MethodSignatureNode> methods = new ArrayList<>();
    private String yummyAnnotation;

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitTraitDeclaration(this);
    }
}
