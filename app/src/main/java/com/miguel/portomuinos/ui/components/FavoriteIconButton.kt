/*
 * Responsabilidad: Botón reutilizable para representar y modificar el estado de favorito.
 */

package com.miguel.portomuinos.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.miguel.portomuinos.ui.theme.PortoDarkGreen
import com.miguel.portomuinos.ui.theme.PortoSurface

/**
 * Acción de favorito compartida por recetas y productos.
 *
 * Replica el lenguaje visual que ya funcionaba bien en
 * ProductDetail: círculo neutro PortoSurface + icono verde oscuro.
 */
@Composable
fun FavoriteIconButton(
    isFavorite: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 42.dp,
    iconSize: Dp = 20.dp,
    backgroundColor: Color = PortoSurface
) {
    Box(
        modifier = modifier
            .size(size)
            .background(
                color = backgroundColor,
                shape = CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector =
                if (isFavorite) {
                    Icons.Outlined.Favorite
                } else {
                    Icons.Outlined.FavoriteBorder
                },
            contentDescription =
                if (isFavorite) {
                    "Quitar de favoritos"
                } else {
                    "Añadir a favoritos"
                },
            tint = PortoDarkGreen,
            modifier = Modifier.size(iconSize)
        )
    }
}
