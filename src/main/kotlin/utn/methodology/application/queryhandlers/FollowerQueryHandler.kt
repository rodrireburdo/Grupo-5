package utn.methodology.application.queryhandlers
import io.ktor.server.plugins.*
import utn.methodology.application.queries.FollowerQuery
import utn.methodology.domain.entities.User
import utn.methodology.infrastructure.persistence.repositories.MongoUserRepository

class FollowerQueryHandler(private val userRepository: MongoUserRepository) {

    fun handleGetFollowers(query: GetFollowersQuery): Set<String>? {
        val user = userRepository.findById(query.userId)
        return user?.followers
    }

    fun handleGetFollowing(query: GetFollowingQuery): Set<String>? {
        val user = userRepository.findById(query.userId)
        return user?.following
    }
}
