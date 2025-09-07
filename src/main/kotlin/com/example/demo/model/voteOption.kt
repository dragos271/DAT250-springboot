package com.example.demo.model

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonManagedReference

data class VoteOption(
    var id: Long? = null,
    var caption: String = "",
    var presentationOrder: Int = 0,

    @JsonBackReference
    var poll: Poll? = null,   // back link to poll

    @JsonManagedReference
    var votes: MutableList<Vote> = mutableListOf()
)