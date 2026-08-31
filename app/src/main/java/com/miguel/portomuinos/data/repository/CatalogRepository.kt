/*
 * Responsabilidad: Compone productos, recetas y relaciones del catálogo para ofrecerlos como una
 * única fuente de datos.
 */

package com.miguel.portomuinos.data.repository

import com.miguel.portomuinos.data.mapper.toUi
import com.miguel.portomuinos.data.remote.CatalogRemoteDataSource
import com.miguel.portomuinos.ui.model.ProductUi
import com.miguel.portomuinos.ui.model.RecipeProductRelationUi
import com.miguel.portomuinos.ui.model.RecipeUi

data class CatalogData(
    val products: List<ProductUi>,
    val recipes: List<RecipeUi>,
    val relations: List<RecipeProductRelationUi>
)

object CatalogRepository {

    // Recupera y transforma el catálogo completo en una sola operación.

    suspend fun getCatalog(): CatalogData {

        val products =
            CatalogRemoteDataSource
                .getProducts()
                .map { product ->
                    product.toUi()
                }

        val recipes =
            CatalogRemoteDataSource
                .getRecipes()
                .map { recipe ->
                    recipe.toUi()
                }

        val relations =
            CatalogRemoteDataSource
                .getRecipeProductRelations()
                .map { relation ->
                    relation.toUi()
                }

        return CatalogData(
            products = products,
            recipes = recipes,
            relations = relations
        )
    }
}