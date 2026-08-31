/*
 * Responsabilidad: Modelos de presentación simplificados para los contenidos destacados de Home.
 */

package com.miguel.portomuinos.ui.model

import androidx.annotation.DrawableRes

data class FeaturedRecipeUi(
    val id: String,
    val title: String,
    val timeLabel: String,
    val difficultyLabel: String,
    val difficultyLevel: Int,
    @DrawableRes val imageRes: Int? = null,
    val imageUrl: String? = null
)

data class FeaturedProductUi(
    val id: String,
    val name: String,
    val priceLabel: String? = null,
    @DrawableRes val imageRes: Int? = null,
    val imageUrl: String? = null
)