package com.example.demo.model

import java.time.LocalDateTime

data class Poll(
    var id: Long? = null,
    var question: String,
    var publishedAt: LocalDateTime = LocalDateTime.now(),
    var validUntil: LocalDateTime? = null,
    var options: MutableList<VoteOption> = mutableListOf()
)