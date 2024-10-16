package com.exercise.springapi.domain

@kotlinx.serialization.Serializable
data class Post(
    val userId: Int,
    val id: Int,
    val title: String,
    val body: String
)