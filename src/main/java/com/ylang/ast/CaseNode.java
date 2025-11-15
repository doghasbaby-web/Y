package com.ylang.ast;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * AST node for a single case in pattern matching
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CaseNode {
    private ASTNode pattern; // The pattern to match
    private List<ASTNode> body = new ArrayList<>(); // Statements to execute
    private boolean isDefault; // Whether this is the default case
}
