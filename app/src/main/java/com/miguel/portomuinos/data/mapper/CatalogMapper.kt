/*
 * Responsabilidad: Transforma los DTO remotos del catálogo en modelos de presentación y resuelve
 * las URLs públicas de imágenes.
 */

package com.miguel.portomuinos.data.mapper

import com.miguel.portomuinos.BuildConfig
import com.miguel.portomuinos.data.remote.dto.ProductDto
import com.miguel.portomuinos.data.remote.dto.RecipeDto
import com.miguel.portomuinos.data.remote.dto.RecipeProductRelationDto
import com.miguel.portomuinos.ui.model.ProductCategory
import com.miguel.portomuinos.ui.model.ProductUi
import com.miguel.portomuinos.ui.model.RecipeCategory
import com.miguel.portomuinos.ui.model.RecipeDifficulty
import com.miguel.portomuinos.ui.model.RecipeProductRelationUi
import com.miguel.portomuinos.ui.model.RecipeUi

private const val CATALOG_BUCKET =
    "catalog-media"


// Mapeo de DTO remotos a modelos de presentación.

fun ProductDto.toUi(
    isFavorite: Boolean = false
): ProductUi {

    return ProductUi(
        id = id,
        name = name,
        subtitle = subtitle,
        description = description,
        price = price,
        category = category.toProductCategory(),
        rating = null,
        reviewCount = null,
        imageRes = null,
        imageUrl =
            imageUrl.toPublicCatalogUrl(),
        qrCode = qrCode,
        sourceUrl = sourceUrl,
        isFavorite = isFavorite
    )
}


fun RecipeDto.toUi(
    isFavorite: Boolean = false
): RecipeUi {

    return RecipeUi(
        id = id,
        title = name,
        description = description.orEmpty(),
        durationMinutes = durationMinutes,
        difficulty =
            difficulty.toRecipeDifficulty(),
        category =
            category.toRecipeCategory(),
        ingredients = ingredients,
        steps = steps,
        imageRes = null,
        imageUrl =
            imageUrl.toPublicCatalogUrl(),
        isFavorite = isFavorite
    )
}


fun RecipeProductRelationDto.toUi():
        RecipeProductRelationUi {

    return RecipeProductRelationUi(
        recipeId = recipeId,
        productId = productId
    )
}


// Conversión de valores persistidos a enums de UI.

private fun String.toProductCategory():
        ProductCategory {

    return when (uppercase()) {

        "ALGAS" ->
            ProductCategory.SEAWEED

        "ESPECIAS" ->
            ProductCategory.SPICES

        "KITS" ->
            ProductCategory.KITS

        else ->
            ProductCategory.SEAWEED
    }
}


private fun String.toRecipeDifficulty():
        RecipeDifficulty {

    return when (uppercase()) {

        "EASY" ->
            RecipeDifficulty.EASY

        "MEDIUM" ->
            RecipeDifficulty.MEDIUM

        "HARD" ->
            RecipeDifficulty.HARD

        else ->
            RecipeDifficulty.EASY
    }
}


private fun String.toRecipeCategory():
        RecipeCategory {

    return when (uppercase()) {

        "STARTER" ->
            RecipeCategory.STARTER

        "MAIN" ->
            RecipeCategory.MAIN

        "DESSERT" ->
            RecipeCategory.DESSERT

        else ->
            RecipeCategory.MAIN
    }
}


private fun String?.toPublicCatalogUrl():
        String? {

    if (isNullOrBlank()) {
        return null
    }

    /*
     * Admite tanto rutas relativas del bucket como URLs completas,
     * manteniendo el mapeo compatible con ambos formatos.
     */
    if (
        startsWith("http://") ||
        startsWith("https://")
    ) {
        return this
    }

    val baseUrl =
        BuildConfig.SUPABASE_URL
            .trimEnd('/')

    val storagePath =
        trimStart('/')

    return "$baseUrl/storage/v1/object/public/" +
            "$CATALOG_BUCKET/$storagePath"
}