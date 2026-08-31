/*
 * Responsabilidad: Tarjeta compacta utilizada para destacar recetas en la pantalla de inicio.
 */

package com.miguel.portomuinos.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.miguel.portomuinos.ui.theme.PortoSurface

private val FeaturedRecipeShape =
    RoundedCornerShape(15.dp)

@Composable
fun FeaturedRecipeCard(
    imageRes: Int?,
    modifier: Modifier = Modifier,
    imageUrl: String? = null,
    contentDescription: String? = null,
    isFavorite: Boolean = false,
    onClick: () -> Unit = {},
    onFavoriteClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(345f / 227f)
            .shadow(
                elevation = 3.dp,
                shape = FeaturedRecipeShape
            )
            .clip(FeaturedRecipeShape)
            .background(PortoSurface)
            .clickable(onClick = onClick)
    ) {
        CatalogImage(
            imageRes = imageRes,
            imageUrl = imageUrl,
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Mantiene el mismo lenguaje visual de favorito usado en el catálogo.
        FavoriteIconButton(
            isFavorite = isFavorite,
            onClick = onFavoriteClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(
                    top = 10.dp,
                    end = 10.dp
                ),
            size = 42.dp,
            iconSize = 20.dp,
            backgroundColor =
                PortoSurface.copy(
                    alpha = 0.96f
                )
        )
    }
}
