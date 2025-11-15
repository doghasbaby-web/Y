package com.ylang.ast;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * AST node for method signature in trait/interface
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MethodSignatureNode {
    private String name;
    private List<ParameterNode> parameters = new ArrayList<>();
    private TypeNode returnType;
    private String yummyAnnotation;
}
