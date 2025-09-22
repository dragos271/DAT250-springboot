package com.example.demo.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "votes")
open class Vote() {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null

    open var publishedAt: LocalDateTime = LocalDateTime.now()

    @ManyToOne(optional = false)
    open var user: User? = null


    @ManyToOne(optional = false)
    @JoinColumn(name = "option_id")
    open var votesOn: VoteOption? = null

    constructor(user: User, votesOn: VoteOption): this() {
        this.user = user
        this.votesOn = votesOn
    }
}