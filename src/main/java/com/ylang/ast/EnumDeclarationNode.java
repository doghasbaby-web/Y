package com.ylang.ast;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * AST node for enum declarations
 * Represents Rust enums and TypeScript union types
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnumDeclarationNode implements ASTNode {
    private String name;
    private List<EnumVariantNode> variants = new ArrayList<>();
    private String yummyAnnotation;

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitEnumDeclaration(this);
    }
}
