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

            get {
                // Obtener los parámetros de consulta opcionales
                val order = call.request.queryParameters["order"]?.toUpperCase() ?: "ASC"
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10
                val offset = call.request.queryParameters["offset"]?.toIntOrNull() ?: 0
                val userId = call.request.queryParameters["userId"]

                // Validar los parámetros
                if (order !in listOf("ASC", "DESC")) {
                    call.respond(HttpStatusCode.BadRequest, "El parámetro 'order' debe ser 'ASC' o 'DESC'")
                    return@get
                }

                if (limit <= 0) {
                    call.respond(HttpStatusCode.BadRequest, "El parámetro 'limit' debe ser mayor que cero")
                    return@get
                }

                try {
                    // Llamar al repositorio con el filtro de userId
                    val posts = postRepository.findPosts(order, limit, offset, userId)
                    call.respond(HttpStatusCode.OK, posts)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, "Error al obtener los posts: ${e.message}")
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