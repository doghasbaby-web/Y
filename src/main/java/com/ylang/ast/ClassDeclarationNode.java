package com.ylang.ast;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * AST node for class declarations (TypeScript)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassDeclarationNode implements ASTNode {
    private String name;
    private List<FieldNode> fields = new ArrayList<>();
    private List<MethodDeclarationNode> methods = new ArrayList<>();
    private MethodDeclarationNode constructor;
    private String extendsClass; // Superclass name
    private List<String> implementsInterfaces = new ArrayList<>();
    private String yummyAnnotation;

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitClassDeclaration(this);
    }
}
