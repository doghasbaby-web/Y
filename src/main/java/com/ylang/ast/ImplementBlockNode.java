package com.ylang.ast;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * AST node for implement blocks
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImplementBlockNode implements ASTNode {
    private String structName;
    private String traitName; // null if just implementing methods
    private List<MethodDeclarationNode> methods = new ArrayList<>();
    private String yummyAnnotation;

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitImplementBlock(this);
    }
}
