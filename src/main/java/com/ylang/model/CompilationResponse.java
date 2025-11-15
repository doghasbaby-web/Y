package com.ylang.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response model for compilation
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompilationResponse {
    private boolean success;
    private String compiledCode;
    private String errorMessage;
    private CompilationRequest.TargetLanguage targetLanguage;

    public static CompilationResponse success(String compiledCode, CompilationRequest.TargetLanguage targetLanguage) {
        return new CompilationResponse(true, compiledCode, null, targetLanguage);
    }

    public static CompilationResponse error(String errorMessage, CompilationRequest.TargetLanguage targetLanguage) {
        return new CompilationResponse(false, null, errorMessage, targetLanguage);
    }
}
