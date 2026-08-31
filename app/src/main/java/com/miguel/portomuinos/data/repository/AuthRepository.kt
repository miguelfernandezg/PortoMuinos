/*
 * Responsabilidad: Abstrae las operaciones de Supabase Auth y mantiene la capa de presentación
 * desacoplada del SDK remoto.
 */

package com.miguel.portomuinos.data.repository

import com.miguel.portomuinos.data.remote.SupabaseClientProvider
import io.github.jan.supabase.functions.functions
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.Google
import io.github.jan.supabase.gotrue.providers.builtin.Email
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

data class RegistrationResult(
    val requiresEmailConfirmation: Boolean
)

object AuthRepository {

    private val auth
        get() =
            SupabaseClientProvider
                .client
                .auth

    // Registro e inicio de sesión.

    suspend fun register(
        fullName: String,
        email: String,
        password: String
    ): RegistrationResult {

        auth.signUpWith(Email) {

            this.email =
                email.trim()

            this.password =
                password

            data =
                buildJsonObject {
                    put(
                        "full_name",
                        fullName.trim()
                    )
                }
        }

        return RegistrationResult(
            requiresEmailConfirmation =
                auth.currentSessionOrNull() == null
        )
    }

    suspend fun login(
        email: String,
        password: String
    ) {
        auth.signInWith(Email) {

            this.email =
                email.trim()

            this.password =
                password
        }
    }

    // Gestión de la sesión autenticada.

    suspend fun logout() {
        auth.signOut()
    }

    suspend fun awaitSessionInitialization() {
        auth.awaitInitialization()
    }

    fun hasActiveSession(): Boolean {
        return auth
            .currentSessionOrNull() != null
    }

    fun getCurrentUserId(): String? {
        return auth
            .currentUserOrNull()
            ?.id
    }

    suspend fun deleteAccount() {

        if (
            auth.currentSessionOrNull() == null
        ) {
            error(
                "No hay una sesión activa."
            )
        }

        SupabaseClientProvider
            .client
            .functions
            .invoke(
                "delete-account"
            )

        auth.clearSession()
    }

    // Datos básicos del usuario actual.

    fun getCurrentUserFullName(): String {

        val user =
            auth.currentUserOrNull()
                ?: return "Usuario"

        val fullName =
            user.userMetadata
                ?.get("full_name")
                ?.jsonPrimitive
                ?.contentOrNull
                ?.trim()

        if (!fullName.isNullOrBlank()) {
            return fullName
        }

        return user.email
            ?.substringBefore("@")
            ?.replace(".", " ")
            ?.replace("_", " ")
            ?.trim()
            ?.takeIf {
                it.isNotBlank()
            }
            ?: "Usuario"
    }

    fun getCurrentUserDisplayName(): String {

        return getCurrentUserFullName()
            .substringBefore(" ")
            .ifBlank {
                "Usuario"
            }
    }

    // Recuperación y actualización de credenciales.

    suspend fun sendPasswordRecoveryEmail(
        email: String
    ) {
        auth.resetPasswordForEmail(
            email = email.trim()
        )
    }

    suspend fun updatePassword(
        newPassword: String
    ) {
        auth.updateUser {
            password = newPassword
        }
    }

    suspend fun loginWithGoogle() {

        auth.signInWith(
            Google,
            redirectUrl = "portomuinos://auth"
        )
    }
}