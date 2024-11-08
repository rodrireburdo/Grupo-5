package utn.methodology.infrastructure.http.router

import utn.methodology.application.queryhandlers.SearchUserQueryHandler
import utn.methodology.application.queries.SearchUserQuery
import utn.methodology.infrastructure.http.actions.SearchUserAction
import utn.methodology.infrastructure.persistence.repositories.MongoUserRepository
import utn.methodology.infrastructure.persistence.connectToMongoDB
import utn.methodology.application.commands.CreateUserCommand
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import utn.methodology.application.commandhandlers.CreateUserHandler
import utn.methodology.infrastructure.http.actions.CreateUserAction
import utn.methodology.application.commandhandlers.FollowUserHandler
import utn.methodology.infrastructure.http.actions.FollowUserAction
import utn.methodology.application.commands.FollowUserCommand


fun Application.userRoutes() {
    val mongoDatabase = connectToMongoDB()
    val searchUserMongoRepository = MongoUserRepository(mongoDatabase)
    val searchUserAction = SearchUserAction(SearchUserQueryHandler(searchUserMongoRepository))
    val createUserAction = CreateUserAction(CreateUserHandler(searchUserMongoRepository))
    val followUserAction = FollowUserAction(FollowUserHandler(searchUserMongoRepository))

    routing {
        get("/users/{name}") {
            val username = call.parameters["name"]
            if (username.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, "Ingrese el nombre de usuario")
                return@get
            }

            val query = SearchUserQuery(username)

            try {
                val user = searchUserAction.execute(query)
                if (user != null) {
                    call.respond(HttpStatusCode.OK, user)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Usuario no encontrado")
                }
            } catch (error: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Error interno del servidor")
            }
        }

        post("/users") {
            val body = call.receive<CreateUserCommand>()
            try {
                createUserAction.execute(body)
                call.respond(HttpStatusCode.Created, mapOf("mensaje" to "Usuario creado exitosamente"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Error al crear el usuario")
            }
        }

        post("/register") {
            val body = call.receive<CreateUserCommand>()
            try {
                createUserAction.execute(body)
                call.respond(HttpStatusCode.Created, mapOf("mensaje" to "Usuario registrado exitosamente"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Error al registrar el usuario")
            }
        }

        // Ruta para seguir a un usuario
        post("/users/{followerId}/follow/{followeeId}") {
            val followerId = call.parameters["followerId"]
            val followeeId = call.parameters["followeeId"]

            if (followerId == null || followeeId == null) {
                call.respond(HttpStatusCode.BadRequest, "Debe proporcionar los IDs de seguidor y seguido")
                return@post
            }

            try {
                val command = FollowUserCommand(followerId, followeeId)
                followUserAction.execute(command)
                call.respond(HttpStatusCode.OK, mapOf("mensaje" to "Seguido exitosamente"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Error al seguir al usuario")
            }
        }
    }
}
