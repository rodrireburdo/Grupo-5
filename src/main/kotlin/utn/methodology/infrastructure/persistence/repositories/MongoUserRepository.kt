package utn.methodology.infrastructure.persistence.repositories
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates
import utn.methodology.domain.entities.User
import org.bson.Document
import java.util.UUID

class MongoUserRepository(private val database: MongoDatabase) {

    private var collection: MongoCollection<Document>

    init {
        // Inicializa la colección de usuarios de la base de datos MongoDB.
        collection = database.getCollection("users") as MongoCollection<Document>
    }

    fun save(user: User){
        val options = UpdateOptions().upsert(true)
        val filter = Document("_id", user.getId())
        val update = Document("\$set", user.toPrimitives())

        collection.updateOne(filter, update, options)
    }
    fun findByName(name: String): User? {
        val filter = Document("username", name)

        val primitives = collection.find(filter).firstOrNull() ?: return null

        return User.fromPrimitives(primitives as Map<String, String>)
    }

    fun findAll(): List<User> {

        val primitives = collection.find().map { it as Document }.toList();

        return primitives.map {
            User.fromPrimitives(it.toMap() as Map<String, String>)
        };
    }

    fun delete(user: User) {
        val filter = Document("_name", user.getName());

        collection.deleteOne(filter)
    }

    fun findById(userId: String): User? {
        val filter = Document("_id", userId)

        val primitives = collection.find(filter).firstOrNull() ?: return null;

        return User.fromPrimitives(primitives as Map<String, String>)
    }

    // Método para seguir a un usuario
    fun followUser(followerId: String, userName: String): Boolean {
        // Buscar al usuario con el nombre de usuario proporcionado
        val user = collection.find(Document("username", userName)).firstOrNull()
        val follower = collection.find(Document("_id", followerId)).firstOrNull()

        if (user == null || follower == null) {
            return false // Si uno de los dos usuarios no existe, no se puede seguir
        }

        // Asegurarse de que el usuario no se esté siguiendo a sí mismo
        if (user.getString("_id") == follower.getString("_id")) {
            return false // No se puede seguir a uno mismo
        }

        // Agregar el followerId al campo 'followers' del usuario seguido
        val updateFollowers = collection.updateOne(
            Document("_id", user.getString("_id")),
            Document("\$addToSet", Document("followers", followerId))
        )

        // Agregar el userId al campo 'following' del usuario seguidor
        val updateFollowing = collection.updateOne(
            Document("_id", followerId),
            Document("\$addToSet", Document("following", user.getString("_id")))
        )

        return updateFollowers.modifiedCount > 0 && updateFollowing.modifiedCount > 0
    }
}








