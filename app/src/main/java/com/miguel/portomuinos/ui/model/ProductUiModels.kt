/*
 * Responsabilidad: Modelos de presentación de productos, categorías y formato de precios.
 */

package com.miguel.portomuinos.ui.model

import androidx.annotation.DrawableRes
import java.util.Locale

data class ProductUi(
    val id: String,
    val name: String,
    val subtitle: String = "",
    val description: String = "",
    val price: Double,
    val category: ProductCategory,
    val rating: Double? = null,
    val reviewCount: Int? = null,
    @DrawableRes val imageRes: Int? = null,
    val imageUrl: String? = null,
    val qrCode: String? = null,
    val sourceUrl: String? = null,
    val isFavorite: Boolean = false
)

enum class ProductCategory(
    val label: String
) {
    SEAWEED("Algas"),
    SPICES("Especias"),
    KITS("Kits")
}

fun Double.toPriceLabel(): String =
    String.format(
        Locale("es", "ES"),
        "%.2f €",
        this
    )