package com.example.demo.service

import com.example.demo.model.*
import org.springframework.stereotype.Component

@Component
class PollManager {

    private val users = mutableMapOf<Long, User>()
    private val polls = mutableMapOf<Long, Poll>()
    private val votes = mutableMapOf<Long, Vote>()

    private var userIdCounter = 1L
    private var pollIdCounter = 1L
    private var optionIdCounter = 1L
    private var voteIdCounter = 1L


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
        return v
    }

    fun listVotes(): List<Vote> = votes.values.toList()
}