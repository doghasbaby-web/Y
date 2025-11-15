package com.ylang.ast;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * AST node for struct/class declarations
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StructDeclarationNode implements ASTNode {
    private String name;
    private List<FieldNode> fields = new ArrayList<>();
    private String yummyAnnotation;

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitStructDeclaration(this);
    }
}
