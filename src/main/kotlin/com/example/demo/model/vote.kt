package com.example.demo.model

import java.time.LocalDateTime
import com.fasterxml.jackson.annotation.JsonBackReference

data class Vote(
    var id: Long? = null,
    var publishedAt: LocalDateTime = LocalDateTime.now(),
    var user: User = User(),

    @JsonBackReference
    var option: VoteOption = VoteOption()
)