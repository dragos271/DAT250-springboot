package com.example.demo.controller

import com.example.demo.model.Vote
import com.example.demo.service.PollManager
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/votes")
class VoteController(private val pollManager: PollManager) {


    @PostMapping
    fun castVote(
        @RequestParam userId: Long,
        @RequestParam optionId: Long
    ): Vote? {

        val option = pollManager.listPolls()
            .flatMap { it.options }
            .find { it.id == optionId }

        return if (option != null) {
            pollManager.castVote(userId, option)
        } else null
    }


    @GetMapping
    fun listVotes(): List<Vote> {
        return pollManager.listVotes()
    }
}