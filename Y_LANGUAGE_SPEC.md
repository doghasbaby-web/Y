# Y Language Specification v1.0

## Overview

Y Language is a human-readable intermediate programming language designed to bridge natural English and existing programming languages. It serves as a universal intermediate representation that can be compiled to Rust, TypeScript, and other languages.

## Design Principles

1. **Human Readability**: Code reads like natural English
2. **Unambiguous Translation**: Different LLMs produce nearly identical Y code from the same English
3. **Complete Feature Coverage**: Supports all features from Rust and TypeScript
4. **Bidirectional Mapping**: Exact 1:1 mapping between Y ↔ Rust and Y ↔ TypeScript
5. **Developer Annotations**: Technical specifications in `(Yummy: ...)` blocks

## Syntax

### Basic Structure

```y
Define a function called greet that takes a name as text and returns text.
  Create a greeting by combining "Hello, " with name and "!".
  Return the greeting.
```

### Developer Annotations (Yummy Blocks)

For technical specifications that require precise typing or implementation details:

```y
Define a function called process (Yummy: generic function with type parameter T where T implements Display trait)
  that takes an item (Yummy: type T) and returns nothing.
  Print the item to console.
```

## Language Constructs

### 1. Variables and Constants

**Mutable Variable:**
```y
Create a mutable variable called counter with initial value 0 (Yummy: i32).
```

**Immutable Variable:**
```y
Create a variable called max_size with value 100 (Yummy: const i32).
```

**Type Inference:**
```y
Create a variable called message with value "Hello".
```

### 2. Data Types

**Primitive Types:**
- `number` - integer or float
- `text` - string
- `truth value` - boolean
- `nothing` - void/unit type

**Collections:**
```y
Create a list of numbers called scores with values 85, 90, 78.
Create a map from text to number called ages.
Create a set of text called unique_names.
```

**Custom Types:**
```y
Define a structure called Person with fields:
  - name as text
  - age as number
  - email as optional text (Yummy: Option<String> in Rust, string | null in TypeScript)
```

### 3. Functions

**Basic Function:**
```y
Define a function called add that takes x as number and y as number and returns number.
  Calculate result as x plus y.
  Return result.
```

**Generic Function:**
```y
Define a function called find_max (Yummy: generic with type T where T implements Ord)
  that takes a list of items (Yummy: Vec<T>) and returns optional item (Yummy: Option<T>).
  If the list is empty:
    Return nothing (Yummy: None).
  Otherwise:
    Create a variable called max with the first item.
    For each item in the list:
      If item is greater than max:
        Set max to item.
    Return max (Yummy: Some(max)).
```

**Async Function:**
```y
Define an async function called fetch_data that takes a url as text and returns text (Yummy: async/await, returns Future<String> in Rust, Promise<string> in TypeScript).
  Send a GET request to url and wait for response.
  Return the response body as text.
```

### 4. Control Flow

**Conditionals:**
```y
If temperature is greater than 30:
  Print "It's hot!".
Otherwise if temperature is greater than 20:
  Print "It's warm.".
Otherwise:
  Print "It's cold.".
```

**Loops:**
```y
For each number in numbers:
  Print the number.

While counter is less than 10:
  Increment counter by 1.

Loop indefinitely:
  If should_stop:
    Break the loop.
```

**Pattern Matching:**
```y
Match the value against:
  Case it equals 0:
    Print "Zero".
  Case it equals 1:
    Print "One".
  Case anything else:
    Print "Other".
```

### 5. Error Handling

**Result Type:**
```y
Define a function called divide that takes a as number and b as number
  and returns either number or error text (Yummy: Result<f64, String>).
  If b equals 0:
    Return error "Cannot divide by zero".
  Otherwise:
    Return success with a divided by b.
```

**Try-Catch:**
```y
Try:
  Attempt to open file "data.txt".
  Read the contents.
Catch error:
  Print "Failed to read file" with error message.
```

### 6. Object-Oriented Features

**Classes/Structs:**
```y
Define a structure called Rectangle with fields:
  - width as number
  - height as number

Implement methods for Rectangle:
  Define a method called area that returns number.
    Return width multiplied by height.

  Define a method called scale that takes factor as number and returns nothing (Yummy: &mut self).
    Set width to width multiplied by factor.
    Set height to height multiplied by factor.
```

**Traits/Interfaces:**
```y
Define a trait called Drawable (Yummy: trait in Rust, interface in TypeScript) with methods:
  - draw that returns nothing
  - get_position that returns a pair of numbers (Yummy: (f64, f64))

Implement Drawable for Circle:
  Define method draw.
    Print "Drawing circle at" with position.

  Define method get_position.
    Return the x and y coordinates as a pair.
```

### 7. Ownership and Borrowing (Rust-specific)

```y
Define a function called process_data that takes data as borrowed text (Yummy: &str) and returns number.
  Return the length of data.

Define a function called take_ownership that takes data as owned text (Yummy: String) and returns nothing.
  Print data.
  (Yummy: data is moved here and dropped at end of function)
```

### 8. Advanced Types

**Enumerations:**
```y
Define an enumeration called Color with variants:
  - Red
  - Green
  - Blue
  - Custom with red as number, green as number, blue as number
```

**Union Types:**
```y
Create a type called StringOrNumber that is either text or number (Yummy: String | number in TypeScript, enum in Rust).
```

**Tuples:**
```y
Create a variable called point as a tuple of two numbers (Yummy: (f64, f64)).
Set point to (3.5, 7.2).
```

### 9. Modules and Imports

```y
Import the math module.
Import the random_number function from utilities module.

Export the following:
  - process function
  - DataType structure
```

### 10. Lifetime Annotations (Rust-specific)

```y
Define a function called longest (Yummy: with lifetime 'a)
  that takes first as borrowed text (Yummy: &'a str)
  and second as borrowed text (Yummy: &'a str)
  and returns borrowed text (Yummy: &'a str).
  If length of first is greater than length of second:
    Return first.
  Otherwise:
    Return second.
```

### 11. Decorators/Attributes

```y
(Yummy: #[derive(Debug, Clone)] in Rust, decorator in TypeScript)
Define a structure called User with auto-derived debug and clone capabilities.
```

### 12. Concurrency

```y
Spawn a new thread that runs:
  Print "Hello from thread!".
  Sleep for 1 second.

Create a channel for sending numbers (Yummy: mpsc::channel in Rust).
Send value 42 through the channel.
Receive the value from the channel.
```

## Keywords Reference

### Declaration Keywords
- `Define` - declare functions, types, structures
- `Create` - create variables, instances
- `Implement` - implement traits/interfaces for types

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

### Special Keywords
- `Try`, `Catch`
- `Import`, `Export`
- `Spawn`, `Wait`

## Translation Examples

### Y to TypeScript

**Y Code:**
```y
Define a function called fibonacci that takes n as number and returns number.
  If n is less than or equal to 1:
    Return n.
  Otherwise:
    Return fibonacci(n minus 1) plus fibonacci(n minus 2).
```

**TypeScript Output:**
```typescript
function fibonacci(n: number): number {
  if (n <= 1) {
    return n;
  } else {
    return fibonacci(n - 1) + fibonacci(n - 2);
  }
}
```

### Y to Rust

**Y Code:**
```y
Define a structure called Point with fields:
  - x as number (Yummy: f64)
  - y as number (Yummy: f64)

Implement methods for Point:
  Define a method called distance_from_origin that returns number (Yummy: f64).
    Return square root of (x multiplied by x plus y multiplied by y).
```

**Rust Output:**
```rust
struct Point {
    x: f64,
    y: f64,
}

impl Point {
    fn distance_from_origin(&self) -> f64 {
        (self.x * self.x + self.y * self.y).sqrt()
    }
}
```

## Grammar Rules

1. **Line-based**: Each statement is typically one line
2. **Indentation**: Indicates scope (like Python)
3. **Natural ordering**: Subject-verb-object when possible
4. **Explicit types**: Can be inferred or specified
5. **Yummy annotations**: Always in parentheses, start with "Yummy:"

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
| Error Handling | ✓ | Result/Option | try/catch |

## Version History

- **v1.0** - Initial specification (2025-11-15)
