package com.example.model

data class GeneratedProject(
    val title: String,
    val type: String, // "App" or "Game"
    val description: String,
    val category: String,
    val features: List<String>,
    val interactiveType: String, // "SPACE_DODGER", "BRICK_BREAKER", "TODO_PRO", "FITNESS_FLOW", "EXPENSE_TRACKER", "STOREFRONT", "MEMORY_GAME", "TAP_REFLEX"
    val colorHex: String,
    val iconName: String,
    val codeCompose: String,
    val codeFlutter: String,
    val architectureSchema: String
)

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: String, // "user" or "ai"
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isArtifact: Boolean = false,
    val artifact: GeneratedProject? = null
)

data class AppTemplate(
    val id: String,
    val title: String,
    val titleAr: String,
    val type: String, // "App" or "Game"
    val description: String,
    val descriptionAr: String,
    val category: String,
    val prompt: String,
    val interactiveType: String,
    val iconName: String,
    val badge: String
)
