/*
 * Responsabilidad: Actividad principal de Android. Inicializa la interfaz Compose y delega el
 * tratamiento de deep links de autenticación.
 */

package com.miguel.portomuinos

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.miguel.portomuinos.data.auth.AuthDeepLinkManager
import com.miguel.portomuinos.data.remote.SupabaseClientProvider
import com.miguel.portomuinos.ui.navigation.PortoMuinosApp
import com.miguel.portomuinos.ui.theme.PortoMuinosTheme
import io.github.jan.supabase.gotrue.handleDeeplinks

class MainActivity : ComponentActivity() {

    // Creación de la actividad y montaje de la interfaz Compose.

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        handleAuthDeepLink(intent)

        enableEdgeToEdge()

        setContent {
            PortoMuinosTheme {
                PortoMuinosApp()
            }
        }
    }

    // Recepción de deep links cuando la actividad ya está abierta.

    override fun onNewIntent(
        intent: Intent
    ) {
        super.onNewIntent(intent)

        setIntent(intent)

        handleAuthDeepLink(intent)
    }

    // Clasifica el callback de Supabase como recuperación o login externo.

    private fun handleAuthDeepLink(
        intent: Intent
    ) {

        SupabaseClientProvider
            .client
            .handleDeeplinks(
                intent
            ) { session ->

                if (
                    session.type.equals(
                        "recovery",
                        ignoreCase = true
                    )
                ) {

                    AuthDeepLinkManager
                        .markPasswordRecoveryPending()

                } else {

                    AuthDeepLinkManager
                        .markAuthLoginPending()
                }
            }
    }
}