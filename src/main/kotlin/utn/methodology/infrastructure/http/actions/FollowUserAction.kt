package utn.methodology.infrastructure.http.actions

import utn.methodology.application.commandhandlers.FollowUserHandler
import utn.methodology.application.commands.FollowUserCommand
import utn.methodology.application.queryhandlers.FollowerQueryHandler
import utn.methodology.application.queries.GetFollowersQuery

class FollowUserAction(
    private val followUserHandler: FollowUserHandler
) {
    fun execute(command: FollowUserCommand): Map<String, String> {
        try {
            // Execute the logic to follow a user
            return mapOf("mensaje" to followUserHandler.followUser(command))
        } catch (e: Exception) {
            return mapOf("error" to "Error al seguir al usuario: ${e.message}")
        }
    }
}
