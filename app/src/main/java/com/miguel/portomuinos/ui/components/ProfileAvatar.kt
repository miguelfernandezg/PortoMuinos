/*
 * Responsabilidad: Muestra la imagen de perfil seleccionada por el usuario o un icono de respaldo.
 */

package com.miguel.portomuinos.ui.components

import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import com.miguel.portomuinos.R
import com.miguel.portomuinos.ui.theme.PortoDarkGreen
import com.miguel.portomuinos.ui.theme.PortoSurface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun ProfileAvatar(
    imageUri: String?,
    size: Dp,
    placeholderIconSize: Dp,
    modifier: Modifier = Modifier,
    backgroundColor: Color = PortoSurface
) {
    val context =
        LocalContext.current.applicationContext

    val bitmap by produceState<ImageBitmap?>(
        initialValue = null,
        key1 = imageUri
    ) {
        value =
            if (imageUri.isNullOrBlank()) {
                null
            } else {
                withContext(Dispatchers.IO) {
                    decodeProfileBitmap(
                        context = context,
                        uriString = imageUri
                    )
                }
            }
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap!!,
                contentDescription =
                    "Foto de perfil",
                contentScale =
                    ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
            )
        } else {
            Icon(
                painter = painterResource(
                    R.drawable.ic_profile_outline
                ),
                contentDescription = "Perfil",
                tint = PortoDarkGreen,
                modifier = Modifier.size(
                    placeholderIconSize
                )
            )
        }
    }
}

@Suppress("DEPRECATION")
// Utilidades internas del componente.

private fun decodeProfileBitmap(
    context: android.content.Context,
    uriString: String
): ImageBitmap? {
    return try {
        val uri = Uri.parse(uriString)

        val bitmap =
            if (
                Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.P
            ) {
                val source =
                    ImageDecoder.createSource(
                        context.contentResolver,
                        uri
                    )

                ImageDecoder.decodeBitmap(
                    source
                ) { decoder, _, _ ->
                    decoder.allocator =
                        ImageDecoder.ALLOCATOR_SOFTWARE
                }
            } else {
                MediaStore.Images.Media.getBitmap(
                    context.contentResolver,
                    uri
                )
            }

        bitmap.asImageBitmap()
    } catch (_: Exception) {
        null
    }
}