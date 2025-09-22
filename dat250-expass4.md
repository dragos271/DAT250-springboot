# DAT250 — Experiment Assignment 4: JPA / Hibernate Integration

## Overview
This experiment extends the backend from **expass2** by introducing **Jakarta Persistence (JPA)** with **Hibernate** and **H2**.  
The goal is to map the existing domain model (Users, Polls, VoteOptions, Votes) to relational tables and validate the mappings with automated JPA tests — **without** Spring Data JPA auto-configuration.

---

## What I Implemented

### 1) Dependencies (plain Hibernate + H2, no Spring Data JPA)
`build.gradle.kts` — relevant parts:
```kotlin
plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    kotlin("plugin.jpa") version "1.9.25"         
    id("org.springframework.boot") version "3.5.5"
    id("io.spring.dependency-management") version "1.1.6"
}

dependencies {
    
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    
    implementation("org.hibernate.orm:hibernate-core:7.1.1.Final")
    implementation("jakarta.persistence:jakarta.persistence-api:3.2.0")
    implementation("com.h2database:h2:2.3.232")

    
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}
```
Hibernate was used directly for this assignment.
<img width="743" height="605" alt="Screenshot 2025-09-22 at 20 39 08" src="https://github.com/user-attachments/assets/11174511-c062-416d-b386-a4a19110dd28" />


This screenshot showcases all the 3 tests that run succesfully, testUsers, testVotes, testOptions.

All entities are annotated with JPA:

	•	User ↔ Poll
  
	•	@OneToMany(mappedBy = "createdBy") / @ManyToOne
  
	•	Poll ↔ VoteOption
  
	•	@OneToMany(mappedBy = "poll") / @ManyToOne
  
	•	VoteOption ↔ Vote
  
	•	@OneToMany(mappedBy = "votesOn") / @ManyToOne

Helper methods required by the assignment are implemented:

	•	User.createPoll(question: String): Poll
  
	•	User.voteFor(option: VoteOption): Vote
  
	•	Poll.addVoteOption(caption: String): VoteOption
  
(sets presentationOrder = options.size so first = 0, second = 1, …)

Technical Problems Encountered & Resolutions

	1.	Compile errors after switching to JPA entities
	•	Issue: Old code used named-parameter constructors (Poll(id=..., question=...)) which don’t exist with JPA-style entities.
	•	Fix: Refactored service code to use no-arg construction + property assignment; ensured entities are open via the Kotlin JPA plugin.
  
	2.	“No Persistence provider for EntityManager named polls”
	•	Issue: Tests couldn’t find the persistence unit.
	•	Fix: Created persistence.xml under the exact path src/test/resources/META-INF/persistence.xml with PU name polls.

### Conclusion

The project now includes:

	•	Correct JPA/Hibernate mappings for the poll domain model,
  
	•	A plain-JPA persistence unit using H2,
  
	•	Automated JPA tests that recreate the scenario described in the assignment and pass successfully.

