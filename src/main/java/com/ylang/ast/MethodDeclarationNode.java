package com.ylang.ast;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * AST node for method declarations
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MethodDeclarationNode implements ASTNode {
    private String name;
    private List<ParameterNode> parameters = new ArrayList<>();
    private TypeNode returnType;
    private List<ASTNode> body = new ArrayList<>();
    private boolean isStatic;
    private String yummyAnnotation;

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitMethodDeclaration(this);
    }
}
