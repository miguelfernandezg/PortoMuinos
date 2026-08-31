/*
 * Responsabilidad: Pruebas unitarias del mapeo de errores técnicos de autenticación a mensajes de
 * usuario.
 */

package com.miguel.portomuinos.ui.auth

import org.junit.Assert.assertEquals
import org.junit.Test

class AuthErrorMapperTest {

    // Casos representativos devueltos por Supabase Auth.

    @Test
    fun `credenciales incorrectas muestran un mensaje comprensible`() {

        val error =
            Throwable(
                "Invalid login credentials"
            )

        val result =
            error.toAuthUserMessage(
                fallbackMessage =
                    "Error desconocido"
            )

        assertEquals(
            "Correo electrónico o contraseña incorrectos.",
            result
        )
    }

    @Test
    fun `correo no confirmado muestra el mensaje correspondiente`() {

        val error =
            Throwable(
                "email_not_confirmed"
            )

        val result =
            error.toAuthUserMessage(
                fallbackMessage =
                    "Error desconocido"
            )

        assertEquals(
            "Debes confirmar tu correo electrónico antes de iniciar sesión.",
            result
        )
    }

    @Test
    fun `el límite de envío de correos se traduce correctamente`() {

        val error =
            Throwable(
                "over_email_send_rate_limit"
            )

        val result =
            error.toAuthUserMessage(
                fallbackMessage =
                    "Error desconocido"
            )

        assertEquals(
            "Has realizado demasiados intentos. Espera unos minutos antes de volver a intentarlo.",
            result
        )
    }

    @Test
    fun `un error de red muestra un mensaje de conexión`() {

        val error =
            Throwable(
                "Unable to resolve host"
            )

        val result =
            error.toAuthUserMessage(
                fallbackMessage =
                    "Error desconocido"
            )

        assertEquals(
            "No se pudo conectar con el servidor. Comprueba tu conexión a Internet.",
            result
        )
    }

    @Test
    fun `un error desconocido utiliza el mensaje alternativo`() {

        val error =
            Throwable(
                "Something completely unexpected"
            )

        val result =
            error.toAuthUserMessage(
                fallbackMessage =
                    "No se pudo completar la operación."
            )

        assertEquals(
            "No se pudo completar la operación.",
            result
        )
    }
}