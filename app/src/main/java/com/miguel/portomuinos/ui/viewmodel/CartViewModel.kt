/*
 * Responsabilidad: Gestiona el estado reactivo del carrito y sincroniza las modificaciones con
 * Supabase en orden.
 */

package com.miguel.portomuinos.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miguel.portomuinos.data.repository.CartRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CartUiState(
    val quantities: Map<String, Int> = emptyMap()
)

/**
 * Gestiona el carrito persistente del usuario autenticado.
 *
 * Las escrituras remotas se encadenan para conservar el mismo orden
 * que las acciones del usuario, evitando que cambios rápidos de cantidad
 * puedan terminar persistidos fuera de orden.
 */
class CartViewModel : ViewModel() {

    // Estado observable y canal de mensajes.

    private val _uiState =
        MutableStateFlow(
            CartUiState()
        )

    val uiState =
        _uiState.asStateFlow()

    private val _messages =
        MutableSharedFlow<UiMessage>(
            extraBufferCapacity = 6
        )

    val messages =
        _messages.asSharedFlow()

    private var syncJob: Job? =
        null

    // Carga inicial del carrito.

    suspend fun loadCart() {

        val result =
            runCatching {
                CartRepository
                    .getCart()
            }

        result.onSuccess { cart ->

            _uiState.value =
                CartUiState(
                    quantities =
                        cart.quantities
                )
        }

        result.onFailure {

            _uiState.value =
                CartUiState()

            _messages.emit(
                UiMessage(
                    "No se pudo cargar tu carrito."
                )
            )
        }
    }

    // Acciones públicas de modificación.

    fun addToCart(
        productId: String,
        quantity: Int,
        productName: String
    ) {

        if (quantity <= 0) {
            return
        }

        val current =
            _uiState.value
                .quantities[productId]
                ?: 0

        setProductQuantity(
            productId = productId,
            newQuantity =
                current + quantity,
            successMessage =
                "$productName añadido al carrito"
        )
    }

    fun increaseQuantity(
        productId: String
    ) {

        val current =
            _uiState.value
                .quantities[productId]
                ?: 0

        setProductQuantity(
            productId = productId,
            newQuantity =
                current + 1
        )
    }

    fun decreaseQuantity(
        productId: String
    ) {

        val current =
            _uiState.value
                .quantities[productId]
                ?: 0

        if (current <= 0) {
            return
        }

        setProductQuantity(
            productId = productId,
            newQuantity =
                current - 1
        )
    }

    fun removeProduct(
        productId: String
    ) {

        if (
            productId !in
            _uiState.value.quantities
        ) {
            return
        }

        setProductQuantity(
            productId = productId,
            newQuantity = 0
        )
    }

    fun clearCart() {

        val previousQuantities =
            _uiState.value.quantities

        if (previousQuantities.isEmpty()) {
            return
        }

        _uiState.value =
            CartUiState()

        enqueueSync {

            val result =
                runCatching {
                    CartRepository
                        .clearCart()
                }

            result.onFailure {

                restoreCartFromServerIfStill(
                    expectedQuantities =
                        emptyMap()
                )

                _messages.emit(
                    UiMessage(
                        "No se pudo vaciar el carrito."
                    )
                )
            }
        }
    }

    fun clearLocalState() {

        syncJob?.cancel()
        syncJob = null

        _uiState.value =
            CartUiState()
    }

    // Sincronización interna con Supabase.

    private fun setProductQuantity(
        productId: String,
        newQuantity: Int,
        successMessage: String? = null
    ) {

        val previousQuantity =
            _uiState.value
                .quantities[productId]
                ?: 0

        _uiState.update { state ->

            val updated =
                if (newQuantity <= 0) {
                    state.quantities -
                        productId
                } else {
                    state.quantities +
                        (
                            productId to
                                newQuantity
                        )
                }

            state.copy(
                quantities = updated
            )
        }

        enqueueSync {

            val result =
                runCatching {

                    if (newQuantity <= 0) {
                        CartRepository
                            .removeProduct(
                                productId
                            )
                    } else {
                        CartRepository
                            .setProductQuantity(
                                productId =
                                    productId,
                                quantity =
                                    newQuantity
                            )
                    }
                }

            result.onSuccess {

                if (
                    !successMessage
                        .isNullOrBlank()
                ) {
                    _messages.emit(
                        UiMessage(
                            text =
                                successMessage,
                            transient = true
                        )
                    )
                }
            }

            result.onFailure {

                val currentQuantity =
                    _uiState.value
                        .quantities[productId]
                        ?: 0

                if (
                    currentQuantity ==
                    newQuantity
                ) {

                    val remoteResult =
                        runCatching {
                            CartRepository
                                .getCart()
                        }

                    remoteResult.onSuccess {
                            cart ->
                            _uiState.value =
                                CartUiState(
                                    quantities =
                                        cart.quantities
                                )
                        }

                    remoteResult.onFailure {

                        _uiState.update { state ->

                            val restored =
                                if (
                                    previousQuantity <= 0
                                ) {
                                    state.quantities -
                                        productId
                                } else {
                                    state.quantities +
                                        (
                                            productId to
                                                previousQuantity
                                        )
                                }

                            state.copy(
                                quantities =
                                    restored
                            )
                        }
                    }
                }

                _messages.emit(
                    UiMessage(
                        "No se pudo actualizar el carrito."
                    )
                )
            }
        }
    }

    private fun enqueueSync(
        operation:
            suspend () -> Unit
    ) {

        val previousJob =
            syncJob

        syncJob =
            viewModelScope.launch {

                previousJob?.join()

                operation()
            }
    }

    private suspend fun restoreCartFromServerIfStill(
        expectedQuantities: Map<String, Int>
    ) {

        if (
            _uiState.value.quantities !=
            expectedQuantities
        ) {
            return
        }

        val remoteResult =
            runCatching {
                CartRepository
                    .getCart()
            }

        remoteResult.onSuccess {
                cart ->
            _uiState.value =
                CartUiState(
                    quantities =
                        cart.quantities
                )
        }
    }
}
