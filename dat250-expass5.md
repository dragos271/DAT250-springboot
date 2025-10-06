# DAT250 — Experiment Assignment 5: Redis Integration

## Overview
This experiment aimed to integrate **Redis** into the poll application to understand NoSQL basics and caching concepts. I:
- Installed and used Redis via CLI (`SET`, `GET`, `HSET`, `EXPIRE`, `TTL`).
- Connected Redis from Kotlin using **Jedis** and **Spring Data Redis**.
- Implemented a cache for poll results (`poll:<id>:results`), stored as Redis Hashes with TTL.

---

## Setup
**Dependencies (build.gradle.kts):**
```kotlin
implementation("redis.clients:jedis:6.2.0")
implementation("org.springframework.boot:spring-boot-starter-data-redis")
```
**Config:**
spring.data.redis.host=localhost
spring.data.redis.port=6379

**Start Redis:**
brew install redis
brew services start redis
redis-cli ping   # -> PONG

**Testing**
# Create poll and options
curl -X POST http://localhost:8080/polls -d '{"question":"Coffee or Tea?"}' -H "Content-Type: application/json"
curl -X POST http://localhost:8080/polls/1/options -d '{"caption":"Coffee","presentationOrder":0}' -H "Content-Type: application/json"
curl -X POST http://localhost:8080/polls/1/options -d '{"caption":"Tea","presentationOrder":1}' -H "Content-Type: application/json"

# Create user & vote
curl -X POST http://localhost:8080/users -d '{"username":"alice","email":"alice@example.com"}' -H "Content-Type: application/json"
curl -X POST "http://localhost:8080/votes?userId=1&optionId=O0"

# Get results & verify cache
curl http://localhost:8080/polls/1/results
redis-cli HGETALL poll:1:results

Issues & Fixes

Error: Used placeholder P in URLs → NumberFormatException.
Fix: Always use numeric IDs.

Zsh errors: Caused by \ and # comments → run one-liners only.

Cache invalidation: Added invalidate() in castVote().

**Conclusion**
✅ Redis connected
✅ Data stored and retrieved via CLI & Kotlin
✅ Poll results cached & auto-expire
✅ Votes update cache correctly
<img width="737" height="220" alt="Screenshot 2025-10-06 at 21 53 56" src="https://github.com/user-attachments/assets/2eef175e-4e83-49fd-b707-569c8964f911" />


Redis integration complete — caching works, and repeated results fetches are instant.
