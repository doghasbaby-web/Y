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

**Closures/Lambda Functions:**
```y
Create a closure called add_ten that takes x as number and returns number.
  Return x plus 10.

(Yummy: |x| x + 10 in Rust, (x) => x + 10 in TypeScript)

Create a closure called process (Yummy: captures environment by reference)
  that takes no parameters and returns nothing.
  Print the value of outer_variable.
```

**Function Overloading (TypeScript):**
```y
Define a function called format that can:
  - Take value as number and return text
  - Take value as text and return text
(Yummy: TypeScript function overloading)
  Match value type:
    Case number:
      Return value converted to text with 2 decimal places.
    Case text:
      Return value in uppercase.
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

**Classes/Structs with Constructors:**
```y
Define a structure called Rectangle with fields:
  - width as number
  - height as number

Implement methods for Rectangle:
  Define a constructor called new that takes w as number and h as number.
    Set width to w.
    Set height to h.
    Return the new instance.

  Define a method called area that returns number.
    Return width multiplied by height.

  Define a method called scale that takes factor as number and returns nothing (Yummy: &mut self).
    Set width to width multiplied by factor.
    Set height to height multiplied by factor.
```

**Access Modifiers (TypeScript):**
```y
Define a class called BankAccount with:
  - private field balance as number
  - public field owner as text
  - protected field account_number as text

Implement methods for BankAccount:
  Define a public method called deposit that takes amount as number.
    Set balance to balance plus amount.

  Define a private method called validate_transaction that returns truth value.
    Return truth if balance is greater than 0.
```

**Static Members:**
```y
Define a structure called MathUtils.

Implement static methods for MathUtils:
  Define a static method called pi that returns number.
    Return 3.14159265359.

  Define a static field called version with value "1.0".
```

**Abstract Classes (TypeScript):**
```y
Define an abstract class called Shape (Yummy: TypeScript only).
  Define an abstract method called area that returns number.
  Define a method called describe that returns text.
    Return "This is a shape with area" combined with area().
```

**Getters and Setters:**
```y
Define a structure called Temperature with private field celsius as number.

Implement methods for Temperature:
  Define a getter called fahrenheit that returns number.
    Return celsius multiplied by 9 divided by 5 plus 32.

  Define a setter called fahrenheit that takes value as number.
    Set celsius to (value minus 32) multiplied by 5 divided by 9.
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

**Associated Types (Rust):**
```y
Define a trait called Container (Yummy: trait with associated type) with:
  - associated type called Item
  - method get that takes index as number and returns optional Item

Implement Container for IntVector:
  Set associated type Item to number (Yummy: i32).
  Define method get that takes index as number.
    If index is within bounds:
      Return the item at index.
    Otherwise:
      Return nothing.
```

**Default Trait Implementations:**
```y
Define a trait called Logger with methods:
  - log that takes message as text (Yummy: default implementation provided)
    Print "LOG:" combined with message.
  - error that takes message as text (requires implementation)

Implement Logger for FileLogger:
  Define method error.
    Write message to error log file.
  (Yummy: log method uses default implementation)
```

**Associated Constants:**
```y
Define a trait called Bounded with:
  - associated constant called MIN of type number
  - associated constant called MAX of type number

Implement Bounded for SignedByte:
  Set MIN to -128.
  Set MAX to 127.
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

**Intersection Types (TypeScript):**
```y
Create a type called Employee that combines Person and Worker (Yummy: Person & Worker in TypeScript).
Define a variable of type Employee that has all fields from both Person and Worker.
```

**Type Aliases:**
```y
Define a type alias called UserID as number (Yummy: type UserID = i32 in Rust, type UserID = number in TypeScript).
Define a type alias called Callback as a function that takes text and returns nothing.
```

**Tuples:**
```y
Create a variable called point as a tuple of two numbers (Yummy: (f64, f64)).
Set point to (3.5, 7.2).
```

**Type Guards and Type Assertions (TypeScript):**
```y
Define a function called is_string that takes value as any type and returns truth value (Yummy: type guard).
  Return whether value is of type text.

Create a variable called user_input as any type.
Assert that user_input is text (Yummy: as string in TypeScript).
Process user_input as text.
```

**Readonly Properties:**
```y
Define a structure called Config with:
  - readonly field app_name as text
  - readonly field version as number
(Yummy: readonly in TypeScript, no mut in Rust)
```

### 9. Destructuring

**Array/List Destructuring:**
```y
Create a list called numbers with values 1, 2, 3, 4, 5.
Destructure numbers into first, second, and rest_of_numbers.
(Yummy: let [first, second, ...rest] = numbers in TypeScript, let [first, second, rest @ ..] in Rust)
```

**Object/Struct Destructuring:**
```y
Create a variable called person with name "Alice" and age 30.
Destructure person into name and age variables.
(Yummy: const {name, age} = person in TypeScript, let Person {name, age} = person in Rust)

Destructure with renaming:
  Extract name as person_name and age as person_age from person.
```

**Pattern Destructuring in Function Parameters:**
```y
Define a function called print_point that takes point as tuple destructured into x and y.
  Print "Point at" with x and y.
(Yummy: function printPoint([x, y]: [number, number]) in TypeScript)
```

### 10. Iterators and Iterator Adapters

**Basic Iterator:**
```y
Create an iterator from numbers list.
For each item in the iterator:
  Process the item.
```

**Iterator Adapters:**
```y
Create a list called numbers with values 1, 2, 3, 4, 5.
Create a pipeline that:
  - Takes numbers as iterator
  - Maps each item to item multiplied by 2
  - Filters items greater than 5
  - Collects into a new list
(Yummy: numbers.iter().map(|x| x * 2).filter(|x| *x > 5).collect() in Rust)
(Yummy: numbers.map(x => x * 2).filter(x => x > 5) in TypeScript)
```

**Common Iterator Methods:**
```y
From numbers iterator:
  - Map each item with a transformation
  - Filter items by condition
  - Reduce to single value with accumulator
  - Take first n items
  - Skip first n items
  - Find first item matching condition
  - Check if all items match condition
  - Check if any item matches condition
```

### 11. Smart Pointers (Rust)

**Box - Heap Allocation:**
```y
Create a boxed value of recursive type (Yummy: Box<T> in Rust).
Define an enumeration called TreeNode with variants:
  - Leaf with value as number
  - Branch with left as boxed TreeNode and right as boxed TreeNode
```

**Reference Counted Pointers:**
```y
Create a reference-counted pointer to data (Yummy: Rc<T> for single-threaded, Arc<T> for multi-threaded).
Create multiple references to the same data.
When all references are dropped, data is deallocated automatically.
```

**Interior Mutability:**
```y
Create a cell that allows mutation through shared reference (Yummy: RefCell<T>).
Borrow the cell mutably to modify its contents.
(Yummy: Allows runtime borrow checking instead of compile-time)
```

### 12. Macros (Rust)

**Declarative Macros:**
```y
Define a macro called create_function (Yummy: macro_rules! in Rust).
  That generates a function with given name and body.
(Yummy: Macros are compile-time code generation)

Use the macro to create multiple similar functions.
```

**Procedural Macros:**
```y
Define a procedural macro called auto_debug (Yummy: #[proc_macro_derive(AutoDebug)]).
  That automatically implements debug printing for structures.
```

### 13. Advanced Operators

**Rest and Spread Operators:**
```y
Define a function called sum that takes rest parameters as numbers (Yummy: ...numbers in TypeScript/JavaScript).
  Sum all the numbers and return result.

Create a new array by spreading existing arrays (Yummy: [...array1, ...array2]).
Create a new object by spreading existing objects (Yummy: {...obj1, ...obj2}).
```

**Optional Chaining (TypeScript):**
```y
Access nested property safely using optional chaining.
Get user?.address?.street (Yummy: returns undefined if any part is null/undefined).
```

**Nullish Coalescing (TypeScript):**
```y
Create a variable called display_name with value name ?? "Anonymous".
(Yummy: ?? operator returns right side only if left is null/undefined, not for other falsy values)
```

**Range Expressions (Rust):**
```y
Create a range from 0 to 10 (Yummy: 0..10 exclusive end, 0..=10 inclusive end).
For each number in range 1 to 100:
  Process the number.
```

**Operator Overloading (Rust):**
```y
Implement addition operator for Point structure (Yummy: impl Add for Point).
Define how to add two Points together.
  Return new Point with summed coordinates.
```

### 14. Modules and Imports

```y
Import the math module.
Import the random_number function from utilities module.

Export the following:
  - process function
  - DataType structure
```

**Namespaces (TypeScript):**
```y
Define a namespace called Utilities (Yummy: namespace in TypeScript).
  Define a function called helper.
  Define a constant called VERSION.

Access namespace members:
  Call Utilities.helper().
```

**Index Signatures (TypeScript):**
```y
Define an interface called StringMap (Yummy: TypeScript).
  With index signature: any text key maps to text value.
  (Yummy: [key: string]: string)

Create a variable of type StringMap.
Set any_key to any_value.
```

### 15. Lifetime Annotations (Rust-specific)

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

### 16. Decorators/Attributes

```y
(Yummy: #[derive(Debug, Clone)] in Rust, decorator in TypeScript)
Define a structure called User with auto-derived debug and clone capabilities.
```

### 17. Unsafe and Low-Level Features (Rust)

**Unsafe Blocks:**
```y
Execute unsafe code block (Yummy: unsafe {} in Rust):
  Dereference a raw pointer.
  Call an unsafe function.
  Access mutable static variable.
  Implement unsafe trait.
(Yummy: Bypasses Rust's safety guarantees - use with caution)
```

**Raw Pointers:**
```y
Create a raw pointer to value (Yummy: *const T or *mut T).
In unsafe block:
  Dereference the raw pointer to access value.
```

**Foreign Function Interface:**
```y
Declare external function from C library (Yummy: extern "C").
Define a function called external_sqrt that takes number and returns number.

Call the external function in unsafe block.
```

### 18. Const and Static

**Const Functions (Rust):**
```y
Define a const function called compute_value (Yummy: const fn in Rust).
  That can be evaluated at compile time.
  Return a computed constant value.
```

**Const Generics (Rust):**
```y
Define a structure called FixedArray (Yummy: generic over const N: usize).
  With field data as array of N numbers.
Create an instance with size known at compile time.
```

**Static Variables:**
```y
Define a static variable called GLOBAL_CONFIG with value "production" (Yummy: static in both languages).
(Yummy: Lives for entire program duration, single memory location)

Define a mutable static variable called COUNTER (Yummy: static mut in Rust).
Access mutable static in unsafe block.
```

### 19. Advanced Features

**Drop and Destructors (Rust):**
```y
Implement Drop trait for Resource structure.
  Define method called drop that runs when value goes out of scope.
    Clean up resources.
    Close file handles.
(Yummy: Automatic cleanup when ownership ends)
```

**Deref Trait (Rust):**
```y
Implement Deref trait for SmartPointer.
  Define how to dereference the pointer.
  Enable automatic dereferencing coercion.
```

**Generators (TypeScript/JavaScript):**
```y
Define a generator function called fibonacci_generator (Yummy: function* in TypeScript).
  Yield 0.
  Yield 1.
  Loop indefinitely:
    Yield next Fibonacci number.

Create an iterator from the generator.
```

**Symbols (TypeScript):**
```y
Create a unique symbol called id (Yummy: Symbol() in TypeScript).
Use symbol as object property key.
(Yummy: Symbols are unique and immutable)
```

**PhantomData (Rust):**
```y
Define a structure called Wrapper (Yummy: with PhantomData<T>).
  That acts as if it owns type T without actually storing it.
(Yummy: Used for lifetime and type parameter variance)
```

### 20. Concurrency

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
- `Define` - declare functions, types, structures, traits, macros
- `Create` - create variables, instances, closures
- `Implement` - implement traits/interfaces for types
- `Declare` - declare external functions, modules

### Type Keywords
- `number`, `text`, `truth value`, `nothing`
- `list`, `map`, `set`, `optional`
- `mutable`, `borrowed`, `owned`
- `boxed`, `reference-counted`
- `readonly`, `private`, `public`, `protected`
- `static`, `const`, `abstract`

### Control Flow Keywords
- `If`, `Otherwise if`, `Otherwise`
- `For each`, `While`, `Loop`
- `Match`, `Case`
- `Return`, `Break`, `Continue`
- `Yield` - for generators

### Function Keywords
- `takes`, `returns`, `and`
- `async`, `await`
- `closure`, `lambda`
- `constructor`, `method`
- `getter`, `setter`

### Operation Keywords
- `plus`, `minus`, `multiplied by`, `divided by`
- `equals`, `is greater than`, `is less than`
- `and`, `or`, `not`
- `spread`, `rest`
- `range from`, `range to`

### Pattern Keywords
- `Destructure` - destructure arrays/objects
- `Extract` - extract values with renaming

### Special Keywords
- `Try`, `Catch`
- `Import`, `Export`
- `Spawn`, `Wait`
- `Unsafe` - unsafe operations
- `Yummy` - technical annotations

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
| Closures/Lambdas | ✓ | ✓ | ✓ |
| Generics | ✓ | ✓ | ✓ |
| Traits/Interfaces | ✓ | ✓ | ✓ |
| Associated Types | ✓ | ✓ | - |
| Pattern Matching | ✓ | ✓ | ✓ |
| Destructuring | ✓ | ✓ | ✓ |
| Async/Await | ✓ | ✓ | ✓ |
| Iterators | ✓ | ✓ | ✓ |
| Ownership | ✓ | ✓ | - |
| Lifetimes | ✓ | ✓ | - |
| Smart Pointers | ✓ | ✓ | - |
| Union Types | ✓ | enum | ✓ |
| Intersection Types | ✓ | - | ✓ |
| Type Aliases | ✓ | ✓ | ✓ |
| Error Handling | ✓ | Result/Option | try/catch |
| Macros | ✓ | ✓ | - |
| Constructors | ✓ | ✓ | ✓ |
| Access Modifiers | ✓ | pub/private | ✓ |
| Static Members | ✓ | ✓ | ✓ |
| Abstract Classes | ✓ | - | ✓ |
| Getters/Setters | ✓ | - | ✓ |
| Optional Chaining | ✓ | - | ✓ |
| Operator Overloading | ✓ | ✓ | - |
| Unsafe Code | ✓ | ✓ | - |
| Const Generics | ✓ | ✓ | - |
| Generators | ✓ | - | ✓ |
| Symbols | ✓ | - | ✓ |
| Namespaces | ✓ | mod | ✓ |

## Summary of New Features Added

This specification now includes comprehensive coverage of TypeScript and Rust features:

**TypeScript-specific features:**
- Closures/arrow functions
- Function overloading
- Access modifiers (public, private, protected)
- Abstract classes
- Getters and setters
- Intersection types
- Type guards and assertions
- Readonly properties
- Optional chaining and nullish coalescing
- Rest and spread operators
- Namespaces
- Index signatures
- Generators
- Symbols

**Rust-specific features:**
- Closures with capture modes
- Associated types and constants
- Default trait implementations
- Smart pointers (Box, Rc, Arc, RefCell)
- Macros (declarative and procedural)
- Operator overloading
- Unsafe blocks and raw pointers
- Foreign Function Interface (FFI)
- Const functions and const generics
- Static variables
- Drop trait and destructors
- Deref trait
- PhantomData
- Range expressions

**Features in both languages:**
- Destructuring (arrays, objects, function parameters)
- Iterators and iterator adapters
- Type aliases
- Constructors
- Static members

## Version History

- **v1.1** - Added comprehensive TypeScript and Rust features (2025-11-15)
  - Closures and lambda functions
  - Destructuring patterns
  - Iterators and adapters
  - Smart pointers (Rust)
  - Macros (Rust)
  - Advanced operators (spread, rest, optional chaining, ranges)
  - Access modifiers and OOP features
  - Unsafe and low-level features (Rust)
  - Const/static features
  - Generators and symbols (TypeScript)
  - Associated types and default implementations

- **v1.0** - Initial specification (2025-11-15)
