/*
 * Responsabilidad: Representa la estructura remota de un producto tal y como se almacena en la
 * tabla products.
 */

package com.miguel.portomuinos.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductDto(

    val id: String,

    val name: String,

    val subtitle: String,

    val description: String,

    val price: Double,

    val category: String,

    val sku: String?,

    @SerialName("qr_code")
    val qrCode: String?,

    @SerialName("image_url")
    val imageUrl: String?,

    @SerialName("source_url")
    val sourceUrl: String?,

    @SerialName("created_at")
    val createdAt: String
)