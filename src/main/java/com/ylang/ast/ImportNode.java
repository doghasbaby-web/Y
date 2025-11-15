package com.ylang.ast;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * AST node for import statements
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImportNode implements ASTNode {
    private String modulePath; // e.g., "std::collections::HashMap"
    private List<String> items = new ArrayList<>(); // Specific items to import
    private boolean isWildcard; // Import everything (use * or ::*)
    private String alias; // Optional alias for the import
    private String yummyAnnotation;

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitImport(this);
    }
}
