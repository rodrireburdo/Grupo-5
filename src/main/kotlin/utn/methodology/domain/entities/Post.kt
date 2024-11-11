package utn.methodology.domain.entities

import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class Post(
    val postId: String = UUID.randomUUID().toString(),
    val userId: String,
    val message: String,
    val createdAt: Long = System.currentTimeMillis(),
)