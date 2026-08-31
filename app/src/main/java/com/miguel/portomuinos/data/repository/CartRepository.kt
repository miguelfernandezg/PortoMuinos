/*
 * Responsabilidad: Encapsula la lectura y escritura del carrito persistente asociado al usuario
 * autenticado.
 */

package com.miguel.portomuinos.data.repository

import com.miguel.portomuinos.data.remote.SupabaseClientProvider
import com.miguel.portomuinos.data.remote.dto.CartItemDto
import com.miguel.portomuinos.data.remote.dto.CartItemUpsertDto
import io.github.jan.supabase.postgrest.from

data class CartData(
    val quantities: Map<String, Int>
)

object CartRepository {

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

    // Lectura del carrito del usuario.

    suspend fun getCart(): CartData {

        val userId =
            requireCurrentUserId()

        val items =
            client
                .from("cart_items")
                .select {
                    filter {
                        eq(
                            "user_id",
                            userId
                        )
                    }
                }
                .decodeList<CartItemDto>()

        return CartData(
            quantities =
                items.associate {
                    it.productId to
                            it.quantity
                }
        )
    }

    // Operaciones de escritura.

    suspend fun setProductQuantity(
        productId: String,
        quantity: Int
    ) {

        if (quantity <= 0) {
            removeProduct(
                productId
            )
            return
        }

        val userId =
            requireCurrentUserId()

        client
            .from("cart_items")
            .upsert(
                CartItemUpsertDto(
                    userId = userId,
                    productId = productId,
                    quantity = quantity
                )
            )
    }

    suspend fun removeProduct(
        productId: String
    ) {

        val userId =
            requireCurrentUserId()

        client
            .from("cart_items")
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

    suspend fun clearCart() {

        val userId =
            requireCurrentUserId()

        client
            .from("cart_items")
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