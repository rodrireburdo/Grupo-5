package utn.methodology.infrastructure.http.actions

import utn.methodology.application.commands.CreatePostCommand
import utn.methodology.application.commandhandlers.CreatePostHandler

class CreatePostAction(
    private val handler: CreatePostHandler
) {
    fun execute(body: CreatePostCommand) {
        try {
            // Validar el comando antes de procesarlo
            body.validate()

            // Si la validación es exitosa, ejecutar el handler
            handler.handle(body)
        } catch (e: IllegalArgumentException) {
            // Manejo de excepciones de validación
            println("Error: ${e.message}")
            // Aquí podrías devolver una respuesta de error o realizar un manejo más adecuado.
        }
    }
}