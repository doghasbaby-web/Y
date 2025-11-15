package com.ylang.ast;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * AST node for pattern matching (match/switch)
 * Represents Rust match and TypeScript switch
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchNode implements ASTNode {
    private ASTNode expression; // The value being matched
    private List<CaseNode> cases = new ArrayList<>();
    private String yummyAnnotation;

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitMatch(this);
    }
}
