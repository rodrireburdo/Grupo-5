package utn.methodology.infrastructure.http.router

import utn.methodology.application.queryhandlers.SearchUserQueryHandler
import utn.methodology.application.queries.SearchUserQuery
import utn.methodology.infrastructure.http.actions.SearchUserAction
import utn.methodology.infrastructure.persistence.repositories.MongoUserRepository
import utn.methodology.infrastructure.persistence.connectToMongoDB
import utn.methodology.application.commands.CreateUserCommands
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.userRoutes() {
    val mongoDatabase = connectToMongoDB()
    val searchUserMongoRepository = MongoUserRepository(mongoDatabase)
    val searchUserAction = SearchUserAction(SearchUserQueryHandler(searchUserMongoRepository))

    routing {
        get("/users/{name}") {
            val username = call.request.queryParameters["username"]
            if (username.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, "Enter username")
                return@get
            }

            val query = SearchUserQuery(username)

            try {
                val user = searchUserAction.execute(query)
                if(user != null) { call.respond(HttpStatusCode.OK, user) }
                else { call.respond(HttpStatusCode.NotFound, "User not found")}
            } catch (error: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Error")
            }
        }
        post("/users") {

            val body = call.receive<CreateUserCommand>()

            createUserAction.execute(body);

            call.respond(HttpStatusCode.Created, mapOf("message" to "ok"))

    }
        post("/register") {
            val body = call.receive<CreateUserCommand>()

            try {
                createUserAction.execute(body)
                call.respond(HttpStatusCode.Created, mapOf("message" to "User created successfully"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Error creating user")
            }
}