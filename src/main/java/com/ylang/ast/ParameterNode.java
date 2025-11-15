package com.ylang.ast;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a function parameter
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParameterNode {
    private String name;
    private TypeNode type;
    private String yummyAnnotation;
}
