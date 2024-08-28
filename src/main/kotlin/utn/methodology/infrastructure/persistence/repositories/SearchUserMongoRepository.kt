//package utn.methodology.infrastructure.persistence.repositories
//
//import utn.methodology.domain.entities.User
//import utn.methodology.infrastructure.persistence.repositories.SearchUserMongoRepository
//import com.mongodb.client.MongoCollection
//import com.mongodb.client.MongoDatabase
//import com.mongodb.client.model.Filters
//import utn.methodology.infrastructure.persistence.MongoUserRepository
//import org.bson.Document
//
//val collectionName: String = "NOMBRE_BASE_DATOS"
//
//class SearchUserMongoRepository(
//    private val db = MongoDatabase
//) : SearchUserRepository {
//
//    private val collection: MongoCollection<Document>
//
//    init {
//        collection = this.db.getCollection(collectionName)
//    }
//
//    override fun searchUsername(username: String): User? {
//        val filterUsername = Filters.eq("username", username)
//        val document = collection.find(filterUsername).firstOrNull()
//
//        return document?.let {
//            User(
//                uuid = it.getString("uuid"),
//                name = it.getString("name"),
//                username = it.getString("username"),
//                email = it.getString("email")
//            )
//        }
//    }
//}