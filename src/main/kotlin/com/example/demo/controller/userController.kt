package com.example.demo.controller

import com.example.demo.model.User
import com.example.demo.service.PollManager
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class UserController(private val pollManager: PollManager) {

    // Create user (POST /users)
    @PostMapping
    fun createUser(@RequestBody user: User): User {
        return pollManager.createUser(user.username, user.email)
    }

    // List users (GET /users)
    @GetMapping
    fun listUsers(): List<User> {
        return pollManager.listUsers()
    }
}