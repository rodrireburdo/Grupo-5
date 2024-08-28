package utn.methodology.infrastructure.http.actions

import utn.methodology.application.queries.SearchUserQuery
import utn.methodology.application.queryhandlers.SearchUserQueryHandler
import utn.methodology.domain.entities.User

class SearchUserAction(
    private val queryHandler: SearchUserQueryHandler
) {
    fun execute(query: SearchUserQuery): Map<String, String>{
        query
            .validate()
            .let {
                return queryHandler.handle(it)
            }
    }
}


