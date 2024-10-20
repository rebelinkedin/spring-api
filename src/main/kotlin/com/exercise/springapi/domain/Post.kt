package com.exercise.springapi.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "posts")
@kotlinx.serialization.Serializable
data class Post(
    @Id val documentId: String? = null,
    val id: Int,
    val userId: Int,
    val title: String,
    val body: String
)