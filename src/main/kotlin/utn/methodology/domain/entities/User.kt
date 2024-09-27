package utn.methodology.domain.entities

class User(
    private val id: String,
    private var name: String,
    private var username: String,
    private val email: String,
    private var password: String,
    private val followers: MutableSet<String> = mutableSetOf(),
    private val following: MutableSet<String> = mutableSetOf(),
)
{
    //En vez de usar as String, uso ?: "" para asegurar que si algún valor es null, se le asigne una cadena vacía ("")
    //y evita excepciones debido a valores nulos o tipos inesperados.
    companion object {
        fun fromPrimitives(primitives: Map<String, String>): User {
            val user = User(
                primitives["id"] ?: "",
                primitives["name"] ?: "",
                primitives["username"] ?: "",
                primitives["email"] ?: "",
                primitives["password"] ?: "",
                primitives["followers"] ?: "",
                primitives["following"] ?: ""
            );
            return user;
        }
    }

    fun getName(): String{
        return this.name
    }

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
            "followers" to this.followers,
            "following" to this.following
        )
    }

    fun getId(): String {
        return this.id
    }
}