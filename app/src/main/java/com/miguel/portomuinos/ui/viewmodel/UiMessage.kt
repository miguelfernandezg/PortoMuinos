/*
 * Responsabilidad: Modelo común para comunicar mensajes puntuales desde los ViewModels hacia la
 * interfaz.
 */

package com.miguel.portomuinos.ui.viewmodel

/**
 * Mensaje de presentación emitido por un ViewModel.
 *
 * Los mensajes transitorios se muestran mediante el aviso visual
 * propio de la app. El resto se muestran como Snackbar.
 */
data class UiMessage(
    val text: String,
    val transient: Boolean = false
)
