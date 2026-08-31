/*
 * Responsabilidad: Pantalla inicial que mantiene la identidad visual mientras se resuelve el
 * estado de sesión.
 */

package com.miguel.portomuinos.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.miguel.portomuinos.ui.components.AuthBackground
import com.miguel.portomuinos.ui.components.PortoMuinosLogo
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onFinished: () -> Unit
) {
    LaunchedEffect(Unit) {
        delay(950)
        onFinished()
    }

    SplashVisual()
}

@Composable
fun SplashVisual(
    modifier: Modifier = Modifier
) {
    AuthBackground(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            PortoMuinosLogo(
                modifier = Modifier.size(
                    width = 250.dp,
                    height = 126.dp
                )
            )
        }
    }
}
