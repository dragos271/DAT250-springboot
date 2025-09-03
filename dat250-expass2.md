For domain model the following classes were created: User, Poll, VoteOption, Vote.

- PollManager

In-memory storage with functions to create users, polls, add options, and cast votes.

- Controllers (REST endpoints)

• UserController → /users (create/list users).

° PollController →/polls (create/list polls, add options).

• VoteController → /votes (cast/list votes).

- Serialization fixes

Used @JsonManagedReference / @JsonBackReference to avoid infinite

JSON loops.
Manual testing

Used curl to confirm the whole flow works:

Create user → Create poll → Add options → Cast vote → Fetch results.

Verified JSON output is correct.

- Automated testing (started)

Added a first JUnit test with TestRestTemplate to check /users endpoint.

Confirmed we can run ./gradlew test and validate API behavior.
