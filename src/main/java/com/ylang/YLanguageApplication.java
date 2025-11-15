package com.ylang;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Main Spring Boot application for Y Language Compiler
 */
@SpringBootApplication
@EnableCaching
public class YLanguageApplication {

    public static void main(String[] args) {
        SpringApplication.run(YLanguageApplication.class, args);
    }
}
