package utn.methodology.application.commands

data class CreatePostCommand(
    val userId: String,
    val message: String,
    val createdAt: Long,
    val author: String
) {
    fun validate() {
        // Validación del userId como número positivo
        val userIdInt = userId.toIntOrNull()
        if (userIdInt == null || userIdInt <= 0) {
            throw IllegalArgumentException("El userId debe ser un número positivo")
        }

        // Validación de los campos obligatorios
        if (message.isBlank()) {
            throw IllegalArgumentException("El mensaje no puede estar vacío")
        }
        if (author.isBlank()) {
            throw IllegalArgumentException("El autor no puede estar vacío")
        }
    }
}