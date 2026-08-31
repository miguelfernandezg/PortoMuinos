/*
 * Responsabilidad: Gestiona estado y acciones de autenticación, manteniendo la navegación fuera de
 * la lógica de presentación.
 */

package com.miguel.portomuinos.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miguel.portomuinos.data.repository.AuthRepository
import com.miguel.portomuinos.ui.auth.toAuthUserMessage
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val currentUserName: String = "Usuario",
    val currentUserFullName: String = "Usuario",
    val loginAuthError: String? = null,
    val loginInfoText: String? = null,
    val loginIsLoading: Boolean = false,
    val registerAuthError: String? = null,
    val registerIsLoading: Boolean = false,
    val forgotPasswordIsLoading: Boolean = false,
    val forgotPasswordAuthError: String? = null,
    val forgotPasswordInfoText: String? = null,
    val resetPasswordIsLoading: Boolean = false,
    val resetPasswordAuthError: String? = null
)

sealed interface AuthEvent {
    data object LoginSucceeded : AuthEvent
    data object RegistrationSucceeded : AuthEvent
    data object RegistrationRequiresConfirmation : AuthEvent
    data object PasswordUpdated : AuthEvent
    data object LoggedOut : AuthEvent
    data object AccountDeleted : AuthEvent
}

/**
 * Gestiona la lógica de presentación relacionada con autenticación y sesión.
 *
 * La navegación se mantiene fuera del ViewModel: los eventos de éxito
 * se consumen desde la capa UI y el NavController decide el destino.
 */
class AuthViewModel : ViewModel() {

    // Estado observable por las pantallas de autenticación.

    private val _uiState =
        MutableStateFlow(
            AuthUiState()
        )

    val uiState =
        _uiState.asStateFlow()

    private val _events =
        MutableSharedFlow<AuthEvent>(
            extraBufferCapacity = 4
        )

    val events =
        _events.asSharedFlow()

    private val _messages =
        MutableSharedFlow<UiMessage>(
            extraBufferCapacity = 4
        )

    val messages =
        _messages.asSharedFlow()

    // Inicialización y restauración de sesión.

    suspend fun initializeSession(): Boolean {

        val sessionActive =
            runCatching {
                AuthRepository
                    .awaitSessionInitialization()

                AuthRepository
                    .hasActiveSession()
            }
                .getOrDefault(false)

        if (sessionActive) {
            refreshCurrentUserProfile()
        }

        return sessionActive
    }

    suspend fun completeExternalLogin(): Boolean {

        val sessionActive =
            initializeSession()

        if (!sessionActive) {
            _uiState.update {
                it.copy(
                    loginAuthError =
                        "No hemos podido completar el inicio de sesión con Google."
                )
            }
        }

        return sessionActive
    }

    // Acciones de autenticación iniciadas por el usuario.

    fun login(
        email: String,
        password: String
    ) {

        viewModelScope.launch {

            _uiState.update {
                it.copy(
                    loginIsLoading = true,
                    loginAuthError = null,
                    loginInfoText = null
                )
            }

            val result =
                runCatching {
                    AuthRepository.login(
                        email = email,
                        password = password
                    )
                }

            result.onSuccess {

                refreshCurrentUserProfile()

                _uiState.update {
                    it.copy(
                        loginIsLoading = false
                    )
                }

                _events.emit(
                    AuthEvent.LoginSucceeded
                )
            }

            result.onFailure { error ->

                _uiState.update {
                    it.copy(
                        loginIsLoading = false,
                        loginAuthError =
                            error.toAuthUserMessage(
                                fallbackMessage =
                                    "No hemos podido iniciar sesión. Inténtalo de nuevo."
                            )
                    )
                }
            }
        }
    }

    fun register(
        fullName: String,
        email: String,
        password: String
    ) {

        viewModelScope.launch {

            _uiState.update {
                it.copy(
                    registerIsLoading = true,
                    registerAuthError = null
                )
            }

            val result =
                runCatching {
                    AuthRepository.register(
                        fullName = fullName,
                        email = email,
                        password = password
                    )
                }

            result.onSuccess {
                    registrationResult ->

                if (
                    registrationResult
                        .requiresEmailConfirmation
                ) {

                    _uiState.update {
                        it.copy(
                            registerIsLoading = false,
                            loginAuthError = null,
                            loginInfoText =
                                "Revisa tu correo electrónico. Si esta dirección puede registrarse, recibirás un enlace para confirmar la cuenta. Si ya tienes una cuenta, puedes iniciar sesión."
                        )
                    }

                    _events.emit(
                        AuthEvent
                            .RegistrationRequiresConfirmation
                    )

                } else {

                    refreshCurrentUserProfile()

                    _uiState.update {
                        it.copy(
                            registerIsLoading = false
                        )
                    }

                    _events.emit(
                        AuthEvent
                            .RegistrationSucceeded
                    )
                }
            }

            result.onFailure { error ->

                _uiState.update {
                    it.copy(
                        registerIsLoading = false,
                        registerAuthError =
                            error.toAuthUserMessage(
                                fallbackMessage =
                                    "No hemos podido crear la cuenta. Inténtalo de nuevo."
                            )
                    )
                }
            }
        }
    }

    fun loginWithGoogle() {

        viewModelScope.launch {

            _uiState.update {
                it.copy(
                    loginAuthError = null,
                    loginInfoText = null
                )
            }

            val result =
                runCatching {
                    AuthRepository
                        .loginWithGoogle()
                }

            result.onFailure { error ->

                _uiState.update {
                    it.copy(
                        loginAuthError =
                            error.toAuthUserMessage(
                                fallbackMessage =
                                    "No hemos podido iniciar sesión con Google. Inténtalo de nuevo."
                            )
                    )
                }
            }
        }
    }

    fun sendPasswordRecoveryEmail(
        email: String
    ) {

        viewModelScope.launch {

            _uiState.update {
                it.copy(
                    forgotPasswordIsLoading = true,
                    forgotPasswordAuthError = null,
                    forgotPasswordInfoText = null
                )
            }

            val result =
                runCatching {
                    AuthRepository
                        .sendPasswordRecoveryEmail(
                            email
                        )
                }

            result.onSuccess {

                _uiState.update {
                    it.copy(
                        forgotPasswordIsLoading = false,
                        forgotPasswordInfoText =
                            "Si existe una cuenta asociada a este correo electrónico, recibirás un enlace para restablecer tu contraseña. Revisa también la carpeta de spam."
                    )
                }
            }

            result.onFailure { error ->

                _uiState.update {
                    it.copy(
                        forgotPasswordIsLoading = false,
                        forgotPasswordAuthError =
                            error.toAuthUserMessage(
                                fallbackMessage =
                                    "No hemos podido enviar el correo. Inténtalo de nuevo."
                            )
                    )
                }
            }
        }
    }

    fun updatePassword(
        newPassword: String
    ) {

        viewModelScope.launch {

            _uiState.update {
                it.copy(
                    resetPasswordIsLoading = true,
                    resetPasswordAuthError = null
                )
            }

            val result =
                runCatching {
                    AuthRepository
                        .updatePassword(
                            newPassword
                        )
                }

            result.onSuccess {

                runCatching {
                    AuthRepository.logout()
                }

                _uiState.update {
                    it.copy(
                        currentUserName = "Usuario",
                        currentUserFullName = "Usuario",
                        resetPasswordIsLoading = false,
                        loginAuthError = null,
                        loginInfoText =
                            "Contraseña actualizada correctamente. Ya puedes iniciar sesión con tu nueva contraseña."
                    )
                }

                _events.emit(
                    AuthEvent.PasswordUpdated
                )
            }

            result.onFailure { error ->

                _uiState.update {
                    it.copy(
                        resetPasswordIsLoading = false,
                        resetPasswordAuthError =
                            error.toAuthUserMessage(
                                fallbackMessage =
                                    "No hemos podido actualizar la contraseña. Solicita un nuevo enlace e inténtalo de nuevo."
                            )
                    )
                }
            }
        }
    }

    fun logout() {

        viewModelScope.launch {

            val result =
                runCatching {
                    AuthRepository.logout()
                }

            result.onSuccess {

                clearSessionPresentationState()

                _events.emit(
                    AuthEvent.LoggedOut
                )
            }

            result.onFailure {

                _messages.emit(
                    UiMessage(
                        "No se pudo cerrar la sesión. Inténtalo de nuevo."
                    )
                )
            }
        }
    }

    fun deleteAccount() {

        viewModelScope.launch {

            val result =
                runCatching {
                    AuthRepository
                        .deleteAccount()
                }

            result.onSuccess {

                clearSessionPresentationState()

                _events.emit(
                    AuthEvent.AccountDeleted
                )
            }

            result.onFailure {

                _messages.emit(
                    UiMessage(
                        "No se pudo eliminar la cuenta. Inténtalo de nuevo."
                    )
                )
            }
        }
    }

    // Limpieza de estados temporales de formularios.

    fun clearLoginMessages() {
        _uiState.update {
            it.copy(
                loginAuthError = null,
                loginInfoText = null
            )
        }
    }

    fun clearRegisterError() {
        _uiState.update {
            it.copy(
                registerAuthError = null
            )
        }
    }

    fun prepareRegistration() {
        clearRegisterError()
    }

    fun prepareForgotPassword() {
        _uiState.update {
            it.copy(
                forgotPasswordAuthError = null,
                forgotPasswordInfoText = null
            )
        }
    }

    fun clearForgotPasswordMessages() {
        prepareForgotPassword()
    }

    fun clearResetPasswordError() {
        _uiState.update {
            it.copy(
                resetPasswordAuthError = null
            )
        }
    }

    fun clearSessionPresentationState() {

        _uiState.update {
            it.copy(
                currentUserName = "Usuario",
                currentUserFullName = "Usuario",
                loginAuthError = null,
                loginInfoText = null,
                registerAuthError = null,
                forgotPasswordAuthError = null,
                forgotPasswordInfoText = null,
                resetPasswordAuthError = null,
                loginIsLoading = false,
                registerIsLoading = false,
                forgotPasswordIsLoading = false,
                resetPasswordIsLoading = false
            )
        }
    }

    // Utilidades internas.

    private fun refreshCurrentUserProfile() {

        _uiState.update {
            it.copy(
                currentUserFullName =
                    AuthRepository
                        .getCurrentUserFullName(),
                currentUserName =
                    AuthRepository
                        .getCurrentUserDisplayName()
            )
        }
    }
}
