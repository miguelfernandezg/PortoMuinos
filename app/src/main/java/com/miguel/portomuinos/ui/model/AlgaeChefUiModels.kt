/*
 * Responsabilidad: Modelos de presentación utilizados por la conversación y las recomendaciones
 * del demostrador AlgaeChef.
 */

package com.miguel.portomuinos.ui.model

data class AlgaeChefSuggestionUi(
    val id: String,
    val label: String,
    val prompt: String
)

data class AlgaeChefResultUi(
    val recommendedSeaweed: String,
    val reason: String,
    val recipeId: String
)

enum class AlgaeChefMessageRole {
    USER,
    ASSISTANT
}

data class AlgaeChefMessageUi(
    val id: Long,
    val role: AlgaeChefMessageRole,
    val text: String,
    val result: AlgaeChefResultUi? = null
)
