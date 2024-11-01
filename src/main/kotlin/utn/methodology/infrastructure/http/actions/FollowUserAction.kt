package utn.methodology.infrastructure.http.actions
import utn.methodology.application.commands.FollowUserCommand
import utn.methodology.application.queryhandlers.FollowerQueryHandler
import utn.methodology.application.queries.GetFollowersQuery

class FollowUserAction(
    private val queryHandler: FollowerQueryHandler
) {
    fun execute(query: GetFollowersQuery): Map<String, String>{
        query
            .validate()
            .let {
                return queryHandler.handle(it)
            }
    }
}