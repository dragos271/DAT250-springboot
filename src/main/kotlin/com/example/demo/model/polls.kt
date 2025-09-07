package com.example.demo.model
import com.fasterxml.jackson.annotation.JsonManagedReference
import java.time.LocalDateTime

data class Poll(
    var id: Long? = null,
    var question: String = "",
    var publishedAt: LocalDateTime = LocalDateTime.now(),
    var validUntil: LocalDateTime? = null,
    @JsonManagedReference
    var options: MutableList<VoteOption> = mutableListOf()
)