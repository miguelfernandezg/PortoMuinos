/*
 * Responsabilidad: Representa la estructura remota de una receta tal y como se almacena en la
 * tabla recipes.
 */

package com.miguel.portomuinos.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecipeDto(

    val id: String,

    val name: String,

    val description: String?,

    @SerialName("duration_minutes")
    val durationMinutes: Int,

    val difficulty: String,

    val category: String,

    val ingredients: List<String>,

    val steps: List<String>,

    @SerialName("image_url")
    val imageUrl: String?,

    @SerialName("source_url")
    val sourceUrl: String?,

    @SerialName("created_at")
    val createdAt: String
)