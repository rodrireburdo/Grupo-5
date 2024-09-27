package utn.methodology

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.http.*

// Mocks
data class CreatePostRequest(val userId: String, val message: String)

class PostService {
    fun createPost(userId: String, message: String): Boolean {
        if (userId.isBlank() || message.isBlank()) {
            throw IllegalArgumentException("Datos inválidos")
        }
        // Lógica para crear un post (simulación para el test)
        return true
    }
}

// Rutas para testear la creación de posts
fun Application.postRoutes() {
    routing {
        post("/posts") {
            try {
                val request = call.receive<CreatePostRequest>()
                val postService = PostService()
                postService.createPost(request.userId, request.message)
                call.respond(HttpStatusCode.Created, "Post creado correctamente")
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, e.message ?: "Datos inválidos")
            }
        }
    }
}

class PostCreationTest {

    @Test
    fun create_post_should_returns_201() = testApplication {
        // Test para caso exitoso (201)
        application {
            postRoutes() // Se monta la ruta de creación de posts
        }

        val validPost = """
            {
                "userId": "12345",
                "message": "Este es un mensaje válido para un post."
            }
        """.trimIndent()

        // Simulación de la request
        client.post("/posts") {
            contentType(ContentType.Application.Json)
            setBody(validPost)
        }.apply {
            assertEquals(HttpStatusCode.Created, status)
            assertEquals("Post creado correctamente", bodyAsText())
        }
    }

    @Test
    fun create_post_should_returns_400() = testApplication {
        // Test para caso fallido (400)
        application {
            postRoutes() // Se monta la ruta de creación de posts
        }

        val invalidPost = """
            {
                "userId": "",
                "message": "Este mensaje es válido, pero falta el userId"
            }
        """.trimIndent()

        // Simulación de la request con datos inválidos
        client.post("/posts") {
            contentType(ContentType.Application.Json)
            setBody(invalidPost)
        }.apply {
            assertEquals(HttpStatusCode.BadRequest, status)
            assertEquals("Datos inválidos", bodyAsText())
        }
    }
}