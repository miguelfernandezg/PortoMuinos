/*
 * Responsabilidad: Convierte errores técnicos de autenticación en mensajes comprensibles y seguros
 * para mostrar al usuario.
 */

package com.miguel.portomuinos.ui.auth

fun Throwable.toAuthUserMessage(
    fallbackMessage: String
): String {

    val message =
        this.message
            ?.lowercase()
            .orEmpty()

    return when {

        "invalid login credentials" in message ||
                "invalid_credentials" in message ->
            "Correo electrónico o contraseña incorrectos."

        "email not confirmed" in message ||
                "email_not_confirmed" in message ->
            "Debes confirmar tu correo electrónico antes de iniciar sesión."

        "user already registered" in message ||
                "user_already_exists" in message ->
            "Ya existe una cuenta asociada a este correo electrónico."

        "weak_password" in message ||
                "password should be at least" in message ->
            "La contraseña no cumple los requisitos de seguridad."

        "signup is disabled" in message ||
                "signup_disabled" in message ->
            "El registro de nuevas cuentas no está disponible en este momento."

        "email rate limit exceeded" in message ||
                "over_email_send_rate_limit" in message ->
            "Has realizado demasiados intentos. Espera unos minutos antes de volver a intentarlo."

        "rate limit" in message ||
                "too many requests" in message ||
                "over_request_rate_limit" in message ->
            "Demasiados intentos. Espera un momento y vuelve a intentarlo."

        "unable to resolve host" in message ||
                "failed to connect" in message ||
                "network" in message ||
                "connectexception" in message ||
                "sockettimeoutexception" in message ->
            "No se pudo conectar con el servidor. Comprueba tu conexión a Internet."

        else ->
            fallbackMessage
    }
}