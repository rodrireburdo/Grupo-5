package utn.methodology.infrastructure.persistence.repositories

import utn.methodology.domain.entities.User

class UserRepository {
    interface UserRepository {
        fun followUser(followerId: String, userIdToFollow: String): Boolean
        fun unfollowUser(followerId: String, userIdToUnfollow: String): Boolean
        fun getFollowers(userId: String): List<String>
        fun getFollowing(userId: String): List<String>
        fun getUserById(userId: String): User?
    }
}