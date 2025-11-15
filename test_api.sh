#!/bin/bash

# Test script for Y Language Compiler API

BASE_URL="http://localhost:8080/api"

echo "================================"
echo "Y Language Compiler API Tests"
echo "================================"
echo ""

# Test 1: Health Check
echo "Test 1: Health Check"
echo "--------------------"
curl -s "${BASE_URL}/compile/health"
echo -e "\n"

# Test 2: Compile to TypeScript
echo "Test 2: Compile to TypeScript"
echo "-----------------------------"
curl -X POST "${BASE_URL}/compile/typescript" \
  -H "Content-Type: text/plain" \
  -d 'Define a function called greet that takes name as text and returns text.
  Create a greeting by combining "Hello, " with name.
  Return the greeting.' | jq '.'
echo ""

# Test 3: Compile to Rust
echo "Test 3: Compile to Rust"
echo "-----------------------"
curl -X POST "${BASE_URL}/compile/rust" \
  -H "Content-Type: text/plain" \
  -d 'Define a function called add that takes x as number (Yummy: i32) and y as number (Yummy: i32) and returns number (Yummy: i32).
  Return x.' | jq '.'
echo ""

# Test 4: Compile using JSON request
echo "Test 4: Compile using JSON request"
echo "-----------------------------------"
curl -X POST "${BASE_URL}/compile" \
  -H "Content-Type: application/json" \
  -d '{
    "sourceCode": "Define a structure called Person with fields:\n  - name as text\n  - age as number",
    "targetLanguage": "TYPESCRIPT"
  }' | jq '.'
echo ""

# Test 5: Compile Fibonacci to TypeScript
echo "Test 5: Compile Fibonacci to TypeScript"
echo "----------------------------------------"
curl -X POST "${BASE_URL}/compile/typescript" \
  -H "Content-Type: text/plain" \
  -d 'Define a function called fibonacci that takes n as number and returns number.
  If n is less than 2:
    Return n.
  Otherwise:
    Return n.' | jq '.'
echo ""

# Test 6: Compile Struct to Rust
echo "Test 6: Compile Struct to Rust"
echo "-------------------------------"
curl -X POST "${BASE_URL}/compile/rust" \
  -H "Content-Type: text/plain" \
  -d 'Define a structure called Point (Yummy: derive Debug Clone) with fields:
  - x as number (Yummy: f64)
  - y as number (Yummy: f64)' | jq '.'
echo ""

echo "================================"
echo "All tests completed!"
echo "================================"
