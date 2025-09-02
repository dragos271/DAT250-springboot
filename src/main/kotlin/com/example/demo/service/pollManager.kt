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
    private var voteIdCounter = 1L
    private var optionIdCounter = 1L

    // - USER -
    fun createUser(username: String, email: String): User {
        val user = User(id = userIdCounter++, username = username, email = email)
        users[user.id!!] = user
        return user
    }

    fun listUsers(): List<User> = users.values.toList()

    // - POLL -
    fun createPoll(question: String): Poll {
        val poll = Poll(id = pollIdCounter++, question = question)
        polls[poll.id!!] = poll
        return poll
    }

    fun listPolls(): List<Poll> = polls.values.toList()

    fun addOptionToPoll(pollId: Long, caption: String, order: Int): VoteOption? {
        val poll = polls[pollId] ?: return null
        val option = VoteOption(id = optionIdCounter++, caption = caption, presentationOrder = order)
        poll.options.add(option)
        return option
    }

    // - VOTE -
    fun castVote(userId: Long, option: VoteOption): Vote? {
        val user = users[userId] ?: return null
        val vote = Vote(id = voteIdCounter++, user = user, option = option)
        votes[vote.id!!] = vote
        option.votes.add(vote)
        return vote
    }

    fun listVotes(): List<Vote> = votes.values.toList()
}