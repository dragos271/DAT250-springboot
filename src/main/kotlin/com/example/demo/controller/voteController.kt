package com.example.demo.controller

import com.example.demo.model.Vote
import com.example.demo.service.PollCacheService
import com.example.demo.service.PollManager
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/votes")
@CrossOrigin(origins = ["*"])
class VoteController(
    private val pollManager: PollManager,
    private val pollCacheService: PollCacheService
) {
    @PostMapping
    fun castVote(
        @RequestParam userId: Long,
        @RequestParam optionId: Long
    ): Vote? {

        val option = pollManager.listPolls()
            .flatMap { it.options }
            .firstOrNull { it.id == optionId }
            ?: return null

        val vote = pollManager.castVote(userId, option)


        option.poll?.id?.let { pollId ->
            pollCacheService.invalidate(pollId)
        }



        return vote
    }
}
