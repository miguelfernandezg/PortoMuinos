/*
 * Responsabilidad: Carga y mantiene disponible el catálogo remoto para las pantallas de la
 * aplicación.
 */

package com.miguel.portomuinos.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miguel.portomuinos.data.repository.CatalogData
import com.miguel.portomuinos.data.repository.CatalogRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CatalogUiState(
    val catalogData: CatalogData? = null,
    val isLoading: Boolean = true
)

/**
 * Mantiene el catálogo remoto disponible para todas las pantallas.
 */
class CatalogViewModel : ViewModel() {

    // Estado del catálogo compartido por las pantallas.

    private val _uiState =
        MutableStateFlow(
            CatalogUiState()
        )

    val uiState =
        _uiState.asStateFlow()

    private val _messages =
        MutableSharedFlow<UiMessage>(
            extraBufferCapacity = 2
        )

    val messages =
        _messages.asSharedFlow()

    init {
        loadCatalog()
    }

    // Carga remota del catálogo.

    fun loadCatalog() {

        viewModelScope.launch {

            _uiState.update {
                it.copy(
                    isLoading = true
                )
            }

            val result =
                runCatching {
                    CatalogRepository
                        .getCatalog()
                }

            result.onSuccess { catalog ->

                _uiState.value =
                    CatalogUiState(
                        catalogData = catalog,
                        isLoading = false
                    )
            }

            result.onFailure {

                _uiState.update {
                    it.copy(
                        isLoading = false
                    )
                }

                _messages.emit(
                    UiMessage(
                        "No se pudo cargar el catálogo."
                    )
                )
            }
        }
    }
}
