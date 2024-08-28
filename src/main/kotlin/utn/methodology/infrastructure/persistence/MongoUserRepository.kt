package utn.methodology.infrastructure.persistence
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.UpdateOptions
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
            collection = database.getCollection("users") as MongoCollection<Document>
            val options = UpdateOptions().upsert(true)
            val filter = Document("_id", user.getId())
            val update = Document(" \$set", user.toPrimitives())

            collection.updateOne(filter, update, options)
    }
    fun findOne(id: String): User? {
        val filter = Document("_id", id);

        val primitives = collection.find(filter).firstOrNull();

        if (primitives == null) {
            return null;
        }

        return User.fromPrimitives(primitives as Map<String, String>)
    }

    fun findAll(): List<User> {

        val primitives = collection.find().map { it as Document }.toList();

        return primitives.map {
            User.fromPrimitives(it.toMap() as Map<String, String>)
        };
    }

    fun delete(user: User) {
        val filter = Document("_id", user.getId());

        collection.deleteOne(filter)
    }
}











