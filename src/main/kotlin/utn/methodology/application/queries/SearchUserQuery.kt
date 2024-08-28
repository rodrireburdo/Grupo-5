package utn.methodology.application.queries

data class SearchUserQuery(
    val username: String
)
{ fun validate(): SearchUserQuery {

    checkNotNull(username) {throw IllegalArgumentException("name must be defined")}
    return this

}}