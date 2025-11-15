package com.ylang.controller;

import com.ylang.model.CompilationRequest;
import com.ylang.model.CompilationResponse;
import com.ylang.service.CompilationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST API Controller for Y Language compilation
 */
@RestController
@RequestMapping("/compile")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class CompilationController {

    private final CompilationService compilationService;

    /**
     * Compile Y language to specified target language
     */
    @PostMapping
    public ResponseEntity<CompilationResponse> compile(@Valid @RequestBody CompilationRequest request) {
        log.info("Received compilation request for target: {}", request.getTargetLanguage());

        CompilationResponse response = compilationService.compile(request);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Compile Y language to TypeScript
     */
    @PostMapping("/typescript")
    public ResponseEntity<CompilationResponse> compileToTypeScript(@RequestBody String sourceCode) {
        log.info("Received TypeScript compilation request");

        CompilationResponse response = compilationService.compileToTypeScript(sourceCode);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Compile Y language to Rust
     */
    @PostMapping("/rust")
    public ResponseEntity<CompilationResponse> compileToRust(@RequestBody String sourceCode) {
        log.info("Received Rust compilation request");

        CompilationResponse response = compilationService.compileToRust(sourceCode);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Compile Y language to Python
     */
    @PostMapping("/python")
    public ResponseEntity<CompilationResponse> compileToPython(@RequestBody String sourceCode) {
        log.info("Received Python compilation request");

        CompilationResponse response = compilationService.compileToPython(sourceCode);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Compile Y language to JavaScript
     */
    @PostMapping("/javascript")
    public ResponseEntity<CompilationResponse> compileToJavaScript(@RequestBody String sourceCode) {
        log.info("Received JavaScript compilation request");

        CompilationResponse response = compilationService.compileToJavaScript(sourceCode);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Y Language Compiler is running");
    }
}
