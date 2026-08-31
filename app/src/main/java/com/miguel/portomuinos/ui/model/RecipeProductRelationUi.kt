/*
 * Responsabilidad: Modelo de presentación de la relación muchos-a-muchos entre recetas y
 * productos.
 */

package com.miguel.portomuinos.ui.model

/*
 * Relación muchos-a-muchos entre recetas y productos
 * obtenida de la tabla intermedia recipe_products de Supabase.
 */
data class RecipeProductRelationUi(
    val recipeId: String,
    val productId: String
)
