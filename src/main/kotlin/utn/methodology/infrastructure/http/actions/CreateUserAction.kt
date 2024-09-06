package utn.methodology.infrastructure.http.actions

import utn.methodology.application.commands.CreateUserCommand
import utn.methodology.application.commandhandlers.CreateUserHandler

class CreateUserAction(
    private val handler: CreateUserHandler
) {

    fun execute(body: CreateUserCommand) {
        if (body.userName.isBlank() || body.email.isBlank() || body.password.isBlank()) {
            throw IllegalArgumentException("Invalid Data")
        }
        body.validate().let {
            handler.handle(it)
        }

    }
}