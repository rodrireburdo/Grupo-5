package example.com.infrastructure.http.actions

import example.com.application.commands.CreateUserCommand
import example.com.application.commandhandlers.CreateUserHandler

class CreateUserAction(
    private val handler: CreateUserHandler
) {

    fun execute(body: CreateUserCommand) {
        if (command.username.isBlank() || command.email.isBlank() || command.password.isBlank()) {
            throw IllegalArgumentException("Invalid Data")
        }
        body.validate().let {
            handler.handle(it)
        }

    }
}