
package com.example.demo.service

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class PollCacheService(
    private val redisTemplate: RedisTemplate<String, String>
) {
    data class VoteCount(val presentationOrder: Int, val count: Int)

    private fun key(pollId: Long) = "poll:$pollId:results"


    fun getCachedPollResults(pollId: Long): Map<Int, Int>? {
        val entries = redisTemplate.opsForHash<String, String>().entries(key(pollId))
        if (entries.isEmpty()) return null
        return entries.mapKeys { it.key.toInt() }.mapValues { it.value.toInt() }.toSortedMap()
    }


    fun cachePollResults(pollId: Long, results: Map<Int, Int>, ttlMinutes: Long = 30) {
        if (results.isEmpty()) return
        val k = key(pollId)
        val ops = redisTemplate.opsForHash<String, String>()
        results.forEach { (order, count) -> ops.put(k, order.toString(), count.toString()) }
        redisTemplate.expire(k, Duration.ofMinutes(ttlMinutes))
    }


    fun invalidate(pollId: Long) {
        redisTemplate.delete(key(pollId))
    }


    fun incrementIfCached(pollId: Long, presentationOrder: Int, ttlMinutes: Long = 30) {
        val k = key(pollId)
        if (redisTemplate.hasKey(k) == true) {
            redisTemplate.opsForHash<String, String>().increment(k, presentationOrder.toString(), 1)
            redisTemplate.expire(k, Duration.ofMinutes(ttlMinutes))
        }
    }


    fun getVoteCountsForPoll(pollId: Long, pollManager: PollManager): List<VoteCount> {
        val cached = getCachedPollResults(pollId)
        if (cached != null) {
            return cached.entries
                .map { VoteCount(it.key, it.value) }
                .sortedBy { it.presentationOrder }
        }

        val computed = pollManager.getPollResultsFromDatabase(pollId)
        cachePollResults(pollId, computed)
        return computed.entries
            .map { VoteCount(it.key, it.value) }
            .sortedBy { it.presentationOrder }
    }
}
