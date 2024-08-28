package utn.methodology.application.queryhandlers

import io.ktor.server.plugins.*
import utn.methodology.domain.entities.UserResponseDTO
import utn.methodology.application.queries.SearchUserQuery
import utn.methodology.domain.entities.User
import utn.methodology.infrastructure.persistence.MongoUserRepository

class SearchUserQueryHandler(
    private val userRepository: MongoUserRepository
) {
    fun handle(query: SearchUserQuery): Map<String, String> {
        val user = userRepository.findOne(query.username)

        if (user == null){
            throw NotFoundException("user with username ${query.username} not found")
        }
        return user.toPrimitives()


}
}