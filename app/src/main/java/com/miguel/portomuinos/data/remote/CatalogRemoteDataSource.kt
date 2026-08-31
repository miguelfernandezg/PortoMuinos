/*
 * Responsabilidad: Accede a las tablas públicas de Supabase y devuelve los DTO del catálogo sin
 * lógica de presentación.
 */

package com.miguel.portomuinos.data.remote

import com.miguel.portomuinos.data.remote.dto.ProductDto
import com.miguel.portomuinos.data.remote.dto.RecipeDto
import com.miguel.portomuinos.data.remote.dto.RecipeProductRelationDto
import io.github.jan.supabase.postgrest.from

object CatalogRemoteDataSource {

    suspend fun getProducts(): List<ProductDto> {
        return SupabaseClientProvider
            .client
            .from("products")
            .select()
            .decodeList<ProductDto>()
    }

    suspend fun getRecipes(): List<RecipeDto> {
        return SupabaseClientProvider
            .client
            .from("recipes")
            .select()
            .decodeList<RecipeDto>()
    }

    suspend fun getRecipeProductRelations():
            List<RecipeProductRelationDto> {

        return SupabaseClientProvider
            .client
            .from("recipe_products")
            .select()
            .decodeList<RecipeProductRelationDto>()
    }
}