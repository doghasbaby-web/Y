Import the http module.
Import the json module.

Define a structure called ApiResponse with fields:
  - status as number
  - data as text
  - success as truth value

Define an async function called fetch_user_data that takes user_id as number
  and returns either ApiResponse or error text (Yummy: Result<ApiResponse, String> in Rust, Promise<ApiResponse> throws Error in TypeScript).
  Create a variable called url by combining "https://api.example.com/users/" with user_id.

  Try:
    Send a GET request to url and wait for response.

    If response status is 200:
      Parse response body as text and wait for result.
      Create an ApiResponse with:
        - status as response status
        - data as parsed body
        - success as true
      Return success with the ApiResponse.
    Otherwise:
      Return error "HTTP request failed with status" with response status.
  Catch error:
    Return error "Network error" with error message.

Define an async function called process_multiple_users that takes user_ids as list of number
  and returns list of ApiResponse (Yummy: async, returns Future<Vec<ApiResponse>> in Rust, Promise<ApiResponse[]> in TypeScript).
  Create an empty list called results.

  For each id in user_ids:
    Try:
      Fetch user data for id and wait for result.
      Add result to results list.
    Catch error:
      Print "Failed to fetch user" with id with "error:" with error message.

  Return results.

Define an async function called main that returns nothing.
  Create a list of numbers called ids with values 1, 2, 3, 4, 5.

  Fetch and process multiple users with ids and wait for responses.

  For each response in responses:
    If response's success is true:
      Print "User data:" with response's data.
    Otherwise:
      Print "Failed with status" with response's status.
