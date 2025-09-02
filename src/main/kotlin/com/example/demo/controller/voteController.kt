package com.example.demo.controller

import com.example.demo.model.Vote
import com.example.demo.service.PollManager
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/votes")
class VoteController(private val pollManager: PollManager) {

    // Cast a vote (POST /votes?userId=1&optionId=2)
    @PostMapping
    fun castVote(
        @RequestParam userId: Long,
        @RequestParam optionId: Long
    ): Vote? {
        // Find poll option by searching through polls
        val option = pollManager.listPolls()
            .flatMap { it.options }
            .find { it.id == optionId }

        return if (option != null) {
            pollManager.castVote(userId, option)
        } else null
    }

    // List all votes (GET /votes)
    @GetMapping
    fun listVotes(): List<Vote> {
        return pollManager.listVotes()
    }
}