package com.ylang.ast;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * AST node for closures/lambda expressions
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClosureNode implements ASTNode {
    private List<ParameterNode> parameters = new ArrayList<>();
    private List<ASTNode> body = new ArrayList<>();
    private TypeNode returnType;
    private String yummyAnnotation;

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitClosure(this);
    }
}
