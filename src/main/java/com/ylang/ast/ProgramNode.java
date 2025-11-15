package com.ylang.ast;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Root node representing the entire program
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProgramNode implements ASTNode {
    private List<ASTNode> statements = new ArrayList<>();

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitProgram(this);
    }

    public void addStatement(ASTNode statement) {
        statements.add(statement);
    }
}
