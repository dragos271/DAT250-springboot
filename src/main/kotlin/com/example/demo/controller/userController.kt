package com.example.demo.controller

import com.example.demo.model.User
import com.example.demo.service.PollManager
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = ["*"])
class UserController(private val pollManager: PollManager) {


    @PostMapping
    fun createUser(@RequestBody user: User): User {
        return pollManager.createUser(user.username, user.email)
    }

    
    @GetMapping
    fun listUsers(): List<User> {
        return pollManager.listUsers()
    }
}