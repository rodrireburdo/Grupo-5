package utn.methodology.infrastructure.http.router

import utn.methodology.infrastructure.persistence.repositories.MongoPostRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import utn.methodology.application.commands.CreatePostCommand
import utn.methodology.domain.entities.Post
import utn.methodology.infrastructure.persistence.repositories.MongoUserRepository

fun Application.postRoutes(postRepository: MongoPostRepository, userRepository: MongoUserRepository) {
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
                val order = call.request.queryParameters["order"]?.toUpperCase() ?: "DESC"
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

            get("/users/{followerId}/followed/{followedId}") {
                val followerId = call.parameters["followerId"]
                val followedId = call.parameters["followedId"]

                if (followerId.isNullOrBlank() || followedId.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest, "Se requieren los IDs de los usuarios (followerId y followedId)")
                    return@get
                }

                // Verificar si followerId sigue a followedId
                val isFollowing = userRepository.isFollowing(followerId, followedId)
                if (!isFollowing) {
                    call.respond(HttpStatusCode.Forbidden, "El usuario no tiene permiso para ver los posts de este usuario")
                    return@get
                }

                // Obtener y devolver los posts del usuario seguido
                val posts = postRepository.findPosts("ASC", 10, 0, followedId)
                call.respond(HttpStatusCode.OK, posts)
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
    )
}

// Función de manejo de errores de validación
private suspend fun handleValidationError(call: ApplicationCall, exception: IllegalArgumentException) {
    call.respond(HttpStatusCode.BadRequest, "Error de validación: ${exception.message}")
}