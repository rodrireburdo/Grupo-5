package utn.methodology.domain.entities

import java.util.UUID

data class UserResponseDTO(
    val uuid: UUID,
    val name: String,
    val username: String,
    val email: String
)