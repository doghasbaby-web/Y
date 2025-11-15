package com.ylang.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request model for compilation
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompilationRequest {

    @NotBlank(message = "Source code cannot be empty")
    private String sourceCode;

    @NotNull(message = "Target language must be specified")
    private TargetLanguage targetLanguage;

    public enum TargetLanguage {
        TYPESCRIPT,
        RUST
    }
}
