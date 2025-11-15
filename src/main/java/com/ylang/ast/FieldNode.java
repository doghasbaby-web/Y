package com.ylang.ast;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a struct field
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FieldNode {
    private String name;
    private TypeNode type;
    private String yummyAnnotation;
}
