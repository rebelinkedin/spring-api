package com.exercise.springapi.repository

import com.exercise.springapi.domain.Post
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface PostRepository : MongoRepository<Post, String> {

    @Query("{ 'id' : ?0 }")
    fun findById(id: Int): Post?

    @Query("{ 'title' : { \$regex: ?0, \$options: 'i' } }")
    fun findByTitle(keyword: String): Post?
}