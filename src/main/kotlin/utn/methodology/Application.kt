package utn.methodology

import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.LoggerFactory
import utn.methodology.infrastructure.http.router.postRoutes
import utn.methodology.infrastructure.http.router.userRoutes
import utn.methodology.infrastructure.persistence.configureDatabases
import utn.methodology.infrastructure.persistence.connectToMongoDB
import utn.methodology.infrastructure.persistence.repositories.MongoPostRepository

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    // Configuración del manejador de errores
    errorHandler()

    // Conexión y configuración de la base de datos
    val database = connectToMongoDB()
    val postRepository = MongoPostRepository(database)

    // Rutas y configuración del enrutador
    routing {
        this@module.postRoutes(postRepository)  // Rutas para manejar posts
        this@module.userRoutes()                // Rutas para manejar usuarios
    }

    configureDatabases()
}

fun Application.errorHandler() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            logError(call, cause)
            val status = when (cause) {
                is IllegalArgumentException -> HttpStatusCode.BadRequest
                else -> HttpStatusCode.InternalServerError
            }
            call.respond(status, mapOf("error" to (cause.message ?: "Error interno del servidor")))
        }
    }
}

fun logError(call: ApplicationCall, cause: Throwable) {
    val log = LoggerFactory.getLogger("ErrorLogger")
    val requestUri = call.request.uri
    log.error("Error at $requestUri: ${cause.localizedMessage}", cause)
}
