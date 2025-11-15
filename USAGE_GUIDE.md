# Y Language Usage Guide

## Quick Start Guide

This guide will help you get started with Y Language quickly.

## Installation

### Method 1: Using Maven

```bash
# Clone the repository
git clone https://github.com/doghasbaby-web/Y.git
cd Y

# Build and run
mvn spring-boot:run
```

### Method 2: Using Docker

```bash
# Build and run with Docker Compose
docker-compose up -d

# Check logs
docker-compose logs -f
```

### Method 3: Build JAR and run

```bash
# Build JAR
mvn clean package

# Run JAR
java -jar target/y-language-compiler-1.0.0.jar
```

## Writing Your First Y Program

### Example 1: Hello World

Create a file `hello.y`:

```y
Define a function called main that returns nothing.
  Print "Hello, World!" to console.
```

### Example 2: Function with Parameters

```y
Define a function called add that takes x as number and y as number and returns number.
  Create a result with value x plus y.
  Return result.
```

### Example 3: Struct Definition

```y
Define a structure called User with fields:
  - id as number (Yummy: u64)
  - username as text
  - email as text
  - is_active as truth value
```

### Example 4: Async Function

```y
Define an async function called fetch_user that takes id as number and returns text.
  Create a url by combining "https://api.example.com/users/" with id.
  Send a GET request to url and wait for response.
  Return response.
```

## Using the API

### Start the Server

```bash
mvn spring-boot:run
```

The server will be available at `http://localhost:8080/api`

### Compile to TypeScript

```bash
curl -X POST http://localhost:8080/api/compile/typescript \
  -H "Content-Type: text/plain" \
  -d 'Define a function called greet that takes name as text and returns text.
  Return "Hello".'
```

### Compile to Rust

```bash
curl -X POST http://localhost:8080/api/compile/rust \
  -H "Content-Type: text/plain" \
  -d 'Define a function called add that takes x as number (Yummy: i32) and y as number (Yummy: i32) and returns number (Yummy: i32).
  Return x.'
```

### Using JSON Request

```bash
curl -X POST http://localhost:8080/api/compile \
  -H "Content-Type: application/json" \
  -d '{
    "sourceCode": "Define a function called test that returns nothing.",
    "targetLanguage": "TYPESCRIPT"
  }'
```

## Running Tests

### Run All Tests

```bash
mvn test
```

### Run Specific Test Class

```bash
mvn test -Dtest=LexerTest
mvn test -Dtest=TypeScriptCompilerTest
mvn test -Dtest=RustCompilerTest
```

### Run Test Suite with Coverage

```bash
mvn clean test jacoco:report
```

## Using the Test Script

We provide a convenient test script to verify the API:

```bash
./test_api.sh
```

This script will:
1. Check server health
2. Compile examples to TypeScript
3. Compile examples to Rust
4. Test various language features

## Language Features

### Variables

**Mutable Variable:**
```y
Create a mutable variable called counter with initial value 0.
```

**Immutable Variable:**
```y
Create a variable called max_size with value 100.
```

### Functions

**Basic Function:**
```y
Define a function called square that takes x as number and returns number.
  Return x multiplied by x.
```

**Multiple Parameters:**
```y
Define a function called calculate that takes x as number and y as number and z as number and returns number.
  Create result with value x plus y multiplied by z.
  Return result.
```

### Control Flow

**If Statement:**
```y
If x is greater than 10:
  Print "Large".
Otherwise if x is greater than 5:
  Print "Medium".
Otherwise:
  Print "Small".
```

**For Loop:**
```y
For each item in items:
  Print item.
```

**While Loop:**
```y
While counter is less than 10:
  Increment counter by 1.
```

### Data Structures

**Struct:**
```y
Define a structure called Rectangle with fields:
  - width as number
  - height as number
```

**Methods:**
```y
Implement methods for Rectangle:
  Define a method called area that returns number.
    Return width multiplied by height.
```

### Error Handling

**Try-Catch:**
```y
Try:
  Open file "data.txt".
  Read contents.
Catch error:
  Print "Error:" with error message.
```

### Yummy Annotations

For precise technical specifications:

**Generic Types:**
```y
Define a function called process (Yummy: generic with type T where T implements Display)
  that takes item (Yummy: T) and returns nothing.
```

**Specific Number Types:**
```y
Create a variable called count with value 0 (Yummy: i32 in Rust, number in TypeScript).
```

**Borrowed References (Rust):**
```y
Define a function called print_name that takes name as borrowed text (Yummy: &str).
  Print name.
```

**Lifetimes (Rust):**
```y
Define a function called longest (Yummy: with lifetime 'a)
  that takes first as borrowed text (Yummy: &'a str)
  and second as borrowed text (Yummy: &'a str)
  and returns borrowed text (Yummy: &'a str).
```

## Common Patterns

### Pattern 1: Builder Pattern

```y
Define a structure called UserBuilder with fields:
  - name as optional text
  - email as optional text

Implement methods for UserBuilder:
  Define a method called new that returns UserBuilder.
    Create builder with empty fields.
    Return builder.

  Define a method called with_name that takes n as text and returns UserBuilder (Yummy: self).
    Set name to n.
    Return self.

  Define a method called build that returns User.
    Create user with name and email.
    Return user.
```

### Pattern 2: Result Type

```y
Define a function called divide that takes a as number and b as number
  and returns either number or error text (Yummy: Result<f64, String>).
  If b equals 0:
    Return error "Cannot divide by zero".
  Otherwise:
    Return success with a divided by b.
```

### Pattern 3: Option Type

```y
Define a function called find_user that takes id as number
  and returns optional User (Yummy: Option<User>).
  For each user in users:
    If user's id equals id:
      Return user (Yummy: Some(user)).
  Return nothing (Yummy: None).
```

## Best Practices

1. **Use Descriptive Names**: Make variable and function names clear
   ```y
   Define a function called calculate_total_price that takes items as list...
   ```

2. **Add Yummy Annotations for Types**: Be explicit about number types
   ```y
   Create a variable called count with value 0 (Yummy: i32).
   ```

3. **Document Complex Logic**: Use natural language to explain
   ```y
   Define a function called process_batch that takes items as list.
     For each item in items where item is not processed:
       Process the item and mark as complete.
   ```

4. **Handle Errors Properly**: Use try-catch for operations that might fail
   ```y
   Try:
     Read file contents.
   Catch error:
     Print error and return default value.
   ```

## Troubleshooting

### Issue: Server won't start

**Solution**: Check if port 8080 is already in use
```bash
lsof -i :8080
# Kill the process or use a different port
```

### Issue: Compilation errors

**Solution**: Check your Y language syntax
- Ensure proper indentation (2 or 4 spaces)
- Check keyword spelling
- Verify Yummy annotations are properly formatted

### Issue: Maven build fails

**Solution**: Ensure you have Java 17+
```bash
java -version
# Should show Java 17 or higher
```

## Getting Help

- Read the [Language Specification](Y_LANGUAGE_SPEC.md)
- Check [Examples](examples/)
- Review [README](README.md)
- Open an issue on GitHub

## Next Steps

1. Try the examples in the `examples/` directory
2. Experiment with different language features
3. Build your own Y programs
4. Contribute to the project

Happy coding with Y Language!
