package com.example.demo.controller

import com.example.demo.model.Poll
import com.example.demo.model.VoteOption
import com.example.demo.service.PollManager
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/polls")
class PollController(private val pollManager: PollManager) {


    @PostMapping
    fun createPoll(@RequestBody poll: Poll): Poll {
        return pollManager.createPoll(poll.question)
    }


    @GetMapping
    fun listPolls(): List<Poll> {
        return pollManager.listPolls()
    }


    @PostMapping("/{pollId}/options")
    fun addOption(
        @PathVariable pollId: Long,
        @RequestBody option: VoteOption
    ): VoteOption? {
        return pollManager.addOptionToPoll(pollId, option.caption, option.presentationOrder)
    }
}