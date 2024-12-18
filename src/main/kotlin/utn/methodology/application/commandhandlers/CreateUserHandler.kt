package utn.methodology.application.commandhandlers

import utn.methodology.application.commands.CreateUserCommand
import utn.methodology.domain.entities.User
import utn.methodology.infrastructure.persistence.repositories.MongoUserRepository
import java.util.UUID

class CreateUserHandler(
    private val mongoUserRepository: MongoUserRepository
) {
    fun handle(command: CreateUserCommand): String {
        // Validar que los campos necesarios no estén vacíos
        if (command.userName.isBlank() || command.email.isBlank()) {
            return "Error: El nombre de usuario y el correo electrónico no pueden estar vacíos."
        }

        // Crear un nuevo usuario
        val user = User(
            userId = UUID.randomUUID().toString(),
            username = command.userName,
            email = command.email,
            password = command.password,
        )

        //Guardar el usuario en el repositorio
        return try {
            mongoUserRepository.save(user)
            "Usuario creado exitosamente."
        } catch (e: Exception) {
            "Error al crear el usuario: ${e.message}"
        }
    }
}