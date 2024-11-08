package utn.methodology.application.commandhandlers
import utn.methodology.domain.entities.User
import utn.methodology.infrastructure.persistence.repositories.MongoUserRepository
import utn.methodology.application.commands.FollowUserCommand



class FollowUserHandler(private val userRepository: MongoUserRepository) {
    fun followUser(command: FollowUserCommand): String {
        val follower = userRepository.findById(command.followerId)
        val followee = userRepository.findById(command.followeeId)

        if (follower == null || followee == null) {
            return "Uno de los usuarios no existe."
        }
        if (follower.getId() == followee.getId()) {
            return "No puedes seguirte a ti mismo."
        }
        if (follower.getFollowing().contains(followee.getId())) {
            return "Ya sigues a este usuario."
        }

        follower.getFollowing().add(followee.getId())
        followee.getFollowers().add(follower.getId())

        userRepository.save(follower)
        userRepository.save(followee)

        return "Siguiendo a ${followee.getUsername()} exitosamente."
    }
}