# DAT250 - Assignment 6: Redis and RabbitMQ Integration

## Technical Problems Encountered

### 1. Compilation Error - Misplaced Shell Command
Accidentally included `./gradlew bootRun` command inside the Kotlin source code at lines 86-87 of `pollManager.kt`, causing:
- "Expecting an element" error
- "Unresolved reference: bootRun" error

Solution: Removed the command from source code and executed it in terminal.

### 2. RabbitMQ Connection Refused
Application failed to connect to RabbitMQ at `localhost:5672` with `Connection refused` error. RabbitMQ was not running.

Solution: Installed and started RabbitMQ using Homebrew:
```bash
brew install rabbitmq
brew services start rabbitmq
```

### The @RabbitListener(queues = ["poll.*"]) implementation is problematic because:
- RabbitMQ requires explicit queue names, not patterns
- Queues are created dynamically when polls are created (poll.1, poll.2, etc.)
- Listener cannot automatically subscribe to queues that don't exist at startup
Potential solutions not yet implemented:
- Use single queue for all vote events
- Dynamically register listeners when creating polls
- Use RabbitMQ topic exchanges with routing patterns
- End-to-End Testing
- Asynchronous voting flow through RabbitMQ has not been fully tested due to the queue listener issue.


### Working Components
- Redis caching for poll results.
- Cache invalidation when options are added.
- Incremental cache updates on vote casting.
- RabbitMQ connection and queue creation.
- Message publishing to RabbitMQ queues.
REST API endpoints for users, polls, and votes

### Components Requiring Work
RabbitMQ listener with dynamic queue pattern.Complete asynchronous vote processing. Integration tests for full message flow.
