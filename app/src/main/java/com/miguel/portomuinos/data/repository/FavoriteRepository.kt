/*
 * Responsabilidad: Encapsula las operaciones de favoritos persistentes y aplica siempre el usuario
 * autenticado como ámbito.
 */

package com.miguel.portomuinos.data.repository

import com.miguel.portomuinos.data.remote.SupabaseClientProvider
import com.miguel.portomuinos.data.remote.dto.FavoriteProductDto
import com.miguel.portomuinos.data.remote.dto.FavoriteProductInsertDto
import com.miguel.portomuinos.data.remote.dto.FavoriteRecipeDto
import com.miguel.portomuinos.data.remote.dto.FavoriteRecipeInsertDto
import io.github.jan.supabase.postgrest.from

data class FavoriteData(
    val productIds: Set<String>,
    val recipeIds: Set<String>
)

object FavoriteRepository {

    private val client
        get() =
            SupabaseClientProvider.client

    private fun requireCurrentUserId(): String {
        return AuthRepository
            .getCurrentUserId()
            ?: error(
                "No hay un usuario autenticado."
            )
    }

    // Lectura de favoritos del usuario.

    suspend fun getFavorites(): FavoriteData {

        val userId =
            requireCurrentUserId()

        val favoriteProducts =
            client
                .from("favorite_products")
                .select {
                    filter {
                        eq(
                            "user_id",
                            userId
                        )
                    }
                }
                .decodeList<
                        FavoriteProductDto
                        >()

        val favoriteRecipes =
            client
                .from("favorite_recipes")
                .select {
                    filter {
                        eq(
                            "user_id",
                            userId
                        )
                    }
                }
                .decodeList<
                        FavoriteRecipeDto
                        >()

        return FavoriteData(
            productIds =
                favoriteProducts
                    .map {
                        it.productId
                    }
                    .toSet(),

            recipeIds =
                favoriteRecipes
                    .map {
                        it.recipeId
                    }
                    .toSet()
        )
    }

    // Favoritos de productos.

    suspend fun addProductFavorite(
        productId: String
    ) {

        val userId =
            requireCurrentUserId()

        client
            .from("favorite_products")
            .insert(
                FavoriteProductInsertDto(
                    userId = userId,
                    productId = productId
                )
            )
    }

    suspend fun removeProductFavorite(
        productId: String
    ) {

        val userId =
            requireCurrentUserId()

        client
            .from("favorite_products")
            .delete {
                filter {
                    eq(
                        "user_id",
                        userId
                    )

                    eq(
                        "product_id",
                        productId
                    )
                }
            }
    }

    // Favoritos de recetas.

    suspend fun addRecipeFavorite(
        recipeId: String
    ) {

        val userId =
            requireCurrentUserId()

        client
            .from("favorite_recipes")
            .insert(
                FavoriteRecipeInsertDto(
                    userId = userId,
                    recipeId = recipeId
                )
            )
    }

    suspend fun removeRecipeFavorite(
        recipeId: String
    ) {

        val userId =
            requireCurrentUserId()

        client
            .from("favorite_recipes")
            .delete {
                filter {
                    eq(
                        "user_id",
                        userId
                    )

                    eq(
                        "recipe_id",
                        recipeId
                    )
                }
            }
    }

    suspend fun clearProductFavorites() {

        val userId =
            requireCurrentUserId()

        client
            .from("favorite_products")
            .delete {
                filter {
                    eq(
                        "user_id",
                        userId
                    )
                }
            }
    }

    suspend fun clearRecipeFavorites() {

        val userId =
            requireCurrentUserId()

        client
            .from("favorite_recipes")
            .delete {
                filter {
                    eq(
                        "user_id",
                        userId
                    )
                }
            }
    }
}