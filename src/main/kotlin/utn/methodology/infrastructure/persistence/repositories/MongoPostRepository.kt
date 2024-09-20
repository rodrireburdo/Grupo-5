package utn.methodology.infrastructure.persistence.repositories
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.UpdateOptions
import utn.methodology.domain.entities.Post
import org.bson.Document
import java.util.UUID

object MongoPostRepository {
    private val posts = mutableListOf<Post>()

    fun getPosts(userId: String, order: String?, limit: Int?, offset: Int?): List<Post> {
        val userPosts = posts.filter { it.userId == userId }

        val sortedPosts = when (order) {
            "ASC" -> userPosts.sortedBy { it.creationDate }
            "DESC" -> userPosts.sortedByDescending { it.creationDate }
            else -> userPosts
        }

        return sortedPosts.drop(offset ?: 0).take(limit ?: sortedPosts.size)
    }
}
