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
    routing {
        route("/posts") {

            post {
                try {
                    val postRequest = call.receive<CreatePostRequest>()
                    val author = "eze y rodri"
                    val post = postService.createPost(postRequest.userId, postRequest.message, author)
                    val savedPost = postRepository.save(post)

                    call.respond(HttpStatusCode.Created, savedPost)
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, e.message ?: "Error en la solicitud")
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, "Ocurrió un error inesperado")
                }
            }

            get {
                val order = call.request.queryParameters["order"]?.toUpperCase()
                val limit = call.request.queryParameters["limit"]?.toIntOrNull()
                val offset = call.request.queryParameters["offset"]?.toIntOrNull()

                if (limit != null && limit < 1) {
                    call.respond(HttpStatusCode.BadRequest, "El límite debe ser mayor que 0.")
                    return@get
                }
                if (offset != null && offset < 0) {
                    call.respond(HttpStatusCode.BadRequest, "El offset no puede ser negativo.")
                    return@get
                }
                if (order != null && order != "ASC" && order != "DESC") {
                    call.respond(HttpStatusCode.BadRequest, "El orden debe ser 'ASC' o 'DESC'.")
                    return@get
                }

                val userId = call.principal<UserIdPrincipal>()?.name
                if (userId == null) {
                    call.respond(HttpStatusCode.Unauthorized, "Usuario no autenticado.")
                    return@get
                }

                val posts = MongoPostRepository.getPosts(userId, order, limit, offset)
                call.respond(posts)
            }

            get("/user/{userId}") {
                val userId = call.parameters["userId"] ?: return@get call.respond(HttpStatusCode.BadRequest, "UserId no proporcionado")

                try {
                    val query = GetFollowingQuery(userId)
                    val posts = searchPostQueryHandler.handleGetFollowing(query)

                    call.respond(HttpStatusCode.OK, posts)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, "Error al obtener los posts de los usuarios seguidos.")
                }
            }

            delete("/{id}") {
                try {
                    val postId = call.parameters["id"]
                    if (postId == null) {
                        call.respond(HttpStatusCode.BadRequest, "ID de post no proporcionado.")
                        return@delete
                    }

                    val userId = call.principal<UserIdPrincipal>()?.name
                    if (userId == null) {
                        call.respond(HttpStatusCode.Unauthorized, "Usuario no autenticado.")
                        return@delete
                    }

                    val post = MongoPostRepository.getPostById(postId)
                    if (post == null) {
                        call.respond(HttpStatusCode.NotFound, "Post no encontrado.")
                        return@delete
                    }

                    if (post.userId != userId) {
                        call.respond(HttpStatusCode.Forbidden, "No tienes permiso para eliminar este post.")
                        return@delete
                    }


                    MongoPostRepository.deletePost(postId)
                    call.respond(HttpStatusCode.OK, "Post eliminado exitosamente.")
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, "Ocurrió un error al eliminar el post.")
                }
        }
    }
}