package com.ylang.ast;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * AST node for module declarations
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModuleNode implements ASTNode {
    private String name;
    private List<ASTNode> statements = new ArrayList<>();
    private String yummyAnnotation;

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitModule(this);
    }
}
