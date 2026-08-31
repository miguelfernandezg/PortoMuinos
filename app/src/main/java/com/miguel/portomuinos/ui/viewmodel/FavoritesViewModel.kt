/*
 * Responsabilidad: Gestiona favoritos de productos y recetas con actualización optimista y
 * sincronización persistente.
 */

package com.miguel.portomuinos.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miguel.portomuinos.data.repository.FavoriteRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FavoritesUiState(
    val productIds: Set<String> = emptySet(),
    val recipeIds: Set<String> = emptySet()
)

/**
 * Gestiona los favoritos persistentes del usuario autenticado.
 *
 * La UI se actualiza de forma optimista. Para operaciones repetidas
 * sobre el mismo elemento, las escrituras se encadenan para mantener
 * el orden también en Supabase.
 */
class FavoritesViewModel : ViewModel() {

    // Estado observable y canales de sincronización.

    private val _uiState =
        MutableStateFlow(
            FavoritesUiState()
        )

    val uiState =
        _uiState.asStateFlow()

    private val _messages =
        MutableSharedFlow<UiMessage>(
            extraBufferCapacity = 4
        )

    val messages =
        _messages.asSharedFlow()

    private val productSyncJobs =
        mutableMapOf<String, Job>()

    private val recipeSyncJobs =
        mutableMapOf<String, Job>()

    // Carga inicial de favoritos.

    suspend fun loadFavorites() {

        val result =
            runCatching {
                FavoriteRepository
                    .getFavorites()
            }

        result.onSuccess { favorites ->

            _uiState.value =
                FavoritesUiState(
                    productIds =
                        favorites.productIds,
                    recipeIds =
                        favorites.recipeIds
                )
        }

        result.onFailure {

            _uiState.value =
                FavoritesUiState()

            _messages.emit(
                UiMessage(
                    "No se pudieron cargar tus favoritos."
                )
            )
        }
    }

    // Acciones sobre favoritos individuales.

    fun toggleProductFavorite(
        productId: String
    ) {

        val wasFavorite =
            productId in
                _uiState.value.productIds

        val shouldBeFavorite =
            !wasFavorite

        _uiState.update { state ->
            state.copy(
                productIds =
                    state.productIds
                        .withFavoriteState(
                            productId,
                            shouldBeFavorite
                        )
            )
        }

        val previousJob =
            productSyncJobs[productId]

        productSyncJobs[productId] =
            viewModelScope.launch {

                previousJob?.join()

                val result =
                    runCatching {
                        if (shouldBeFavorite) {
                            FavoriteRepository
                                .addProductFavorite(
                                    productId
                                )
                        } else {
                            FavoriteRepository
                                .removeProductFavorite(
                                    productId
                                )
                        }
                    }

                result.onFailure {

                    _uiState.update { state ->

                        val currentValue =
                            productId in
                                state.productIds

                        if (
                            currentValue !=
                            shouldBeFavorite
                        ) {
                            state
                        } else {
                            state.copy(
                                productIds =
                                    state.productIds
                                        .withFavoriteState(
                                            productId,
                                            wasFavorite
                                        )
                            )
                        }
                    }

                    _messages.emit(
                        UiMessage(
                            "No se pudo actualizar el favorito."
                        )
                    )
                }
            }
    }

    fun toggleRecipeFavorite(
        recipeId: String
    ) {

        val wasFavorite =
            recipeId in
                _uiState.value.recipeIds

        val shouldBeFavorite =
            !wasFavorite

        _uiState.update { state ->
            state.copy(
                recipeIds =
                    state.recipeIds
                        .withFavoriteState(
                            recipeId,
                            shouldBeFavorite
                        )
            )
        }

        val previousJob =
            recipeSyncJobs[recipeId]

        recipeSyncJobs[recipeId] =
            viewModelScope.launch {

                previousJob?.join()

                val result =
                    runCatching {
                        if (shouldBeFavorite) {
                            FavoriteRepository
                                .addRecipeFavorite(
                                    recipeId
                                )
                        } else {
                            FavoriteRepository
                                .removeRecipeFavorite(
                                    recipeId
                                )
                        }
                    }

                result.onFailure {

                    _uiState.update { state ->

                        val currentValue =
                            recipeId in
                                state.recipeIds

                        if (
                            currentValue !=
                            shouldBeFavorite
                        ) {
                            state
                        } else {
                            state.copy(
                                recipeIds =
                                    state.recipeIds
                                        .withFavoriteState(
                                            recipeId,
                                            wasFavorite
                                        )
                            )
                        }
                    }

                    _messages.emit(
                        UiMessage(
                            "No se pudo actualizar el favorito."
                        )
                    )
                }
            }
    }

    // Operaciones de vaciado por tipo.

    fun clearRecipeFavorites() {

        val previousFavorites =
            _uiState.value.recipeIds

        if (previousFavorites.isEmpty()) {
            return
        }

        _uiState.update {
            it.copy(
                recipeIds = emptySet()
            )
        }

        viewModelScope.launch {

            recipeSyncJobs
                .values
                .toList()
                .forEach {
                    it.join()
                }

            val result =
                runCatching {
                    FavoriteRepository
                        .clearRecipeFavorites()
                }

            result.onFailure {

                _uiState.update {
                    it.copy(
                        recipeIds =
                            previousFavorites
                    )
                }

                _messages.emit(
                    UiMessage(
                        "No se pudieron eliminar los favoritos de recetas."
                    )
                )
            }
        }
    }

    fun clearProductFavorites() {

        val previousFavorites =
            _uiState.value.productIds

        if (previousFavorites.isEmpty()) {
            return
        }

        _uiState.update {
            it.copy(
                productIds = emptySet()
            )
        }

        viewModelScope.launch {

            productSyncJobs
                .values
                .toList()
                .forEach {
                    it.join()
                }

            val result =
                runCatching {
                    FavoriteRepository
                        .clearProductFavorites()
                }

            result.onFailure {

                _uiState.update {
                    it.copy(
                        productIds =
                            previousFavorites
                    )
                }

                _messages.emit(
                    UiMessage(
                        "No se pudieron eliminar los favoritos de productos."
                    )
                )
            }
        }
    }

    fun clearLocalState() {

        productSyncJobs
            .values
            .forEach {
                it.cancel()
            }

        recipeSyncJobs
            .values
            .forEach {
                it.cancel()
            }

        productSyncJobs.clear()
        recipeSyncJobs.clear()

        _uiState.value =
            FavoritesUiState()
    }

    // Utilidad para aplicar un estado de favorito sin duplicar lógica.

    private fun Set<String>.withFavoriteState(
        id: String,
        isFavorite: Boolean
    ): Set<String> {

        return if (isFavorite) {
            this + id
        } else {
            this - id
        }
    }
}
