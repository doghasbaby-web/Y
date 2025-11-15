package com.ylang.ast;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * AST node for function declarations
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FunctionDeclarationNode implements ASTNode {
    private String name;
    private List<ParameterNode> parameters = new ArrayList<>();
    private TypeNode returnType;
    private List<ASTNode> body = new ArrayList<>();
    private boolean isAsync;
    private String yummyAnnotation; // For generic types, lifetimes, etc.

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitFunctionDeclaration(this);
    }
}
