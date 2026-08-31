/*
 * Responsabilidad: Presenta avisos breves que se sustituyen entre sí sin generar una cola de
 * snackbars.
 */

package com.miguel.portomuinos.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miguel.portomuinos.ui.theme.Poppins
import com.miguel.portomuinos.ui.theme.PortoDarkGreen

/**
 * Aviso breve propio de la aplicación.
 *
 * A diferencia de un Snackbar, sustituye el mensaje anterior en lugar
 * de encolarlo, por lo que funciona mejor para acciones rápidas como
 * añadir varios productos al carrito.
 */
@Composable
fun PortoTransientMessageHost(
    visible: Boolean,
    message: String,
    showBottomBar: Boolean,
    modifier: Modifier = Modifier
) {

    val systemNavigationBottom =
        WindowInsets.navigationBars
            .asPaddingValues()
            .calculateBottomPadding()

    AnimatedVisibility(
        visible = visible,
        enter =
            fadeIn() +
                slideInVertically {
                    it / 3
                },
        exit =
            fadeOut() +
                slideOutVertically {
                    it / 3
                },
        modifier = modifier
            .padding(
                horizontal = 20.dp
            )
            .padding(
                bottom =
                    if (showBottomBar) {
                        PortoBottomBarOverlayClearance +
                            12.dp
                    } else {
                        systemNavigationBottom +
                            24.dp
                    }
            )
    ) {

        Surface(
            modifier = Modifier
                .widthIn(
                    max = 360.dp
                ),
            shape =
                RoundedCornerShape(
                    22.dp
                ),
            color =
                PortoDarkGreen,
            contentColor =
                Color.White,
            shadowElevation =
                5.dp
        ) {

            Text(
                text = message,
                fontFamily =
                    Poppins,
                fontWeight =
                    FontWeight.Medium,
                fontSize =
                    13.sp,
                lineHeight =
                    18.sp,
                color =
                    Color.White,
                modifier =
                    Modifier.padding(
                        horizontal = 18.dp,
                        vertical = 12.dp
                    )
            )
        }
    }
}
