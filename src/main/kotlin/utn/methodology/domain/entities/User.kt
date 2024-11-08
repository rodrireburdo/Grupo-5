package utn.methodology.domain.entities

class User(
    private val id: String,
    private var name: String,
    private var username: String,
    private val email: String,
    private var password: String,
    val followers: MutableSet<String> = mutableSetOf(),
    val following: MutableSet<String> = mutableSetOf(),
) {
    companion object {
        fun fromPrimitives(primitives: Map<String, String>): User {
            val followers = primitives["followers"]?.split(",")?.toMutableSet() ?: mutableSetOf()
            val following = primitives["following"]?.split(",")?.toMutableSet() ?: mutableSetOf()

            return User(
                primitives["id"] ?: "",
                primitives["name"] ?: "",
                primitives["username"] ?: "",
                primitives["email"] ?: "",
                primitives["password"] ?: "",
                followers,
                following
            )
        }
    }

    // Cambia los getters para devolver MutableSet
    fun getFollowers(): MutableSet<String> = followers
    fun getFollowing(): MutableSet<String> = following

    fun getId(): String = id
    fun getName(): String = name
    fun getUsername(): String = username

    fun update(name: String, username: String, password: String) {
        this.name = name
        this.username = username
        this.password = password
    }

    fun toPrimitives(): Map<String, String> {
        return mapOf(
            "id" to this.id,
            "name" to this.name,
            "username" to this.username,
            "email" to this.email,
            "password" to this.password,
            "followers" to this.followers.joinToString(","),
            "following" to this.following.joinToString(",")
        )
    }
}
