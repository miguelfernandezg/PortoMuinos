/*
 * Responsabilidad: Carga imágenes del catálogo desde recurso local o URL remota con un fallback
 * controlado.
 */

package com.miguel.portomuinos.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.miguel.portomuinos.ui.theme.PortoSurface

@Composable
fun CatalogImage(
    @DrawableRes imageRes: Int?,
    imageUrl: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {

    when {

        !imageUrl.isNullOrBlank() -> {

            AsyncImage(
                model = ImageRequest.Builder(
                    LocalContext.current
                )
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = contentDescription,
                modifier = modifier,
                contentScale = contentScale,
                placeholder = ColorPainter(
                    PortoSurface
                ),
                error = ColorPainter(
                    PortoSurface
                ),
                fallback = ColorPainter(
                    PortoSurface
                )
            )
        }

        imageRes != null -> {

            Image(
                painter = painterResource(
                    id = imageRes
                ),
                contentDescription =
                    contentDescription,
                modifier = modifier,
                contentScale = contentScale
            )
        }

        else -> {

            Image(
                painter = ColorPainter(
                    PortoSurface
                ),
                contentDescription =
                    contentDescription,
                modifier = modifier,
                contentScale = contentScale
            )
        }
    }
}