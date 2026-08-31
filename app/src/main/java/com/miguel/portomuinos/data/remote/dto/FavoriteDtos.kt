/*
 * Responsabilidad: Define los contratos serializables de favoritos de productos y recetas para las
 * operaciones con Supabase.
 */

package com.miguel.portomuinos.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FavoriteProductDto(
    @SerialName("user_id")
    val userId: String,

    @SerialName("product_id")
    val productId: String,

    @SerialName("created_at")
    val createdAt: String
)

@Serializable
data class FavoriteProductInsertDto(
    @SerialName("user_id")
    val userId: String,

    @SerialName("product_id")
    val productId: String
)

@Serializable
data class FavoriteRecipeDto(
    @SerialName("user_id")
    val userId: String,

    @SerialName("recipe_id")
    val recipeId: String,

    @SerialName("created_at")
    val createdAt: String
)

@Serializable
data class FavoriteRecipeInsertDto(
    @SerialName("user_id")
    val userId: String,

    @SerialName("recipe_id")
    val recipeId: String
)