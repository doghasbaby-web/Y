package com.ylang.ast;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * AST node for enum variant
 * Represents a single variant in an enum
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnumVariantNode {
    private String name;
    private List<FieldNode> fields = new ArrayList<>(); // For variants with data
    private String yummyAnnotation;
}
