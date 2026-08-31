/*
 * Responsabilidad: Pruebas unitarias de búsqueda y normalización de texto en productos y recetas.
 */

package com.miguel.portomuinos.ui.search

import com.miguel.portomuinos.ui.model.ProductCategory
import com.miguel.portomuinos.ui.model.ProductUi
import com.miguel.portomuinos.ui.model.RecipeCategory
import com.miguel.portomuinos.ui.model.RecipeDifficulty
import com.miguel.portomuinos.ui.model.RecipeUi
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CatalogSearchTest {

    // Datos mínimos usados por los casos de prueba.

    private val product =
        ProductUi(
            id = "product_1",
            name = "Codium Deshidratado BIO",
            subtitle = "Alga atlántica",
            description =
                "Sabor marino intenso y textura crujiente.",
            price = 2.48,
            category =
                ProductCategory.SEAWEED
        )

    private val recipe =
        RecipeUi(
            id = "recipe_1",
            title = "Arroz con wakame",
            description =
                "Una receta ligera y sencilla.",
            durationMinutes = 25,
            difficulty =
                RecipeDifficulty.EASY,
            category =
                RecipeCategory.MAIN,
            ingredients =
                listOf(
                    "Arroz",
                    "Wakame",
                    "Limón"
                )
        )

    // Casos de búsqueda.

    @Test
    fun `una búsqueda vacía coincide con cualquier producto`() {
        assertTrue(
            product.matchesSearch("")
        )
    }

    @Test
    fun `la búsqueda de productos ignora mayúsculas y tildes`() {
        assertTrue(
            product.matchesSearch(
                "atlantica"
            )
        )
    }

    @Test
    fun `la búsqueda encuentra texto de la descripción del producto`() {
        assertTrue(
            product.matchesSearch(
                "crujiente"
            )
        )
    }

    @Test
    fun `la búsqueda encuentra la categoría del producto`() {
        assertTrue(
            product.matchesSearch(
                "algas"
            )
        )
    }

    @Test
    fun `la búsqueda de recetas encuentra ingredientes ignorando tildes`() {
        assertTrue(
            recipe.matchesSearch(
                "limon"
            )
        )
    }

    @Test
    fun `una consulta no relacionada no devuelve coincidencia`() {
        assertFalse(
            recipe.matchesSearch(
                "chocolate"
            )
        )
    }
}