package com.example.demo.model

import jakarta.persistence.*

@Entity
@Table(name = "vote_options")
open class VoteOption() {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null

    @Column(nullable = false)
    open var caption: String = ""

    @Column(nullable = false)
    open var presentationOrder: Int = 0

    @ManyToOne(optional = false)
    open var poll: Poll? = null

    @OneToMany(mappedBy = "votesOn", cascade = [CascadeType.ALL], orphanRemoval = true)
    open var votes: MutableList<Vote> = mutableListOf()

    constructor(caption: String, poll: Poll, presentationOrder: Int): this() {
        this.caption = caption
        this.poll = poll
        this.presentationOrder = presentationOrder
    }
}