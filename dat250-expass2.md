# DAT250 – Experiment Assignment 2: Poll App Backend

## Overview

RESTful backend was implemented using **Spring Boot** and **Kotlin**. The application allows the creation and management of Users, Polls, VoteOptions, and Votes. Data is maintained in-memory through a `PollManager` component. Automated testing of the full workflow was also implemented using Spring's `TestRestTemplate`.

---

## Technical Challenges Encountered

1. **Controllers Not Detected (404 Errors)**
   - **Issue:** Endpoints such as `/polls` and `/users` returned 404 errors even though the controller classes were implemented.
   - **Resolution:** Ensured all controllers (`UserController`, `PollController`, `VoteController`) were located in the same package or a subpackage of the main `DemoApplication.kt` package (`com.example.demo`), allowing Spring Boot to detect and map them correctly.

2. **Infinite JSON Recursion**
   - **Issue:** Fetching polls with nested vote options and votes caused infinite serialization loops (`Poll → VoteOption → Vote → User → Poll`).
   - **Resolution:** Applied Jackson annotations `@JsonManagedReference` and `@JsonBackReference` to the model classes to break cycles and produce valid JSON.

3. **Missing Validation Provider**
   - **Issue:** Automated tests failed with `jakarta.validation.NoProviderFoundException`.
   - **Resolution:** Added the `spring-boot-starter-validation` dependency to `build.gradle.kts` to provide a Bean Validation implementation.

4. **Kotlin Data Classes Missing Default Values**
   - **Issue:** Jackson could not instantiate data classes during automated tests (`MissingKotlinParameterException`).
   - **Resolution:** Provided default values for all properties in `User`, `Poll`, `VoteOption`, and `Vote` to ensure JSON deserialization succeeds.

---

## Pending Issues

- **Database Persistence:** Currently, data is stored in-memory. Integration with a persistent database (e.g., H2 or PostgreSQL) is pending.
- **Full Automated Test Coverage:** Only a single end-to-end test covering the complete workflow (user → poll → option → vote) has been implemented. Additional unit tests and edge case tests could enhance coverage.
- **API Documentation (Swagger/OpenAPI):** Not yet configured. Including Swagger UI would improve API exploration and documentation.
- **Docker Deployment:** While a Dockerfile exists, full containerized testing and deployment have not been completed.

---

## Summary of Implementation

- **Domain Model:** Implemented classes for `User`, `Poll`, `VoteOption`, and `Vote` with proper relationships.
- **PollManager:** Provides in-memory management for all entities with auto-incrementing IDs.
- **Controllers:** REST endpoints for CRUD operations on Users, Polls, VoteOptions, and Votes.
- **Serialization:** Jackson annotations prevent infinite recursion in nested JSON structures.
- **Automated Testing:** End-to-end workflow tested using Spring Boot's `TestRestTemplate`, verifying creation of users, polls, options, and votes.

---

*This report documents the technical work, challenges, and pending tasks for Experiment Assignment 2 in DAT250.*
