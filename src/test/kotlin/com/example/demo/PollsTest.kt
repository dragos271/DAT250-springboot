// src/test/kotlin/com/example/demo/PollsTest.kt
package com.example.demo

import com.example.demo.model.*
import jakarta.persistence.EntityManager
import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.Persistence
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull

class PollsTest {

    private lateinit var emf: EntityManagerFactory

    @BeforeEach
    fun setUp() {
        emf = Persistence.createEntityManagerFactory("polls")
        runInTx { em -> populate(em) }
    }

    @AfterEach
    fun tearDown() {
        if (this::emf.isInitialized) emf.close()
    }

    private fun runInTx(block: (EntityManager) -> Unit) {
        val em = emf.createEntityManager()
        val tx = em.transaction
        try {
            tx.begin()
            block(em)
            tx.commit()
        } finally {
            if (tx.isActive) tx.rollback()
            em.close()
        }
    }

    private fun populate(em: EntityManager) {
        val alice = User("alice", "alice@online.com")
        val bob   = User("bob", "bob@bob.home")
        val eve   = User("eve", "eve@mail.org")
        em.persist(alice); em.persist(bob); em.persist(eve)

        val poll1 = alice.createPoll("Vim or Emacs?")
        val vim   = poll1.addVoteOption("Vim")
        val emacs = poll1.addVoteOption("Emacs")

        val poll2 = eve.createPoll("Pineapple on Pizza")
        val yes   = poll2.addVoteOption("Yes! Yammy!")
        val no    = poll2.addVoteOption("Mamma mia: Nooooo!")

        em.persist(poll1); em.persist(poll2)
        em.persist(alice.voteFor(vim))
        em.persist(bob.voteFor(vim))
        em.persist(eve.voteFor(emacs))
        em.persist(eve.voteFor(yes))
    }

    @Test
    fun testUsers() = runInTx { em ->
        val count = (em.createNativeQuery("select count(id) from users").singleResult as Number).toInt()
        assertEquals(3, count)

        val maybeBob = em.createQuery(
            "select u from User u where u.username like 'bob'", User::class.java
        ).resultList.firstOrNull()
        assertNotNull(maybeBob)
    }

    @Test
    fun testVotes() = runInTx { em ->
        val vimVotes = (em.createQuery(
            "select count(v) from Vote v join v.votesOn o join o.poll p join p.createdBy u " +
                    "where u.email = :mail and o.presentationOrder = :ord", java.lang.Long::class.java
        )
            .setParameter("mail", "alice@online.com")
            .setParameter("ord", 0)
            .singleResult as Number).toLong()

        val emacsVotes = (em.createQuery(
            "select count(v) from Vote v join v.votesOn o join o.poll p join p.createdBy u " +
                    "where u.email = :mail and o.presentationOrder = :ord", java.lang.Long::class.java
        )
            .setParameter("mail", "alice@online.com")
            .setParameter("ord", 1)
            .singleResult as Number).toLong()

        assertEquals(2L, vimVotes)
        assertEquals(1L, emacsVotes)
    }

    @Test
    fun testOptions() = runInTx { em ->
        val poll2Options = em.createQuery(
            "select o.caption from Poll p join p.options o join p.createdBy u " +
                    "where u.email = :mail order by o.presentationOrder", String::class.java
        )
            .setParameter("mail", "eve@mail.org")
            .resultList

        val expected = listOf("Yes! Yammy!", "Mamma mia: Nooooo!")
        assertEquals(expected, poll2Options)
    }
}