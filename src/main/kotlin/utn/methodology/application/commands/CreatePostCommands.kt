package utn.methodology.application.commands

data class CreatePostCommand(
    val userId: String,
    val message: String,
    val author: String
) {
    fun validate() {
        // Validación de los campos obligatorios
        if (userId.isBlank()) {
            throw IllegalArgumentException("El mensaje no puede estar vacío")
        }

        if (message.isBlank()) {
            throw IllegalArgumentException("El mensaje no puede estar vacío")
        }
        if (author.isBlank()) {
            throw IllegalArgumentException("El autor no puede estar vacío")
        }
    }
}