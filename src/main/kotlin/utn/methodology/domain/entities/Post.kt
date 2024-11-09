package utn.methodology.domain.entities

import java.time.LocalDateTime

data class Post(
    val postId: String,
    val userId: Long,
    val message: String,
    val createdAt: Long = System.currentTimeMillis(),
    val author: String
)