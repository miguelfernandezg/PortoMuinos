/*
 * Responsabilidad: Representa la relación remota entre recetas y productos almacenada en
 * recipe_products.
 */

package com.miguel.portomuinos.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecipeProductRelationDto(

    @SerialName("recipe_id")
    val recipeId: String,

    @SerialName("product_id")
    val productId: String
)