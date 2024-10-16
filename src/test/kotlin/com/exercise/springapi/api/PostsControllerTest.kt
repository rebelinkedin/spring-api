package com.exercise.springapi.api

import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest
@AutoConfigureMockMvc
class PostsControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    private lateinit var client: OkHttpClient

    private lateinit var postsController: PostsController

    private val baseUrl = "https://jsonplaceholder.typicode.com"

    @BeforeEach
    fun setup() {
        client = mock()

        postsController = PostsController()
    }

    @Test
    fun `test getPostsById returns 200`() {
        val responseJson = """
            {
                "userId": 1,
                "id": 1,
                "title": "post 1",
                "body": "This is the body of post 1"
            }
        """.trimIndent()

        val request = Request.Builder().url("$baseUrl/posts/1").build()
        val mockResponse = Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .body(responseJson.toResponseBody("application/json".toMediaType()))
            .build()

        Mockito.`when`(client.newCall(ArgumentMatchers.any())).thenReturn(mockCall(mockResponse))

        mockMvc.perform(MockMvcRequestBuilders.get("/posts/1"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Post 1"))
    }

    private fun mockCall(mockResponse: Response): Call {
        val call = mock<Call>()
        Mockito.`when`(call.execute()).thenReturn(mockResponse)
        return call
    }
}