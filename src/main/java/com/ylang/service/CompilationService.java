package com.ylang.service;

import com.ylang.ast.ProgramNode;
import com.ylang.compiler.*;
import com.ylang.lexer.Lexer;
import com.ylang.lexer.Token;
import com.ylang.model.CompilationRequest;
import com.ylang.model.CompilationResponse;
import com.ylang.parser.Parser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for compiling Y Language to target languages
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CompilationService {

    private final Lexer lexer;
    private final Parser parser;
    private final TypeScriptCompiler typeScriptCompiler;
    private final RustCompiler rustCompiler;
    private final PythonCompiler pythonCompiler;
    private final JavaScriptCompiler javaScriptCompiler;

    /**
     * Compile Y language source code to target language
     */
    public CompilationResponse compile(CompilationRequest request) {
        try {
            log.info("Compiling Y language to {}", request.getTargetLanguage());
            log.debug("Source code:\n{}", request.getSourceCode());

            // Step 1: Lexical analysis
            List<Token> tokens = lexer.tokenize(request.getSourceCode());
            log.debug("Tokens: {}", tokens.size());

            // Step 2: Parse into AST
            ProgramNode ast = parser.parse(tokens);
            log.debug("AST parsed successfully");

            // Step 3: Compile to target language
            String compiledCode;
            switch (request.getTargetLanguage()) {
                case TYPESCRIPT:
                    compiledCode = typeScriptCompiler.compile(ast);
                    break;
                case RUST:
                    compiledCode = rustCompiler.compile(ast);
                    break;
                case PYTHON:
                    compiledCode = pythonCompiler.compile(ast);
                    break;
                case JAVASCRIPT:
                    compiledCode = javaScriptCompiler.compile(ast);
                    break;
                case JAVA:
                case C:
                    throw new UnsupportedOperationException("Compiler for " + request.getTargetLanguage() + " is not yet implemented");
                default:
                    throw new IllegalArgumentException("Unsupported target language: " + request.getTargetLanguage());
            }

            log.info("Compilation successful");
            log.debug("Compiled code:\n{}", compiledCode);

            return CompilationResponse.success(compiledCode, request.getTargetLanguage());

        } catch (Exception e) {
            log.error("Compilation failed", e);
            return CompilationResponse.error(e.getMessage(), request.getTargetLanguage());
        }
    }

    /**
     * Compile Y language to TypeScript
     */
    public CompilationResponse compileToTypeScript(String sourceCode) {
        CompilationRequest request = new CompilationRequest();
        request.setSourceCode(sourceCode);
        request.setTargetLanguage(CompilationRequest.TargetLanguage.TYPESCRIPT);
        return compile(request);
    }

    /**
     * Compile Y language to Rust
     */
    public CompilationResponse compileToRust(String sourceCode) {
        CompilationRequest request = new CompilationRequest();
        request.setSourceCode(sourceCode);
        request.setTargetLanguage(CompilationRequest.TargetLanguage.RUST);
        return compile(request);
    }

    /**
     * Compile Y language to Python
     */
    public CompilationResponse compileToPython(String sourceCode) {
        CompilationRequest request = new CompilationRequest();
        request.setSourceCode(sourceCode);
        request.setTargetLanguage(CompilationRequest.TargetLanguage.PYTHON);
        return compile(request);
    }

    /**
     * Compile Y language to JavaScript
     */
    public CompilationResponse compileToJavaScript(String sourceCode) {
        CompilationRequest request = new CompilationRequest();
        request.setSourceCode(sourceCode);
        request.setTargetLanguage(CompilationRequest.TargetLanguage.JAVASCRIPT);
        return compile(request);
    }
}
