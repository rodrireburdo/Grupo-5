package utn.methodology.infrastructure.http.router

import utn.methodology.infrastructure.persistence.repositories.MongoPostRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import utn.methodology.application.commands.CreatePostCommand
import utn.methodology.domain.entities.Post
import utn.methodology.services.PostService.PostService

fun Application.postRoutes(postService: PostService, postRepository: MongoPostRepository) {
    routing {
        route("/posts") {

            post("/posts") {
                val postRequest = call.receive<CreatePostCommand>()

                if (postRequest.message.length > 250) {
                    call.respond(HttpStatusCode.BadRequest, "Maximo 250 caracteres.")
                    return@post
                }

                val post = Post(userId = postRequest.userId, message = postRequest.message, author = String.toString(), postId = String.toString())
                call.respond(HttpStatusCode.Created, post)
            }

            delete("/{postId}") {
                val postId = call.parameters["postId"] ?: return@delete call.respond(HttpStatusCode.BadRequest, "Falta el ID del post")

                val wasDeleted = postRepository.deleteById(postId)
                if (wasDeleted) {
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Post no encontrado")
                }
            }

        }
    }
}