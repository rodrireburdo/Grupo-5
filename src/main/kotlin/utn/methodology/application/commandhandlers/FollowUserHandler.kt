package utn.methodology.application.commandhandlers
import jdk.jfr.internal.Repository
import utn.methodology.domain.entities.User
import utn.methodology.infrastructure.persistence.repositories.MongoUserRepository
import utn.methodology.application.commands.FollowCommand



class FollowUserHandler (private val userRepository: MongoUserRepository)
{
    fun followUser(command: FollowUserCommand): String {
        val follower = userRepository.findById(command.followerId)
        val followee = userRepository.findById(command.followeeId)

        if (follower == null || followee == null) {
            return "Uno de los usuarios no existe."
        }
        if (follower.id == followee.id) {
            return "No puedes seguirte a ti mismo."
        }
        if (follower.following.contains(followee.id)) {
            return "Ya sigues a este usuario."
        }

        follower.following.add(followee.id)
        followee.followers.add(follower.id)

        userRepository.save(follower)
        userRepository.save(followee)

        return "Siguiendo a ${followee.username} exitosamente."
    
}