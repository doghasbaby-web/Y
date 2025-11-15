package com.ylang.ast;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * AST node for export statements
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExportNode implements ASTNode {
    private List<String> items = new ArrayList<>(); // Items to export
    private boolean isDefault; // Default export
    private ASTNode declaration; // The declaration being exported
    private String yummyAnnotation;

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitExport(this);
    }
}
