package utn.methodology.application.queryhandlers

import utn.methodology.application.queries.SearchPostQuery
import utn.methodology.application.queries.GetFollowingQuery
import utn.methodology.infrastructure.persistence.repositories.MongoPostRepository
import utn.methodology.application.queryhandlers.FollowerQueryHandler

class SearchPostQueryHandler(
    private val postRepository: MongoPostRepository,
    private val followerQueryHandler: FollowerQueryHandler
) {
    fun handle(query: SearchPostQuery): List<Post> {

        val following = followerQueryHandler.handleGetFollowing(GetFollowingQuery(query.userId))


        if (following.isNullOrEmpty()) {
            return emptyList()
        }


        return postRepository.findPostsByUsers(following).sortedByDescending { it.creationDate }
    }
}
