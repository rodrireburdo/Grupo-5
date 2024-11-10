package utn.methodology.infrastructure.persistence.repositories

import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Sorts
import org.bson.Document
import utn.methodology.domain.entities.Post

class MongoPostRepository(private val database: MongoDatabase) {

    private val collection: MongoCollection<Document> = database.getCollection("posts")

    // Método para guardar un post
    fun save(post: Post) {
        val document = Document().apply {
            put("userId", post.userId)
            put("postId", post.postId)
            put("message", post.message)
            put("author", post.author)
            put("createdAt", post.createdAt)
        }
        collection.insertOne(document)
    }

    // Método para eliminar un post por ID
    fun deleteById(postId: String): Boolean {
        val deleteResult = collection.deleteOne(Filters.eq("postId", postId))
        return deleteResult.deletedCount > 0
    }

    fun findPosts(order: String, limit: Int, offset: Int, userId: String?): List<Post> {
        // Filtro para userId, si está presente
        val filters = userId?.let { Filters.eq("userId", it) } ?: Filters.empty()

        // Orden basado en el campo "createdAt"
        val sort = if (order == "DESC") Sorts.descending("createdAt") else Sorts.ascending("createdAt")

        // Consulta con filtros, orden, límite y desplazamiento
        val documents = collection.find(filters)
            .sort(sort)
            .skip(offset)
            .limit(limit)
            .toList()

        // Convertir los documentos MongoDB a objetos Post y devolver la lista
        return documents.map { docToPost(it) }
    }

    // Función de conversión de un documento MongoDB a un objeto Post
    fun docToPost(document: Document): Post {
        return Post(
            postId = document.getString("postId"),
            userId = document.getString("userId"),
            message = document.getString("message"),
            createdAt = document.getLong("createdAt"),
            author = document.getString("author")
        )
    }
}