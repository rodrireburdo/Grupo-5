package utn.methodology.application.commandhandlers

import utn.methodology.application.commands.CreatePostCommand
import utn.methodology.domain.entities.Post;
import utn.methodology.infrastructure.persistence.repositories.MongoPostRepository;
import java.util.*


class CreatePostHandler(
    private val postRepository: MongoPostRepository
){
    fun handle(command: CreatePostCommand) {
        command.validate()  // Valida el comando antes de usarlo
        val post = Post(
            postId = UUID.randomUUID().toString(),
            userId = command.userId.toString(),
            message = command.message,
            author = command.author,
            createdAt = System.currentTimeMillis()
        )
        postRepository.save(post)
    }
}
