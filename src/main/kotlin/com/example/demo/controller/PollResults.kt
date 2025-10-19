package com.example.demo.controller

import com.example.demo.service.PollManager
import com.example.demo.service.PollCacheService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/polls")
@CrossOrigin(origins = ["*"])
class PollResultsController(
    private val pollManager: PollManager,
    private val pollCacheService: PollCacheService
) {

    @GetMapping("/{pollId}/results")
    fun getPollResults(@PathVariable pollId: Long): Map<Int, Int> {
        // Check cache first
        val cachedResults = pollCacheService.getCachedPollResults(pollId)

        return if (cachedResults != null) {
            // Return cached results
            cachedResults
        } else {
            // Query database
            val results = pollManager.getPollResultsFromDatabase(pollId)

            // Cache the results for future requests
            pollCacheService.cachePollResults(pollId, results)

            results
        }
    }
}
