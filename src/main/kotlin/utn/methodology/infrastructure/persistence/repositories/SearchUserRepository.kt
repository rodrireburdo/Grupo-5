package utn.methodology.infrastructure.persistence.repositories

import utn.methodology.domain.entities.User

interface SearchUserRepository {
    fun searchUsername(username: String): User?
}