package utn.methodology.infrastructure.persistence.repositories
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import utn.methodology.domain.entities.Post
import org.bson.Document

import com.mongodb.client.model.Filters


class MongoPostRepository(private val database: MongoDatabase) {

    private val collection: MongoCollection<Document> = database.getCollection("posts")

    fun save(post: Post) {
        val document = Document().apply {
            put("userId", post.userId.toString())
            put("postId", post.postId)
            put("message", post.message)
            put("author", post.author)
            put("createdAt", post.createdAt)
        }
        collection.insertOne(document)
    }

    fun deleteById(postId: String): Boolean {
        val deleteResult = collection.deleteOne(Filters.eq("postId", postId))
        return deleteResult.deletedCount > 0
    }
}
