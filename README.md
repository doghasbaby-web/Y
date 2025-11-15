# Y Language - Human-Readable Intermediate Programming Language

![Y Language](https://img.shields.io/badge/Y-Language-blue)
![Version](https://img.shields.io/badge/version-1.0.0-green)
![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen)

Y Language is a revolutionary human-readable intermediate programming language that bridges natural English and existing programming languages. It serves as a universal intermediate representation that can be compiled to Rust, TypeScript, and other languages without any ambiguity.

## Table of Contents

- [Features](#features)
- [Design Principles](#design-principles)
- [Quick Start](#quick-start)
- [Language Syntax](#language-syntax)
- [API Documentation](#api-documentation)
- [Examples](#examples)
- [Building and Running](#building-and-running)
- [Testing](#testing)
- [Architecture](#architecture)
- [Contributing](#contributing)
- [License](#license)

## Features

- **Human Readable**: Code reads like natural English
- **Unambiguous Translation**: Different LLMs produce nearly identical Y code from the same English description
- **Complete Feature Coverage**: Supports all features from both Rust and TypeScript
- **Bidirectional Mapping**: Exact 1:1 mapping between Y ↔ Rust and Y ↔ TypeScript
- **Developer Annotations**: Technical specifications via `(Yummy: ...)` blocks for precise type information
- **REST API**: Spring Boot-based REST API for compilation services
- **Production Ready**: Comprehensive test coverage and error handling

## Design Principles

1. **Human Readability**: Code reads like natural English sentences
2. **Unambiguous Translation**: Consistent output across different AI models
3. **Complete Feature Coverage**: All Rust and TypeScript features supported
4. **Bidirectional Mapping**: Perfect translation in both directions
5. **Developer Control**: Yummy annotations for technical precision

## Quick Start

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- Git

### Installation

```bash
# Clone the repository
git clone https://github.com/doghasbaby-web/Y.git
cd Y

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The server will start on `http://localhost:8080/api`

### Your First Y Program

Create a file `hello.y`:

```y
Define a function called main that returns nothing.
  Print "Hello, World!" to console.
```

Compile it via API:

```bash
curl -X POST http://localhost:8080/api/compile/typescript \
  -H "Content-Type: text/plain" \
  -d 'Define a function called main that returns nothing.
  Print "Hello, World!" to console.'
```

## Language Syntax

### Basic Function

```y
Define a function called greet that takes name as text and returns text.
  Create a greeting by combining "Hello, " with name and "!".
  Return the greeting.
```

**TypeScript Output:**
```typescript
function greet(name: string): string {
  const greeting = "Hello, " + name + "!";
  return greeting;
}
```

**Rust Output:**
```rust
fn greet(name: String) -> String {
    let greeting = format!("Hello, {}", name);
    return greeting;
}
```

### Struct/Interface Definition

```y
Define a structure called Person with fields:
  - name as text
  - age as number (Yummy: u32 in Rust)
  - email as optional text
```

**TypeScript Output:**
```typescript
interface Person {
  name: string;
  age: number;
  email: string | null;
}
```

**Rust Output:**
```rust
struct Person {
    name: String,
    age: u32,
    email: Option<String>,
}
```

### Generic Function

```y
Define a function called find_max (Yummy: generic with type T where T implements Ord)
  that takes a list of items (Yummy: Vec<T>) and returns optional item (Yummy: Option<T>).
  If the list is empty:
    Return nothing (Yummy: None).
  Otherwise:
    Return the first item.
```

### Async Function

```y
Define an async function called fetch_data that takes url as text and returns text.
  Send a GET request to url and wait for response.
  Return the response body as text.
```

**TypeScript Output:**
```typescript
async function fetch_data(url: string): Promise<string> {
  // ... implementation
}
```

**Rust Output:**
```rust
async fn fetch_data(url: String) -> impl Future<Output = String> {
    // ... implementation
}
```

### Control Flow

```y
If temperature is greater than 30:
  Print "It's hot!".
Otherwise if temperature is greater than 20:
  Print "It's warm.".
Otherwise:
  Print "It's cold.".

For each number in numbers:
  Print the number.

While counter is less than 10:
  Increment counter by 1.
```

### Error Handling

```y
Try:
  Attempt to open file "data.txt".
  Read the contents.
Catch error:
  Print "Failed to read file" with error message.
```

## API Documentation

### Endpoints

#### 1. Compile to Specific Language

**POST** `/api/compile`

Request Body:
```json
{
  "sourceCode": "Define a function called test that returns nothing.\n  Print \"Hello\".",
  "targetLanguage": "TYPESCRIPT"
}
```

Response:
```json
{
  "success": true,
  "compiledCode": "function test(): void {\n  console.log(\"Hello\");\n}",
  "errorMessage": null,
  "targetLanguage": "TYPESCRIPT"
}
```

#### 2. Compile to TypeScript

**POST** `/api/compile/typescript`

Request Body (plain text):
```
Define a function called greet that takes name as text and returns text.
  Return "Hello".
```

#### 3. Compile to Rust

**POST** `/api/compile/rust`

Request Body (plain text):
```
Define a function called add that takes x as number and y as number and returns number.
  Return x.
```

#### 4. Health Check

**GET** `/api/compile/health`

Response:
```
Y Language Compiler is running
```

## Examples

See the [examples/](examples/) directory for comprehensive examples:

- `hello_world.y` - Simple hello world program
- `fibonacci.y` - Fibonacci number calculator
- `user_management.y` - User management system with structs and methods
- `generic_container.y` - Generic container implementation
- `async_fetch.y` - Async HTTP request handler

## Building and Running

### Development Mode

```bash
mvn spring-boot:run
```

### Production Build

```bash
mvn clean package
java -jar target/y-language-compiler-1.0.0.jar
```

### Docker

```bash
docker build -t y-language .
docker run -p 8080:8080 y-language
```

## Testing

Run all tests:

```bash
mvn test
```

Run specific test suite:

```bash
mvn test -Dtest=LexerTest
mvn test -Dtest=TypeScriptCompilerTest
mvn test -Dtest=RustCompilerTest
```

Test coverage includes:
- Lexer tests (tokenization)
- Parser tests (AST generation)
- TypeScript compiler tests
- Rust compiler tests
- Integration tests
- Service layer tests

## Architecture

### Components

```
┌─────────────────────────────────────────────┐
│           REST API Controller               │
│         (Spring Boot Controller)            │
└─────────────┬───────────────────────────────┘
              │
              ▼
┌─────────────────────────────────────────────┐
│         Compilation Service                 │
│    (Orchestrates compilation process)       │
└─────────────┬───────────────────────────────┘
              │
    ┌─────────┴──────────┬────────────────┐
    ▼                    ▼                ▼
┌────────┐         ┌──────────┐     ┌──────────┐
│ Lexer  │────────▶│  Parser  │────▶│Compilers │
│(Tokens)│         │  (AST)   │     │ TS/Rust  │
└────────┘         └──────────┘     └──────────┘
```

### Package Structure

```
com.ylang/
├── YLanguageApplication.java     # Main Spring Boot application
├── lexer/
│   ├── Lexer.java                # Tokenizer
│   ├── Token.java                # Token representation
│   └── TokenType.java            # Token types enum
├── parser/
│   └── Parser.java               # Parser (tokens → AST)
├── ast/
│   ├── ASTNode.java              # Base AST interface
│   ├── ASTVisitor.java           # Visitor pattern interface
│   ├── ProgramNode.java          # Program root node
│   ├── FunctionDeclarationNode.java
│   ├── StructDeclarationNode.java
│   └── ...                       # Other AST nodes
├── compiler/
│   ├── TypeScriptCompiler.java  # Y → TypeScript compiler
│   └── RustCompiler.java        # Y → Rust compiler
├── service/
│   └── CompilationService.java  # Business logic
├── controller/
│   └── CompilationController.java # REST endpoints
├── model/
│   ├── CompilationRequest.java
│   └── CompilationResponse.java
└── exception/
    └── GlobalExceptionHandler.java
```

## Language Keywords

### Declaration Keywords
- `Define` - declare functions, types, structures
- `Create` - create variables, instances
- `Implement` - implement traits/interfaces

### Type Keywords
- `number`, `text`, `truth value`, `nothing`
- `list`, `map`, `set`, `optional`
- `mutable`, `borrowed`, `owned`

### Control Flow Keywords
- `If`, `Otherwise if`, `Otherwise`
- `For each`, `While`, `Loop`
- `Match`, `Case`
- `Return`, `Break`, `Continue`

### Function Keywords
- `takes`, `returns`, `and`
- `async`, `await`

### Operation Keywords
- `plus`, `minus`, `multiplied by`, `divided by`
- `equals`, `is greater than`, `is less than`
- `and`, `or`, `not`

## Yummy Annotations

Yummy annotations provide precise technical specifications for developers:

```y
Define a function called process (Yummy: generic function with type parameter T where T implements Display trait)
  that takes an item (Yummy: type T) and returns nothing.
  Print the item to console.
```

The `(Yummy: ...)` blocks are used for:
- Generic type parameters
- Lifetime annotations (Rust)
- Trait bounds (Rust)
- Specific numeric types (i32, u64, f64, etc.)
- Ownership and borrowing specifications

## Compatibility Matrix

| Feature | Y Language | Rust | TypeScript |
|---------|-----------|------|------------|
| Variables | ✓ | ✓ | ✓ |
| Functions | ✓ | ✓ | ✓ |
| Generics | ✓ | ✓ | ✓ |
| Traits/Interfaces | ✓ | ✓ | ✓ |
| Pattern Matching | ✓ | ✓ | ✓ |
| Async/Await | ✓ | ✓ | ✓ |
| Ownership | ✓ | ✓ | - |
| Lifetimes | ✓ | ✓ | - |
| Union Types | ✓ | enum | ✓ |
| Error Handling | ✓ | Result | try/catch |

## Future Roadmap

- [ ] Python compiler
- [ ] Java compiler
- [ ] C/C++ compiler
- [ ] Go compiler
- [ ] WebAssembly target
- [ ] CLI tool for batch compilation
- [ ] VS Code extension
- [ ] Online playground
- [ ] Package manager integration

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Authors

- **Y Language Team** - *Initial work*

## Acknowledgments

- Inspired by the need for a universal intermediate language
- Built with Spring Boot and Java
- Special thanks to the Rust and TypeScript communities

## Contact

- GitHub: [@doghasbaby-web/Y](https://github.com/doghasbaby-web/Y)
- Issues: [GitHub Issues](https://github.com/doghasbaby-web/Y/issues)

## Version History

- **v1.0.0** (2025-11-15)
  - Initial release
  - TypeScript compiler
  - Rust compiler
  - REST API
  - Comprehensive test suite
  - Complete documentation

---

**Made with ❤️ by the Y Language Team**
