Define a structure called Container (Yummy: generic with type parameter T) with fields:
  - value as item (Yummy: T)

Implement methods for Container (Yummy: <T>):
  Define a method called new that takes val as item (Yummy: T) and returns Container (Yummy: Container<T>).
    Return a new Container with value set to val.

  Define a method called get that returns borrowed item (Yummy: &T).
    Return a reference to value.

  Define a method called set that takes new_value as item (Yummy: T) and returns nothing (Yummy: &mut self).
    Set value to new_value.

Define a trait called Displayable (Yummy: trait in Rust, interface in TypeScript) with methods:
  - display that returns text

Implement Displayable for Container (Yummy: <T> where T: std::fmt::Display):
  Define method display that returns text.
    Return text representation of value (Yummy: format!("{}", self.value)).

Define a function called main that returns nothing.
  Create a Container of number called int_container with value 42 (Yummy: Container<i32>).
  Print "Integer container contains" with int_container's get method result.

  Create a Container of text called str_container with value "Hello" (Yummy: Container<String>).
  Print "String container contains" with str_container's get method result.
