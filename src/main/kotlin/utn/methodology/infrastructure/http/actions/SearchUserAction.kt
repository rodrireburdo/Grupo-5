package utn.methodology.infrastructure.http.actions

import utn.methodology.application.queries.SearchUserQuery
import utn.methodology.application.queryhandlers.SearchUserQueryHandler
import utn.methodology.domain.entities.UserResponseDTO

class SearchUserAction(
    private val queryHandler: SearchUserQueryHandler
) {
    fun handle(query: SearchUserQuery): UserResponseDTO? {
        return queryHandler.handle(query)
    }
}