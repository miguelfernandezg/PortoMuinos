/*
 * Responsabilidad: Implementa la normalización y coincidencia de búsquedas para productos y
 * recetas.
 */

package com.miguel.portomuinos.ui.search

import com.miguel.portomuinos.ui.model.ProductUi
import com.miguel.portomuinos.ui.model.RecipeUi
import java.text.Normalizer
import java.util.Locale

private val diacriticsRegex =
    "\\p{M}+".toRegex()

private fun String.normalizedSearchText(): String {

    val normalized =
        Normalizer.normalize(
            this,
            Normalizer.Form.NFD
        )

    return diacriticsRegex
        .replace(
            normalized,
            ""
        )
        .lowercase(Locale.ROOT)
        .trim()
}

// Búsqueda sobre recetas.

fun RecipeUi.matchesSearch(
    query: String
): Boolean {

    val normalizedQuery =
        query.normalizedSearchText()

    if (normalizedQuery.isBlank()) {
        return true
    }

    val searchableContent =
        listOf(
            title,
            description,
            category.label,
            ingredients.joinToString(" ")
        )

    return searchableContent.any {
            content ->

        content
            .normalizedSearchText()
            .contains(normalizedQuery)
    }
}

// Búsqueda sobre productos.

fun ProductUi.matchesSearch(
    query: String
): Boolean {

    val normalizedQuery =
        query.normalizedSearchText()

    if (normalizedQuery.isBlank()) {
        return true
    }

    val searchableContent =
        listOf(
            name,
            subtitle,
            description,
            category.label
        )

    return searchableContent.any {
            content ->

        content
            .normalizedSearchText()
            .contains(normalizedQuery)
    }
}