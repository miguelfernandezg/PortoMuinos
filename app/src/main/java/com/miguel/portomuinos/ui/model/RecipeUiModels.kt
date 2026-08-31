/*
 * Responsabilidad: Modelos de presentación de recetas, dificultad y categorías culinarias.
 */

package com.miguel.portomuinos.ui.model

import androidx.annotation.DrawableRes

data class RecipeUi(
    val id: String,
    val title: String,
    val description: String = "",
    val durationMinutes: Int,
    val difficulty: RecipeDifficulty,
    val category: RecipeCategory,
    val ingredients: List<String> = emptyList(),
    val steps: List<String> = emptyList(),
    @DrawableRes val imageRes: Int? = null,
    val imageUrl: String? = null,
    val isFavorite: Boolean = false
)

enum class RecipeDifficulty(
    val label: String,
    val level: Int
) {
    EASY("Fácil", 1),
    MEDIUM("Media", 2),
    HARD("Difícil", 3)
}

enum class RecipeCategory(
    val label: String
) {
    STARTER("Entrantes"),
    MAIN("Primeros"),
    DESSERT("Postres")
}
