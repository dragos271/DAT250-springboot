# Technical Challenges & Issues Report - Polling Application

## Overview

This document outlines the technical challenges encountered during the development of the polling application and the approaches taken to resolve them. It also highlights pending issues that may require further attention.

## Resolved Technical Challenges

### 1. CORS Configuration Issues

**Problem:** The frontend application was unable to communicate with the backend due to Cross-Origin Resource Sharing (CORS) restrictions, resulting in errors like:

Cross-Origin Request Blocked: The Same Origin Policy disallows reading the remote resource at http://localhost:8080/polls/1

**Solution:** Added the `@CrossOrigin(origins = ["*"])` annotation to the backend controllers:

```kotlin
@RestController
@RequestMapping("/votes")
@CrossOrigin(origins = ["*"])
class VoteController(private val pollManager: PollManager) {
    // Controller methods
}
```
2. Vote Recording Failures
Problem: Votes weren't being recorded despite successful API requests. The backend was returning success responses, but votes weren't appearing in the system.

Solution: Identified that the vote recording issue stemmed from missing user entities in the backend:
```javascript
// Added user creation before attempting to vote
async function createInitialUsers() {
  if (!usersCreated) {
    try {
      // Create our 3 predefined users
      for (const user of users) {
        const res = await fetch('http://localhost:8080/users', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            username: user.name,
            email: `${user.name.toLowerCase()}@example.com`
          })
        });
        if (res.ok) {
          console.log(`Created user: ${user.name}`);
        }
      }
      usersCreated = true;
    } catch (error) {
      console.error("Error creating users:", error);
    }
  }
}

// Update onMount to create users
onMount(async () => {
  await createInitialUsers();
  await loadPolls();
});
```
3. Poll Selection & State Management
Problem: After voting, the application would sometimes switch to a different poll than the one the user was interacting with.

Solution: Improved state management to preserve the currently selected poll when refreshing data:

```javascript
async function loadPolls() {
  try {
    const res = await fetch("http://localhost:8080/polls");
    if (res.ok) {
      polls = await res.json();
      // If we have polls, maintain the current selection
      if (polls.length > 0) {
        // Keep the currently selected poll if possible
        if (poll) {
          const currentPoll = polls.find(p => p.id === poll.id);
          if (currentPoll) {
            poll = currentPoll;
          } else {
            poll = polls[polls.length - 1];
          }
        } else {
          poll = polls[polls.length - 1];
        }
      }
    } else {
      polls = [];
    }
  } catch (error) {
    console.error("Error loading polls:", error);
    polls = [];
  }
}
```
## Pending Issues
1. In-Memory Data Persistence
Issue: The backend uses in-memory storage (mutableMapOf) for users, polls, and votes.
- Potential Solution: Implement JPA repositories.

2. Vote Validation
Issue: Users can currently vote multiple times for the same option.

- Potential Solution: Add vote uniqueness constraints.

### Conclusion
The polling application is functional for demonstrating the core features of creating polls and recording votes. However, for a production-ready application, addressing the pending issues would be necessary, particularly implementing persistent storage and proper authentication/authorization mechanisms.

The current implementation serves as a good foundation for understanding the interaction between a Svelte frontend and a Spring Boot backend, as well as the challenges involved in building a real-time interactive web application.


