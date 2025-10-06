package com.example.demo.controller

import com.example.demo.model.Poll
import com.example.demo.model.VoteOption
import com.example.demo.service.PollManager
import com.example.demo.service.PollCacheService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/polls")
@CrossOrigin(origins = ["*"])
class PollController(
    private val pollManager: PollManager,
    private val pollCacheService: PollCacheService
) {

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

    // New endpoint for cached vote counts
    @GetMapping("/{pollId}/votes")
    fun getVoteCounts(@PathVariable pollId: Long): List<PollCacheService.VoteCount> {
        return pollCacheService.getVoteCountsForPoll(pollId, pollManager)
    }
}
