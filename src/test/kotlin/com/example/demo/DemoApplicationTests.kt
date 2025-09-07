package com.example.demo

import com.example.demo.model.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DemoApplicationTests(
    @Autowired val restTemplate: TestRestTemplate,
    @LocalServerPort val port: Int
) {
    private fun baseUrl(path: String) = "http://localhost:$port$path"
    private fun headers() = HttpHeaders().apply {
        contentType = MediaType.APPLICATION_JSON
    }

    @Test
    fun `full flow - user, poll, option, vote`() {
        // user creation
        val user = User(username = "alice", email = "alice@example.com")
        val userResp = restTemplate.postForEntity(baseUrl("/users"), HttpEntity(user, headers()), User::class.java)
        assertEquals(HttpStatus.OK, userResp.statusCode)
        val userId = userResp.body?.id
        assertNotNull(userId)

        // poll creation
        val poll = Poll(question = "Coffee or Tea?")
        val pollResp = restTemplate.postForEntity(baseUrl("/polls"), HttpEntity(poll, headers()), Poll::class.java)
        assertEquals(HttpStatus.OK, pollResp.statusCode)
        val pollId = pollResp.body?.id
        assertNotNull(pollId)

        // adding option
        val option = VoteOption(caption = "Coffee", presentationOrder = 1)
        val optionResp = restTemplate.postForEntity(
            baseUrl("/polls/$pollId/options"),
            HttpEntity(option, headers()),
            VoteOption::class.java
        )
        assertEquals(HttpStatus.OK, optionResp.statusCode)
        val optionId = optionResp.body?.id
        assertNotNull(optionId)

        // vote casting
        val voteResp = restTemplate.postForEntity(
            baseUrl("/votes?userId=$userId&optionId=$optionId"),
            HttpEntity<Void>(null, headers()),  // empty body
            Vote::class.java
        )
        assertEquals(HttpStatus.OK, voteResp.statusCode)
        assertNotNull(voteResp.body?.id)

        // vote verification
        val votesResp = restTemplate.getForEntity(baseUrl("/votes"), Array<Vote>::class.java)
        assertEquals(HttpStatus.OK, votesResp.statusCode)
        assertEquals(1, votesResp.body?.size)
    }
}