package utn.methodology.application.commands

import kotlinx.serialization.Serializable

@Serializable()

data class CreateUserCommand(
    val userName: String,
    val password: String,
    val email: String,
) {
    fun validate(): CreateUserCommand {
        checkNotNull(userName) { throw IllegalArgumentException("UserName must be defined") }
        checkNotNull(password) { throw IllegalArgumentException("Password must be defined") }
        checkNotNull(email) { throw IllegalArgumentException("Email must be defined") }

        return this;
    }
}