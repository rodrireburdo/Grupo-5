package utn.methodology.infrastructure.http.router

import utn.methodology.application.queryhandlers.SearchPostQueryHandler
import utn.methodology.application.queries.SearchPostQuery
import utn.methodology.infrastructure.http.actions.SearchPostAction
import utn.methodology.infrastructure.persistence.repositories.MongoPostRepository
import utn.methodology.infrastructure.persistence.connectToMongoDB
import utn.methodology.application.commands.CreatePostCommand
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import utn.methodology.infrastructure.persistence.MongoPostRepository
import utn.methodology.domain.entities.Post


fun Application.postRoutes() {
    route("/posts") {
        post {
            try {
                val postRequest = call.receive<CreatePostRequest>()
                val id = "Autor Mock"

                val post = postService.createPost(postRequest.userId, postRequest.message, author)
                val savedPost = postRepository.save(post)

                call.respond(HttpStatusCode.Created, savedPost)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, e.message ?: "Error en la solicitud")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Ocurrió un error inesperado")
            }
        }
    }
}