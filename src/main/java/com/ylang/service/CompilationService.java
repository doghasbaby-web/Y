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
import org.springframework.cache.annotation.Cacheable;
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

    /**
     * Compile Y language source code to target language
     * Results are cached based on source code and target language
     */
    @Cacheable(value = "compilations", key = "#request.sourceCode + '_' + #request.targetLanguage")
    public CompilationResponse compile(CompilationRequest request) {
        try {
            log.info("Compiling Y language to {}", request.getTargetLanguage());
            log.debug("Source code:\n{}", request.getSourceCode());

            // Step 1: Lexical analysis
            List<Token> tokens = lexer.tokenize(request.getSourceCode());
            log.debug("Tokens: {}", tokens.size());

            // Step 2: Parse into AST
            // Create new parser instance for thread safety
            Parser parser = new Parser();
            ProgramNode ast = parser.parse(tokens);
            log.debug("AST parsed successfully");

            // Step 3: Compile to target language
            // Create new compiler instance for thread safety
            String compiledCode;
            switch (request.getTargetLanguage()) {
                case TYPESCRIPT:
                    compiledCode = new TypeScriptCompiler().compile(ast);
                    break;
                case RUST:
                    compiledCode = new RustCompiler().compile(ast);
                    break;
                case PYTHON:
                    compiledCode = new PythonCompiler().compile(ast);
                    break;
                case JAVASCRIPT:
                    compiledCode = new JavaScriptCompiler().compile(ast);
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
}
