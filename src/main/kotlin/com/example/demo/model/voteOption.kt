package com.example.demo.model

data class VoteOption(
    var id: Long? = null,
    var caption: String,
    var presentationOrder: Int,
    var votes: MutableList<Vote> = mutableListOf()
)