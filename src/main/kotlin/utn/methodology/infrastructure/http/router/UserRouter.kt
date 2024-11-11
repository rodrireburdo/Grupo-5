package utn.methodology.infrastructure.http.router
import utn.methodology.infrastructure.persistence.connectToMongoDB
import utn.methodology.application.commands.CreateUserCommand
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.bson.Document
import utn.methodology.application.commandhandlers.CreateUserHandler
import utn.methodology.infrastructure.http.actions.CreateUserAction
import com.mongodb.client.MongoCollection
import utn.methodology.application.queries.SearchUserQuery
import utn.methodology.application.queryhandlers.SearchUserQueryHandler
import utn.methodology.infrastructure.http.actions.SearchUserAction
import utn.methodology.infrastructure.persistence.repositories.MongoUserRepository

fun Application.userRoutes() {
    val mongoDatabase = connectToMongoDB()
    val userRepository = MongoUserRepository(mongoDatabase)
    val collection: MongoCollection<Document> = mongoDatabase.getCollection("users") // Definimos la colección

    val searchUserAction = SearchUserAction(SearchUserQueryHandler(userRepository))
    val createUserAction = CreateUserAction(CreateUserHandler(userRepository))

    routing {
        route("/users") {
            // Ruta para buscar un usuario por userName
            get("/{userName}") {
                val userName = call.parameters["userName"]

                if (userName.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest, "Enter username")
                    return@get
                }

                val query = SearchUserQuery(userName)

                try {
                    val user = searchUserAction.execute(query)
                    if (user != null) {
                        call.respond(HttpStatusCode.OK, user)
                    } else {
                        call.respond(HttpStatusCode.NotFound, "User not found")
                    }
                } catch (error: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, "Error fetching user: ${error.message}")
                }
            }

            get("/{userName}/followers-count") {
                val userName = call.parameters["userName"]

                if (userName.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest, "Username is required")
                    return@get
                }

                // Acceso a la colección de usuarios
                val collection: MongoCollection<Document> = mongoDatabase.getCollection("users")

                // Buscar al usuario en la base de datos
                val user = collection.find(Document("username", userName)).firstOrNull()

                if (user == null) {
                    call.respond(HttpStatusCode.NotFound, "User not found")
                    return@get
                }

                // Obtén el tamaño de las listas de followers y following
                val followersCount = (user.get("followers") as? List<*>)?.size ?: 0
                val followingCount = (user.get("following") as? List<*>)?.size ?: 0

                // Envía la respuesta con los contadores
                call.respond(HttpStatusCode.OK, mapOf(
                    "followersCount" to followersCount,
                    "followingCount" to followingCount
                ))
            }

            // Ruta para crear un usuario
            post {
                val body = call.receive<CreateUserCommand>()

                try {
                    createUserAction.execute(body)
                    call.respond(HttpStatusCode.Created, mapOf("message" to "User created successfully"))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, "Error creating user")
                }
            }
        }

        // Ruta para registrarse (puedes eliminarla si no es necesaria)
        post("/register") {
            val body = call.receive<CreateUserCommand>()

            try {
                createUserAction.execute(body)
                call.respond(HttpStatusCode.Created, mapOf("message" to "User created successfully"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Error creating user")
            }
        }

        // Ruta para seguir a un usuario
        post("/follow") {
            // Recibe el cuerpo como un objeto JSON
            val requestBody = call.receive<Map<String, String>>()

            // Extrae los datos del JSON
            val followerId = requestBody["followerId"]
            val userName = requestBody["userName"]

            if (followerId.isNullOrBlank() || userName.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, "Follower ID and username are required")
                return@post
            }

            val success = userRepository.followUser(followerId, userName)

            if (success) {
                call.respond(HttpStatusCode.OK, "Successfully followed user")
            } else {
                call.respond(HttpStatusCode.NotFound, "Could not follow user")
            }
        }
    }
}