package com.example.demo

import redis.clients.jedis.UnifiedJedis

fun main() {

    val jedis = UnifiedJedis("redis://localhost:6379")

    println("Connected to Redis!")


    jedis.set("message", "Hello from Kotlin + Redis!")
    val message = jedis.get("message")
    println("Stored message: $message")


    jedis.sadd("logged_in_users", "alice", "bob")
    println("Logged-in users: ${jedis.smembers("logged_in_users")}")


    val pollKey = "poll:1"
    jedis.hset(pollKey, mapOf("title" to "Pineapple on Pizza?", "yes" to "100", "no" to "80"))

    println("Current poll: ${jedis.hgetAll(pollKey)}")

    jedis.hincrBy(pollKey, "yes", 1)
    println("After voting YES: ${jedis.hgetAll(pollKey)}")

    jedis.expire(pollKey, 10)
    println("Poll TTL: ${jedis.ttl(pollKey)} seconds")

    jedis.close()
    println("✅ Connection closed.")
}
