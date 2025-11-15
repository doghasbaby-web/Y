package com.ylang.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ylang.model.CompilationRequest;
import com.ylang.model.CompilationResponse;
import com.ylang.service.CompilationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Comprehensive tests for the CompilationController REST endpoints
 */
@WebMvcTest(CompilationController.class)
class CompilationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CompilationService compilationService;

    @Test
    void testHealthEndpoint() throws Exception {
        mockMvc.perform(get("/compile/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Y Language Compiler is running"));
    }

    @Test
    void testCompileEndpointSuccess() throws Exception {
        // Prepare test data
        CompilationRequest request = new CompilationRequest();
        request.setSourceCode("Define function called add");
        request.setTargetLanguage("TypeScript");

        CompilationResponse response = new CompilationResponse();
        response.setSuccess(true);
        response.setCompiledCode("function add() {}");
        response.setTargetLanguage("TypeScript");

        // Mock service
        when(compilationService.compile(any(CompilationRequest.class)))
                .thenReturn(response);

        // Perform request and verify
        mockMvc.perform(post("/compile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.compiledCode").value("function add() {}"))
                .andExpect(jsonPath("$.targetLanguage").value("TypeScript"));
    }

    @Test
    void testCompileEndpointFailure() throws Exception {
        // Prepare test data
        CompilationRequest request = new CompilationRequest();
        request.setSourceCode("invalid code");
        request.setTargetLanguage("TypeScript");

        CompilationResponse response = new CompilationResponse();
        response.setSuccess(false);
        response.setErrorMessage("Compilation failed");

        // Mock service
        when(compilationService.compile(any(CompilationRequest.class)))
                .thenReturn(response);

        // Perform request and verify
        mockMvc.perform(post("/compile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorMessage").value("Compilation failed"));
    }

    @Test
    void testCompileToTypeScriptSuccess() throws Exception {
        String sourceCode = "Define function called greet";

        CompilationResponse response = new CompilationResponse();
        response.setSuccess(true);
        response.setCompiledCode("function greet() {}");
        response.setTargetLanguage("TypeScript");

        when(compilationService.compileToTypeScript(anyString()))
                .thenReturn(response);

        mockMvc.perform(post("/compile/typescript")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sourceCode))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.compiledCode").value("function greet() {}"))
                .andExpect(jsonPath("$.targetLanguage").value("TypeScript"));
    }

    @Test
    void testCompileToTypeScriptFailure() throws Exception {
        String sourceCode = "invalid syntax";

        CompilationResponse response = new CompilationResponse();
        response.setSuccess(false);
        response.setErrorMessage("Syntax error");

        when(compilationService.compileToTypeScript(anyString()))
                .thenReturn(response);

        mockMvc.perform(post("/compile/typescript")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sourceCode))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorMessage").value("Syntax error"));
    }

    @Test
    void testCompileToRustSuccess() throws Exception {
        String sourceCode = "Define function called calculate";

        CompilationResponse response = new CompilationResponse();
        response.setSuccess(true);
        response.setCompiledCode("fn calculate() {}");
        response.setTargetLanguage("Rust");

        when(compilationService.compileToRust(anyString()))
                .thenReturn(response);

        mockMvc.perform(post("/compile/rust")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sourceCode))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.compiledCode").value("fn calculate() {}"))
                .andExpect(jsonPath("$.targetLanguage").value("Rust"));
    }

    @Test
    void testCompileToRustFailure() throws Exception {
        String sourceCode = "bad code";

        CompilationResponse response = new CompilationResponse();
        response.setSuccess(false);
        response.setErrorMessage("Parse error");

        when(compilationService.compileToRust(anyString()))
                .thenReturn(response);

        mockMvc.perform(post("/compile/rust")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sourceCode))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorMessage").value("Parse error"));
    }

    @Test
    void testCompileToPythonSuccess() throws Exception {
        String sourceCode = "Define function called process";

        CompilationResponse response = new CompilationResponse();
        response.setSuccess(true);
        response.setCompiledCode("def process():\n    pass");
        response.setTargetLanguage("Python");

        when(compilationService.compileToPython(anyString()))
                .thenReturn(response);

        mockMvc.perform(post("/compile/python")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sourceCode))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.compiledCode").exists())
                .andExpect(jsonPath("$.targetLanguage").value("Python"));
    }

    @Test
    void testCompileToPythonFailure() throws Exception {
        String sourceCode = "invalid";

        CompilationResponse response = new CompilationResponse();
        response.setSuccess(false);
        response.setErrorMessage("Compilation error");

        when(compilationService.compileToPython(anyString()))
                .thenReturn(response);

        mockMvc.perform(post("/compile/python")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sourceCode))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorMessage").value("Compilation error"));
    }

    @Test
    void testCompileToJavaScriptSuccess() throws Exception {
        String sourceCode = "Define function called init";

        CompilationResponse response = new CompilationResponse();
        response.setSuccess(true);
        response.setCompiledCode("function init() {}");
        response.setTargetLanguage("JavaScript");

        when(compilationService.compileToJavaScript(anyString()))
                .thenReturn(response);

        mockMvc.perform(post("/compile/javascript")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sourceCode))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.compiledCode").value("function init() {}"))
                .andExpect(jsonPath("$.targetLanguage").value("JavaScript"));
    }

    @Test
    void testCompileToJavaScriptFailure() throws Exception {
        String sourceCode = "error prone code";

        CompilationResponse response = new CompilationResponse();
        response.setSuccess(false);
        response.setErrorMessage("Failed to compile");

        when(compilationService.compileToJavaScript(anyString()))
                .thenReturn(response);

        mockMvc.perform(post("/compile/javascript")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sourceCode))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorMessage").value("Failed to compile"));
    }

    @Test
    void testCompileEndpointWithMultipleTargetLanguages() throws Exception {
        // Test TypeScript
        CompilationRequest tsRequest = new CompilationRequest();
        tsRequest.setSourceCode("Define function called test");
        tsRequest.setTargetLanguage("TypeScript");

        CompilationResponse tsResponse = new CompilationResponse();
        tsResponse.setSuccess(true);
        tsResponse.setCompiledCode("function test() {}");

        when(compilationService.compile(any(CompilationRequest.class)))
                .thenReturn(tsResponse);

        mockMvc.perform(post("/compile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tsRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // Test Rust
        CompilationRequest rustRequest = new CompilationRequest();
        rustRequest.setSourceCode("Define function called test");
        rustRequest.setTargetLanguage("Rust");

        CompilationResponse rustResponse = new CompilationResponse();
        rustResponse.setSuccess(true);
        rustResponse.setCompiledCode("fn test() {}");

        when(compilationService.compile(any(CompilationRequest.class)))
                .thenReturn(rustResponse);

        mockMvc.perform(post("/compile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rustRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testCompileWithEmptySourceCode() throws Exception {
        CompilationRequest request = new CompilationRequest();
        request.setSourceCode("");
        request.setTargetLanguage("TypeScript");

        CompilationResponse response = new CompilationResponse();
        response.setSuccess(false);
        response.setErrorMessage("Source code cannot be empty");

        when(compilationService.compile(any(CompilationRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/compile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void testCompileWithComplexSourceCode() throws Exception {
        String complexCode = """
                Define function called fibonacci that takes n as Number and returns Number
                    If n is less than 2:
                        Return n
                    Otherwise:
                        Return fibonacci(n minus 1) plus fibonacci(n minus 2)
                """;

        CompilationRequest request = new CompilationRequest();
        request.setSourceCode(complexCode);
        request.setTargetLanguage("TypeScript");

        CompilationResponse response = new CompilationResponse();
        response.setSuccess(true);
        response.setCompiledCode("function fibonacci(n: number): number { /* ... */ }");

        when(compilationService.compile(any(CompilationRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/compile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testCorsConfiguration() throws Exception {
        mockMvc.perform(get("/compile/health")
                        .header("Origin", "http://example.com"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Access-Control-Allow-Origin"));
    }

    @Test
    void testCompileEndpointAcceptsJson() throws Exception {
        CompilationRequest request = new CompilationRequest();
        request.setSourceCode("Define function called test");
        request.setTargetLanguage("TypeScript");

        CompilationResponse response = new CompilationResponse();
        response.setSuccess(true);
        response.setCompiledCode("function test() {}");

        when(compilationService.compile(any(CompilationRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/compile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testAllLanguageSpecificEndpoints() throws Exception {
        String sourceCode = "Define function called example";

        // Test all language-specific endpoints
        String[] endpoints = {"/typescript", "/rust", "/python", "/javascript"};
        String[] languages = {"TypeScript", "Rust", "Python", "JavaScript"};

        for (int i = 0; i < endpoints.length; i++) {
            CompilationResponse response = new CompilationResponse();
            response.setSuccess(true);
            response.setCompiledCode("compiled code");
            response.setTargetLanguage(languages[i]);

            // Mock the appropriate service method
            switch (languages[i]) {
                case "TypeScript":
                    when(compilationService.compileToTypeScript(anyString())).thenReturn(response);
                    break;
                case "Rust":
                    when(compilationService.compileToRust(anyString())).thenReturn(response);
                    break;
                case "Python":
                    when(compilationService.compileToPython(anyString())).thenReturn(response);
                    break;
                case "JavaScript":
                    when(compilationService.compileToJavaScript(anyString())).thenReturn(response);
                    break;
            }

            mockMvc.perform(post("/compile" + endpoints[i])
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(sourceCode))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.targetLanguage").value(languages[i]));
        }
    }
}
