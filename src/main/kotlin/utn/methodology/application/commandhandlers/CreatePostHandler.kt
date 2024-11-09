package utn.methodology.application.commandhandlers

import utn.methodology.domain.entities.Post;
import utn.methodology.infrastructure.persistence.repositories.MongoPostRepository;
import utn.methodology.application.commands.CreatePostCommand


class CreatePostHandler(
    private val postRepository: MongoPostRepository
){
    fun handle(command: CreatePostCommand) {
        val post = Post(
            userId = command.userId,
            postId = command.postId,
            message = command.message,
            author = command.author,
            createdAt = System.currentTimeMillis()
        )
        postRepository.save(post)
    }
}
