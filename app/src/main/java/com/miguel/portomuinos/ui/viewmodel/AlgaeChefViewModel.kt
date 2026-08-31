/*
 * Responsabilidad: Gestiona el estado conversacional del demostrador AlgaeChef y coordina la
 * generación de recomendaciones.
 */

package com.miguel.portomuinos.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miguel.portomuinos.ui.mock.generateMockAlgaeChefResult
import com.miguel.portomuinos.ui.model.AlgaeChefMessageRole
import com.miguel.portomuinos.ui.model.AlgaeChefMessageUi
import com.miguel.portomuinos.ui.model.RecipeUi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AlgaeChefUiState(
    val messages:
        List<AlgaeChefMessageUi> =
        emptyList(),
    val isThinking: Boolean = false
)

/**
 * Gestiona el estado conversacional del demostrador AlgaeChef.
 *
 * La fuente de respuesta actual sigue siendo simulada de forma
 * deliberada. Sustituirla por un proveedor de IA no requiere
 * modificar las pantallas Compose.
 */
class AlgaeChefViewModel : ViewModel() {

    // Estado observable de la conversación.

    private val _uiState =
        MutableStateFlow(
            AlgaeChefUiState()
        )

    val uiState =
        _uiState.asStateFlow()

    private var nextMessageId =
        1L

    private var sessionId =
        1L

    // Acciones de conversación.

    fun resetConversation() {

        sessionId++

        nextMessageId =
            1L

        _uiState.value =
            AlgaeChefUiState()
    }

    fun sendMessage(
        prompt: String,
        recipes: List<RecipeUi>
    ) {

        val cleanPrompt =
            prompt.trim()

        if (
            cleanPrompt.isEmpty() ||
            _uiState.value.isThinking
        ) {
            return
        }

        val sessionAtSend =
            sessionId

        val userMessage =
            AlgaeChefMessageUi(
                id = nextMessageId++,
                role =
                    AlgaeChefMessageRole.USER,
                text =
                    cleanPrompt
            )

        _uiState.update {
            it.copy(
                messages =
                    it.messages +
                        userMessage,
                isThinking = true
            )
        }

        viewModelScope.launch {

            delay(950)

            if (
                sessionAtSend !=
                sessionId
            ) {
                return@launch
            }

            val result =
                generateMockAlgaeChefResult(
                    prompt =
                        cleanPrompt,
                    recipes =
                        recipes
                )

            val assistantMessage =
                AlgaeChefMessageUi(
                    id = nextMessageId++,
                    role =
                        AlgaeChefMessageRole.ASSISTANT,
                    text =
                        result.reason,
                    result =
                        result
                )

            _uiState.update {
                it.copy(
                    messages =
                        it.messages +
                            assistantMessage,
                    isThinking = false
                )
            }
        }
    }
}
