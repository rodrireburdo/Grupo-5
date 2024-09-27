package utn.methodology.domain.entities

import java.time.LocalDateTime

data class Post(
    val id: Long? = null,
    val userId: Long,
    val message: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val author: String
)