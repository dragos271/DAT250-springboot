package com.example.demo.model

import jakarta.persistence.*

@Entity
@Table(name = "polls")
open class Poll() {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null

    @Column(nullable = false)
    open var question: String = ""

    @ManyToOne(optional = false)
    open var createdBy: User? = null

    @OneToMany(mappedBy = "poll", cascade = [CascadeType.ALL], orphanRemoval = true)
    open var options: MutableList<VoteOption> = mutableListOf()

    constructor(question: String, createdBy: User): this() {
        this.question = question
        this.createdBy = createdBy
    }


    open fun addVoteOption(caption: String): VoteOption {
        val opt = VoteOption(
            caption = caption,
            presentationOrder = options.size,
            poll = this
        )
        options.add(opt)
        return opt
    }
}