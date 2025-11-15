Define a function called fibonacci that takes n as number (Yummy: i32) and returns number (Yummy: i32).
  If n is less than or equal to 1:
    Return n.
  Otherwise:
    Create a variable called prev with value 0.
    Create a variable called curr with value 1.
    For i from 2 to n:
      Create a variable called next with value prev plus curr.
      Set prev to curr.
      Set curr to next.
    Return curr.

Define a function called main that returns nothing.
  For i from 0 to 10:
    Print "Fibonacci of" with i with "is" with fibonacci(i).
