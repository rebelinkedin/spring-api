package com.exercise.springapi.api

import com.exercise.springapi.domain.Post
import com.exercise.springapi.repository.PostRepository
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.assertEquals
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.ResponseBody
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity


class PostsControllerTest {
    private lateinit var postsController: PostsController
    private lateinit var postRepository: PostRepository
    private lateinit var okHttpClient: OkHttpClient
    private lateinit var call: Call
    private val objectMapper = jacksonObjectMapper()


    @BeforeEach
    fun setup() {
        okHttpClient = mockk()
        postRepository = mockk()
        call = mockk()
        postsController = PostsController(okHttpClient, postRepository)
    }

    @Test
    fun `getPosts should returns 200 if successful`() {
        val response = mockk<Response>()
        val responseBody = mockk<ResponseBody>()

        val postList = listOf(Post(documentId = "1", id = 1, userId = 1, "Post Title", "Post Body"))

        every { okHttpClient.newCall(any()) } returns call
        every { call.execute() } returns response
        every { response.isSuccessful } returns true
        every { response.body } returns responseBody
        every { responseBody.string() } returns objectMapper.writeValueAsString(postList)
        every { response.close() } returns Unit

        val result: ResponseEntity<List<Post>> = postsController.getPosts()
        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(postList, result.body)
    }

    @Test
    fun `getPosts should returns 204 if response body has no content`() {
        val response = mockk<Response>()

        every { okHttpClient.newCall(any()) } returns call
        every { call.execute() } returns response
        every { response.isSuccessful } returns true
        every { response.body } returns null
        every { response.close() } returns Unit

        val result: ResponseEntity<List<Post>> = postsController.getPosts()
        assertEquals(HttpStatus.NO_CONTENT, result.statusCode)
        assertEquals(emptyList(), result.body)
    }

    @Test
    fun `getPosts should returns 500 if request fails`() {
        val response = mockk<Response>()
        val responseBody = mockk<ResponseBody>()

        every { okHttpClient.newCall(any()) } returns call
        every { call.execute() } returns response
        every { response.isSuccessful } returns false
        every { response.body } returns responseBody
        every { response.close() } returns Unit

        val result: ResponseEntity<List<Post>> = postsController.getPosts()
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.statusCode)
        assertEquals(emptyList(), result.body)
    }

    @Test
    fun `getPostById should returns 200 if retrieving post successfully from the repository`() {
        val id = 1
        val post = Post(documentId = "1", id = id, userId = 1, "Post Title", "Post Body")

        every { postRepository.findById(id) } returns post

        val result: ResponseEntity<Post?> = postsController.getPostById(id)
        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(post, result.body)
    }

    @Test
    fun `getPostById should returns 200 if successful`() {
        val response = mockk<Response>()
        val responseBody = mockk<ResponseBody>()
        val id = 1
        val post = Post(documentId = "1", id = id, userId = 1, "Post Title", "Post Body")

        every { postRepository.findById(id) } returns null
        every { okHttpClient.newCall(any()) } returns call
        every { call.execute() } returns response
        every { response.isSuccessful } returns true
        every { response.body } returns responseBody
        every { responseBody.string() } returns objectMapper.writeValueAsString(post)
        every { response.close() } returns Unit

        val result: ResponseEntity<Post?> = postsController.getPostById(id)
        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(post, result.body)
    }

    @Test
    fun `getPostById should returns 204 if response body has no content`() {
        val response = mockk<Response>()

        every { postRepository.findById(1) } returns null
        every { okHttpClient.newCall(any()) } returns call
        every { call.execute() } returns response
        every { response.isSuccessful } returns true
        every { response.body } returns null
        every { response.close() } returns Unit

        val result: ResponseEntity<Post?> = postsController.getPostById(1)
        assertEquals(HttpStatus.NO_CONTENT, result.statusCode)
        assertEquals(null, result.body)
    }

    @Test
    fun `getPostById should returns 500 if request fails`() {
        val response = mockk<Response>()
        val responseBody = mockk<ResponseBody>()

        every { postRepository.findById(1) } returns null
        every { okHttpClient.newCall(any()) } returns call
        every { call.execute() } returns response
        every { response.isSuccessful } returns false
        every { response.body } returns responseBody
        every { response.close() } returns Unit

        val result: ResponseEntity<Post?> = postsController.getPostById(1)
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.statusCode)
        assertEquals(null, result.body)
    }

    @Test
    fun `createPost should returns 201 and store the post if successful`() {
        val response = mockk<Response>()
        val responseBody = mockk<ResponseBody>()
        val id = 1
        val post = Post(documentId = "1", id = id, userId = 1, "Post Title", "Post Body")

        every { postRepository.save(any()) } returns post
        every { okHttpClient.newCall(any()) } returns call
        every { call.execute() } returns response
        every { response.isSuccessful } returns true
        every { response.body } returns responseBody
        every { responseBody.string() } returns objectMapper.writeValueAsString(post)
        every { response.close() } returns Unit

        val result: ResponseEntity<Post> = postsController.createPost(post)
        verify(exactly = 1) { postRepository.save(post) }
        assertEquals(HttpStatus.CREATED, result.statusCode)
        assertEquals(post, result.body)
    }

    @Test
    fun `createPost should returns 204 if response body has no content`() {
        val response = mockk<Response>()
        val post = Post(documentId = "1", id = 1, userId = 1, "Post Title", "Post Body")

        every { okHttpClient.newCall(any()) } returns call
        every { call.execute() } returns response
        every { response.isSuccessful } returns true
        every { response.body } returns null
        every { response.close() } returns Unit

        val result: ResponseEntity<Post> = postsController.createPost(post)
        verify(exactly = 0) { postRepository.save(post) }
        assertEquals(HttpStatus.NO_CONTENT, result.statusCode)
        assertEquals(null, result.body)
    }

    @Test
    fun `createPost should returns 500 if request fails`() {
        val response = mockk<Response>()
        val responseBody = mockk<ResponseBody>()
        val post = Post(documentId = "1", id = 1, userId = 1, "Post Title", "Post Body")

        every { okHttpClient.newCall(any()) } returns call
        every { call.execute() } returns response
        every { response.isSuccessful } returns false
        every { response.body } returns responseBody
        every { response.close() } returns Unit

        val result: ResponseEntity<Post> = postsController.createPost(post)
        verify(exactly = 0) { postRepository.save(post) }
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.statusCode)
        assertEquals(null, result.body)
    }

    @Test
    fun `updatePost should returns 200 and update the existing post if successful`() {
        val response = mockk<Response>()
        val responseBody = mockk<ResponseBody>()
        val id = 1
        val existingPost = Post(documentId = "1", id = id, userId = 1, "Post Title", "Post Body")
        val newPost = Post(documentId = "1", id = id, userId = 1, "New Post Title", "New Post Body")

        every { postRepository.findById(1) } returns existingPost
        every { postRepository.save(any()) } returns newPost
        every { okHttpClient.newCall(any()) } returns call
        every { call.execute() } returns response
        every { response.isSuccessful } returns true
        every { response.body } returns responseBody
        every { responseBody.string() } returns objectMapper.writeValueAsString(newPost)
        every { response.close() } returns Unit

        val result: ResponseEntity<Post> = postsController.updatePost(1, newPost)
        verify(exactly = 1) { postRepository.save(newPost) }
        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(newPost, result.body)
    }

    @Test
    fun `updatePost should returns 201 if there is no existing post and the response was successful`() {
        val response = mockk<Response>()
        val responseBody = mockk<ResponseBody>()
        val id = 1
        val post = Post(documentId = "1", id = id, userId = 1, "Post Title", "Post Body")

        every { postRepository.findById(1) } returns null
        every { postRepository.save(any()) } returns post
        every { okHttpClient.newCall(any()) } returns call
        every { call.execute() } returns response
        every { response.isSuccessful } returns true
        every { response.body } returns responseBody
        every { responseBody.string() } returns objectMapper.writeValueAsString(post)
        every { response.close() } returns Unit

        val result: ResponseEntity<Post> = postsController.updatePost(1, post)
        verify(exactly = 1) { postRepository.save(post) }
        assertEquals(HttpStatus.CREATED, result.statusCode)
        assertEquals(post, result.body)
    }

}