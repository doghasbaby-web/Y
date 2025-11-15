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
     * Compile Y language to a specific target language using path variable
     * Supported languages: typescript, rust, python, javascript
     */
    @PostMapping("/{language}")
    public ResponseEntity<CompilationResponse> compileToLanguage(
            @PathVariable String language,
            @RequestBody String sourceCode) {
        log.info("Received {} compilation request", language);

        CompilationRequest request = new CompilationRequest();
        request.setSourceCode(sourceCode);

        try {
            CompilationRequest.TargetLanguage targetLanguage =
                CompilationRequest.TargetLanguage.valueOf(language.toUpperCase());
            request.setTargetLanguage(targetLanguage);
        } catch (IllegalArgumentException e) {
            CompilationResponse errorResponse = new CompilationResponse();
            errorResponse.setSuccess(false);
            errorResponse.setError("Unsupported language: " + language + ". Supported: typescript, rust, python, javascript");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        CompilationResponse response = compilationService.compile(request);

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
