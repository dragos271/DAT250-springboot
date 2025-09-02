package com.example.demo.controller

import com.example.demo.model.Poll
import com.example.demo.model.VoteOption
import com.example.demo.service.PollManager
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/polls")
class PollController(private val pollManager: PollManager) {

    // Create poll (POST /polls)
    @PostMapping
    fun createPoll(@RequestBody poll: Poll): Poll {
        return pollManager.createPoll(poll.question)
    }

    // List polls (GET /polls)
    @GetMapping
    fun listPolls(): List<Poll> {
        return pollManager.listPolls()
    }

    // Add option to a poll (POST /polls/{pollId}/options)
    @PostMapping("/{pollId}/options")
    fun addOption(
        @PathVariable pollId: Long,
        @RequestBody option: VoteOption
    ): VoteOption? {
        return pollManager.addOptionToPoll(pollId, option.caption, option.presentationOrder)
    }
}