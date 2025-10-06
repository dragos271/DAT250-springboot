package com.example.demo.service

import com.example.demo.model.*
import org.springframework.stereotype.Component
import org.springframework.data.redis.core.RedisTemplate
import java.time.Duration

@Component
class PollManager(
    private val redisTemplate: RedisTemplate<String, String>
) {

    private val users = mutableMapOf<Long, User>()
    private val polls = mutableMapOf<Long, Poll>()
    private val votes = mutableMapOf<Long, Vote>()

    private var userIdCounter = 1L
    private var pollIdCounter = 1L
    private var optionIdCounter = 1L
    private var voteIdCounter = 1L


    fun getPollResultsWithCache(pollId: Long): Map<Int, Int> {
        val cacheKey = "poll:$pollId:results"
        val hashOps = redisTemplate.opsForHash<String, String>()


        val cached = hashOps.entries(cacheKey)
        if (cached.isNotEmpty()) {

            return cached
                .mapKeys { it.key.toInt() }
                .mapValues { it.value.toInt() }
                .toSortedMap()
        }


        val results = getPollResultsFromDatabase(pollId)


        if (results.isNotEmpty()) {
            results.forEach { (order, count) ->
                hashOps.put(cacheKey, order.toString(), count.toString())
            }
            redisTemplate.expire(cacheKey, Duration.ofMinutes(30))
        }
        return results
    }


    fun invalidatePollCache(pollId: Long) {
        redisTemplate.delete("poll:$pollId:results")
    }


    fun incrementCachedOption(pollId: Long, presentationOrder: Int) {
        val cacheKey = "poll:$pollId:results"
        val field = presentationOrder.toString()

        if (redisTemplate.hasKey(cacheKey) == true) {
            redisTemplate.opsForHash<String, String>().increment(cacheKey, field, 1)

            redisTemplate.expire(cacheKey, Duration.ofMinutes(30))
        }
    }


    fun getPollResultsFromDatabase(pollId: Long): Map<Int, Int> {
        val poll = polls[pollId] ?: return emptyMap()
        return poll.options
            .associate { option -> option.presentationOrder to option.votes.size }
            .toSortedMap()
    }


    fun createUser(username: String, email: String): User {
        val u = User(username, email)
        u.id = userIdCounter++
        users[u.id!!] = u
        return u
    }

    fun listUsers(): List<User> = users.values.toList()

    fun createPoll(question: String): Poll {
        val p = Poll()
        p.id = pollIdCounter++
        p.question = question
        polls[p.id!!] = p
        return p
    }

    fun listPolls(): List<Poll> = polls.values.toList()

    fun addOptionToPoll(pollId: Long, caption: String, order: Int): VoteOption? {
        val p = polls[pollId] ?: return null
        val opt = VoteOption()
        opt.id = optionIdCounter++
        opt.caption = caption
        opt.presentationOrder = order
        opt.poll = p
        p.options.add(opt)

        invalidatePollCache(pollId)
        return opt
    }

    fun castVote(userId: Long, option: VoteOption): Vote? {
        val user = users[userId] ?: return null
        val v = Vote()
        v.id = voteIdCounter++
        v.user = user
        v.votesOn = option
        option.votes.add(v)
        votes[v.id!!] = v


        option.poll?.id?.let { pollId ->

            incrementCachedOption(pollId, option.presentationOrder)
        }

        return v
    }

    fun listVotes(): List<Vote> = votes.values.toList()
}
