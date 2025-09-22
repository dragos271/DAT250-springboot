package com.example.demo.model

import jakarta.persistence.*

@Entity
@Table(name = "users")
open class User() {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null

    @Column(nullable = false)
    open var username: String = ""

    @Column(nullable = false)
    open var email: String = ""

    @OneToMany(mappedBy = "createdBy", cascade = [CascadeType.ALL], orphanRemoval = true)
    open var created: MutableSet<Poll> = linkedSetOf()

    constructor(username: String, email: String): this() {
        this.username = username
        this.email = email
    }


    open fun createPoll(question: String): Poll {
        val p = Poll(question = question, createdBy = this)
        created.add(p)
        return p
    }

    open fun voteFor(option: VoteOption): Vote =
        Vote(user = this, votesOn = option)
}