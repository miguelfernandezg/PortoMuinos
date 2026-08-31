/*
 * Responsabilidad: Define los contratos serializables utilizados para leer y escribir elementos
 * del carrito en Supabase.
 */

package com.miguel.portomuinos.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CartItemDto(
    @SerialName("user_id")
    val userId: String,

    @SerialName("product_id")
    val productId: String,

    val quantity: Int,

    @SerialName("created_at")
    val createdAt: String
)

@Serializable
data class CartItemUpsertDto(
    @SerialName("user_id")
    val userId: String,

    @SerialName("product_id")
    val productId: String,

    val quantity: Int
)