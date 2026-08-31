/*
 * Responsabilidad: Coordina los eventos recibidos por deep link y los expone como estado
 * observable para el flujo de autenticación.
 */

package com.miguel.portomuinos.data.auth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object AuthDeepLinkManager {

    private val _passwordRecoveryPending =
        MutableStateFlow(false)

    val passwordRecoveryPending =
        _passwordRecoveryPending.asStateFlow()

    private val _authLoginPending =
        MutableStateFlow(false)

    val authLoginPending =
        _authLoginPending.asStateFlow()

    fun markPasswordRecoveryPending() {
        _passwordRecoveryPending.value = true
    }

    fun consumePasswordRecovery() {
        _passwordRecoveryPending.value = false
    }

    fun markAuthLoginPending() {
        _authLoginPending.value = true
    }

    fun consumeAuthLogin() {
        _authLoginPending.value = false
    }
}