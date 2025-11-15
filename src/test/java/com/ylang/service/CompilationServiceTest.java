package com.ylang.service;

import com.ylang.compiler.RustCompiler;
import com.ylang.compiler.TypeScriptCompiler;
import com.ylang.lexer.Lexer;
import com.ylang.model.CompilationRequest;
import com.ylang.model.CompilationResponse;
import com.ylang.parser.Parser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CompilationServiceTest {

    private CompilationService compilationService;

    @BeforeEach
    void setUp() {
        Lexer lexer = new Lexer();
        Parser parser = new Parser();
        TypeScriptCompiler tsCompiler = new TypeScriptCompiler();
        RustCompiler rustCompiler = new RustCompiler();

        compilationService = new CompilationService(lexer, parser, tsCompiler, rustCompiler);
    }

    @Test
    void testCompileSimpleFunctionToTypeScript() {
        String yCode = """
                Define a function called greet that takes name as text and returns text.
                  Return "Hello".
                """;

        CompilationResponse response = compilationService.compileToTypeScript(yCode);

        assertTrue(response.isSuccess());
        assertNotNull(response.getCompiledCode());
        assertTrue(response.getCompiledCode().contains("function greet"));
        assertTrue(response.getCompiledCode().contains("name: string"));
        assertTrue(response.getCompiledCode().contains(": string"));
    }

    @Test
    void testCompileSimpleFunctionToRust() {
        String yCode = """
                Define a function called add that takes x as number and y as number and returns number.
                  Return x.
                """;

        CompilationResponse response = compilationService.compileToRust(yCode);

        assertTrue(response.isSuccess());
        assertNotNull(response.getCompiledCode());
        assertTrue(response.getCompiledCode().contains("fn add"));
        assertTrue(response.getCompiledCode().contains("x: i32"));
        assertTrue(response.getCompiledCode().contains("y: i32"));
    }

    @Test
    void testCompileWithRequest() {
        CompilationRequest request = new CompilationRequest();
        request.setSourceCode("Define a function called test that returns nothing.");
        request.setTargetLanguage(CompilationRequest.TargetLanguage.TYPESCRIPT);

        CompilationResponse response = compilationService.compile(request);

        assertTrue(response.isSuccess());
        assertNotNull(response.getCompiledCode());
    }

    @Test
    void testCompileInvalidCode() {
        String invalidCode = "This is not valid Y language code!!!";

        CompilationResponse response = compilationService.compileToTypeScript(invalidCode);

        // Should not crash, might produce empty or partial output
        assertNotNull(response);
    }
}
