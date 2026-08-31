/*
 * Responsabilidad: Encapsula las preferencias locales simples de la aplicación, como onboarding y
 * foto de perfil.
 */

package com.miguel.portomuinos.data.local

import android.content.Context

/**
 * Centraliza las preferencias locales sencillas de la aplicación.
 *
 * La sesión de usuario no se guarda aquí: Supabase Auth mantiene
 * su propia sesión persistente.
 */
class AppPreferences(
    context: Context
) {

    private val preferences =
        context.applicationContext
            .getSharedPreferences(
                FILE_NAME,
                Context.MODE_PRIVATE
            )

    // Estado del onboarding.

    fun isOnboardingCompleted(): Boolean {
        return preferences.getBoolean(
            KEY_ONBOARDING_COMPLETED,
            false
        )
    }

    fun markOnboardingCompleted() {
        preferences.edit()
            .putBoolean(
                KEY_ONBOARDING_COMPLETED,
                true
            )
            .apply()
    }

    // Imagen de perfil seleccionada localmente.

    fun getProfileImageUri(): String? {
        return preferences.getString(
            KEY_PROFILE_IMAGE_URI,
            null
        )
    }

    fun setProfileImageUri(
        uri: String
    ) {
        preferences.edit()
            .putString(
                KEY_PROFILE_IMAGE_URI,
                uri
            )
            .apply()
    }

    fun clearProfileImage() {
        preferences.edit()
            .remove(
                KEY_PROFILE_IMAGE_URI
            )
            .apply()
    }

    private companion object {
        const val FILE_NAME =
            "portomuinos_preferences"

        const val KEY_ONBOARDING_COMPLETED =
            "onboarding_completed"

        const val KEY_PROFILE_IMAGE_URI =
            "profile_image_uri"
    }
}
