package utn.methodology.application.commands


import java.time.LocalDateTime

data class CreatePostCommand(
    val postId: String,
    val userId: Long,
    val message: String,
    val createdAt: Long = System.currentTimeMillis(),
    val author: String
)