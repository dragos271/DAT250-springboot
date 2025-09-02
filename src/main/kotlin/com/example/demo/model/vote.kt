package com.example.demo.model

import java.time.LocalDateTime

data class Vote(
    var id: Long? = null,
    var publishedAt: LocalDateTime = LocalDateTime.now(),
    var user: User,
    var option: VoteOption
)