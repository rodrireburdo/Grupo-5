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


fun Route.postRoutes() {
    route("/posts") {
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
    }
}
