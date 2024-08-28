package utn.methodology.domain.entities

import java.util.UUID

data class User(
    val uuid: UUID,
    val name: String,
    val username: String,
    val email: String,
    val password: String
)