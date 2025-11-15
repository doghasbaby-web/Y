package com.ylang.ast;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a type
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TypeNode {
    private String name; // number, text, etc.
    private List<TypeNode> genericParams = new ArrayList<>();
    private boolean isMutable;
    private boolean isBorrowed;
    private boolean isOptional;
    private String yummyAnnotation;

    public TypeNode(String name) {
        this.name = name;
    }
}
