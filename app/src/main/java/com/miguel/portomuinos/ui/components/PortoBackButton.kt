/*
 * Responsabilidad: Botón de retroceso reutilizable con variantes visuales para fondos normales y
 * superpuestos.
 */

package com.miguel.portomuinos.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.miguel.portomuinos.ui.theme.PortoDarkGreen

enum class PortoBackButtonStyle {
    PLAIN,
    OVERLAY
}

/**
 * Botón de volver compartido por la aplicación.
 *
 * PLAIN:
 * - cabeceras y fondos limpios
 * - zona táctil 44 dp sin círculo visible
 *
 * OVERLAY:
 * - fotografías y cámara
 * - círculo blanco y sombra suave para conservar contraste
 */
@Composable
fun PortoBackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: PortoBackButtonStyle = PortoBackButtonStyle.PLAIN,
    size: Dp = 44.dp,
    iconSize: Dp = 23.dp
) {
    val visualModifier =
        if (style == PortoBackButtonStyle.OVERLAY) {
            Modifier
                .shadow(
                    elevation = 2.dp,
                    shape = CircleShape
                )
                .background(
                    color = Color.White.copy(alpha = 0.96f),
                    shape = CircleShape
                )
        } else {
            Modifier
        }

    Box(
        modifier = modifier
            .size(size)
            .then(visualModifier)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.ArrowBack,
            contentDescription = "Volver",
            tint = PortoDarkGreen,
            modifier = Modifier.size(iconSize)
        )
    }
}
