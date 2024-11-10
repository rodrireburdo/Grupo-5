package utn.methodology.domain.entities

import java.util.*

data class Post(
    val postId: String = UUID.randomUUID().toString(),
    val userId: String,
    val message: String,
    val createdAt: Long = System.currentTimeMillis(),
    val author: String
)