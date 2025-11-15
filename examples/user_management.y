Define a structure called User with fields:
  - id as number (Yummy: u64)
  - username as text
  - email as text
  - is_active as truth value

Define a structure called UserManager (Yummy: generic with lifetime 'a) with fields:
  - users as list of User (Yummy: Vec<User>)

Implement methods for UserManager:
  Define a method called new that returns UserManager.
    Create an empty list of users.
    Return a new UserManager with the empty users list.

  Define a method called add_user that takes a user as User and returns nothing (Yummy: &mut self).
    Add user to the users list.

  Define a method called find_by_username that takes username as borrowed text (Yummy: &str)
    and returns optional borrowed User (Yummy: Option<&User>).
    For each user in users:
      If user's username equals username:
        Return the user (Yummy: Some(user)).
    Return nothing (Yummy: None).

  Define a method called activate_user that takes username as borrowed text (Yummy: &str)
    and returns either truth value or error text (Yummy: Result<bool, String>).
    For each user in users (Yummy: &mut self.users):
      If user's username equals username:
        Set user's is_active to true.
        Return success with true.
    Return error "User not found".

Define a function called main that returns nothing.
  Create a mutable UserManager called manager using new method.

  Create a User called alice with:
    - id as 1
    - username as "alice"
    - email as "alice@example.com"
    - is_active as false

  Add alice to manager.

  Try:
    Activate user "alice" in manager and wait for result.
    Print "User alice activated successfully".
  Catch error:
    Print "Failed to activate user" with error message.
