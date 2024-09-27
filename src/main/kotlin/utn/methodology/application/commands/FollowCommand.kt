package utn.methodology.application.commands

data class FollowUserCommand(val followerId: String, val followeeId: String)
data class UnfollowUserCommand(val followerId: String, val followeeId: String)
