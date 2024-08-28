package utn.methodology.application.queryhandlers

import utn.methodology.domain.entities.UserResponseDTO
import utn.methodology.application.queries.SearchUserQuery
import utn.methodology.infrastructure.persistence.repositories.SearchUserRepository

class SearchUserQueryHandler(
    private val searchUserRepository: SearchUserRepository
) {
    fun handle(query: SearchUserQuery): UserResponseDTO? {
        val user = searchUserRepository.searchUsername(query.username)

        return user?.let {
            UserResponseDTO(
                uuid = user.uuid,
                name = user.name,
                username = user.username,
                email = user.email
            )
        }
    }
}