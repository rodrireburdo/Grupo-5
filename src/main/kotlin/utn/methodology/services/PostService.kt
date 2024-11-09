package utn.methodology.services.PostService

import utn.methodology.domain.entities.Post

class PostService {

    fun createPost(userId: Long, message: String, author: String, postId: String, createdAt: Long): Post {
        if (message.length > 280) {
            throw IllegalArgumentException("El mensaje supera el límite de 200 caracteres.")
        }
        if (message.isBlank()) {
            throw IllegalArgumentException("El mensaje no puede estar vacío.")
        }

        return Post(
            userId = userId,
            message = message,
            author = author,
            postId = postId,
            createdAt = createdAt,
        )
    }

}
