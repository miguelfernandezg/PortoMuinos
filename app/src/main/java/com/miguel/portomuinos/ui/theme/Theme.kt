/*
 * Responsabilidad: Configura el tema Material 3 y aplica la identidad visual general de la
 * aplicación.
 */

package com.miguel.portomuinos.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val PortoMuinosColorScheme = lightColorScheme(
    primary = PortoPrimary,
    secondary = PortoMint,
    background = PortoBackground,
    surface = PortoSurface,
    onPrimary = PortoWhite,
    onSecondary = PortoDarkGreen,
    onBackground = PortoTextPrimary,
    onSurface = PortoTextPrimary
)

@Composable
fun PortoMuinosTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = PortoMuinosColorScheme,
        typography = Typography,
        content = content
    )
}