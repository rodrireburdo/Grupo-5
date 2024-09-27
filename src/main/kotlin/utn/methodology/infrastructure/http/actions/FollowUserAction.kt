package utn.methodology.infrastructure.http.actions
import utn.methodology.application.commands.FollowCommand
import utn.methodology.application.commandhandlers.FollowerQueryHandler
import utn.methodology.application.queries.FollowerQuery

class FollowUserAction(
    private val queryHandler: FollowQueryHandler
) {
    fun execute(query: FollowerQuery): Map<String, String>{
        query
            .validate()
            .let {
                return queryHandler.handle(it)
            }
    }
}