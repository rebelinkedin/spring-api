package com.exercise.springapi.api

import com.exercise.springapi.domain.Post
import com.exercise.springapi.repository.PostRepository
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class PostsController(
    private val httpClient: OkHttpClient,
    private val postRepository: PostRepository
) {
    private val objectMapper = jacksonObjectMapper()
    private val headers = HttpHeaders()
    private val baseUrl = "https://jsonplaceholder.typicode.com"
    private val log: Logger = LoggerFactory.getLogger(PostsController::class.java)


    init {
        headers.contentType = MediaType.APPLICATION_JSON
    }

    @GetMapping("/posts")
    fun getPosts(): ResponseEntity<List<Post>> {
        val request = Request.Builder().url("$baseUrl/posts").build()
        return executeRequest<List<Post>>(request, successStatus = HttpStatus.OK).let { response ->
            when {
                response.body != null -> ResponseEntity(response.body!!, headers, response.statusCode)
                else -> ResponseEntity(emptyList(), headers, response.statusCode)
            }
        }.also { log.info("GET /posts - ${it.statusCode}") }
    }

    @GetMapping("/posts/{id}")
    fun getPostById(@PathVariable id: Int): ResponseEntity<Post?> {
        val post = postRepository.findById(id)
        if (post != null) {
            return ResponseEntity.status(HttpStatus.OK).body(post)
        }

        val request = Request.Builder().url("$baseUrl/posts/$id").build()
        return executeRequest<Post?>(request, successStatus = HttpStatus.OK)
            .also { log.info("GET /posts/${id} - ${it.statusCode}") }
    }

    @PostMapping("/posts")
    fun createPost(@RequestBody post: Post): ResponseEntity<Post> {
        val request = Request.Builder()
            .url("$baseUrl/posts")
            .post(objectMapper.writeValueAsString(post).toRequestBody("Application/Json".toMediaType()))
            .build()
        val response = executeRequest<Post>(request, successStatus = HttpStatus.CREATED)
            .also { log.info("POST /posts - ${it.statusCode}") }

        if (response.statusCode == HttpStatus.CREATED) postRepository.save(post)
        return response
    }

    @PutMapping("/posts/{id}")
    fun updatePost(@PathVariable id: Int, @RequestBody newPost: Post): ResponseEntity<Post> {
        val existingPost = postRepository.findById(id)
        return if (existingPost != null) {
            val request = Request.Builder()
                .url("$baseUrl/posts")
                .put(objectMapper.writeValueAsString(newPost).toRequestBody("Application/Json".toMediaType()))
                .build()
            val response = executeRequest<Post>(request, successStatus = HttpStatus.OK)
                .also { log.info("PUT /posts/${id} - ${it.statusCode}") }

            if (response.statusCode == HttpStatus.OK) postRepository.save(newPost)
            response
        } else createPost(newPost)
    }

    private inline fun <reified T> executeRequest(
        request: Request,
        successStatus: HttpStatus = HttpStatus.OK
    ): ResponseEntity<T> {

        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null)
            }

            val responseBody = response.body?.string()
                ?: return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null)

            val result: T = when {
                List::class.java.isAssignableFrom(T::class.java) -> {
                    objectMapper.readValue(responseBody, object : TypeReference<T>() {})
                }

                Map::class.java.isAssignableFrom(T::class.java) -> {
                    objectMapper.readValue(responseBody, object : TypeReference<T>() {})
                }

                Set::class.java.isAssignableFrom(T::class.java) -> {
                    objectMapper.readValue(responseBody, object : TypeReference<T>() {})
                }

                else -> {
                    objectMapper.readValue(responseBody, T::class.java)
                }
            }

            return ResponseEntity(result, headers, successStatus)
        }
    }
}