package utn.methodology.infrastructure.http.router

import utn.methodology.infrastructure.persistence.repositories.MongoPostRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import utn.methodology.application.commands.CreatePostCommand
import utn.methodology.domain.entities.Post

fun Application.postRoutes(postRepository: MongoPostRepository) {
    routing {
        route("/posts") {

            // Ruta para crear un nuevo post
            post {
                try {
                    val postRequest = call.receive<CreatePostCommand>()
                    postRequest.validate()

                    // Convertir el comando a un objeto Post
                    val post = postRequest.toPost()

                    // Guardar el post en el repositorio
                    postRepository.save(post)
                    call.respond(HttpStatusCode.Created, post)
                } catch (e: IllegalArgumentException) {
                    // Manejo de errores de validación
                    handleValidationError(call, e)
                } catch (e: Exception) {
                    // Manejo de otros errores
                    call.respond(HttpStatusCode.InternalServerError, "Error al crear el post: ${e.localizedMessage}")
                }
            }

            // Ruta para eliminar un post por ID
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

// Función de extensión para convertir el comando en un objeto Post
private fun CreatePostCommand.toPost(): Post {
    return Post(
        userId = this.userId.toString(),
        message = this.message,
        createdAt = this.createdAt,
        author = this.author
    )
}

// Función de manejo de errores de validación
private suspend fun handleValidationError(call: ApplicationCall, exception: IllegalArgumentException) {
    call.respond(HttpStatusCode.BadRequest, "Error de validación: ${exception.message}")
}