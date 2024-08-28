package utn.methodology.infrastructure.http.router

import utn.methodology.application.queryhandlers.SearchUserQueryHandler
import utn.methodology.application.queries.SearchUserQuery
import utn.methodology.infrastructure.persistence.http.actions.SearchUserAction
import utn.methodology.infrastructure.persistence.repositories.SearchUserMongoRepository
import utn.methodology.infrastructure.persistence.connectToMongoDB
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.userRoutes() {
    val mongoDatabase = connectToMongoDB()
    val searchUserMongoRepository = SearchUserMongoRepository(mongoDatabase)
    val searchUserAction = SearchUserAction(SearchUserQueryHandler(searchUserMongoRepository))

    routing {
        get("/users") {
            val username = call.request.queryParameters["username"]
            if (username.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, "Enter username")
                return@get
            }

            val query = SearchUserQuery(username)

            try {
                val user = searchUserAction.handle(query)
                if(user != null) { call.respond(HttpStatusCode.OK, user) }
                else { call.respond(HttpStatusCode.NotFound, "User not found"}
            } catch (error: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Error")
            }
        }
    }
}