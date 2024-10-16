package com.exercise.springapi.api

import com.exercise.springapi.domain.Post
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class PostsController {
    private val client = OkHttpClient()
    private val objectMapper = jacksonObjectMapper()
    private val headers = HttpHeaders()
    private val baseUrl = "https://jsonplaceholder.typicode.com"

    init {
        headers.contentType = MediaType.APPLICATION_JSON
    }

    @GetMapping("/posts")
    fun getPosts(): ResponseEntity<List<Post>> {
        val request = Request.Builder().url("$baseUrl/posts").build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(emptyList())
            }
            val responseBody = response.body?.string()
                ?: return ResponseEntity.status(HttpStatus.NO_CONTENT).body(emptyList())

            val posts = objectMapper.readValue(responseBody, object : TypeReference<List<Post>>() {})

            return ResponseEntity(posts, headers, HttpStatus.OK)
        }
    }

    @GetMapping("/posts/{id}")
    fun getPostById(@PathVariable id: Int): ResponseEntity<Post?> {
        val request = Request.Builder().url("$baseUrl/posts/$id").build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null)
            }

            val responseBody = response.body?.string()
                ?: return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null)

            val post: Post = objectMapper.readValue(responseBody, Post::class.java)

            return ResponseEntity(post, headers, HttpStatus.OK)
        }
    }
}