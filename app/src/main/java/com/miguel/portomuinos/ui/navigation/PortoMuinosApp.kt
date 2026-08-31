/*
 * Responsabilidad: Punto raíz de Compose. Coordina ViewModels, navegación, mensajes globales y
 * estado visual de nivel aplicación.
 */

package com.miguel.portomuinos.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.miguel.portomuinos.data.auth.AuthDeepLinkManager
import com.miguel.portomuinos.data.local.AppPreferences
import com.miguel.portomuinos.ui.components.PortoBottomBarOverlayClearance
import com.miguel.portomuinos.ui.components.PortoTransientMessageHost
import com.miguel.portomuinos.ui.screens.SplashVisual
import com.miguel.portomuinos.ui.viewmodel.AlgaeChefViewModel
import com.miguel.portomuinos.ui.viewmodel.AuthEvent
import com.miguel.portomuinos.ui.viewmodel.AuthViewModel
import com.miguel.portomuinos.ui.viewmodel.CartViewModel
import com.miguel.portomuinos.ui.viewmodel.CatalogViewModel
import com.miguel.portomuinos.ui.viewmodel.FavoritesViewModel
import com.miguel.portomuinos.ui.viewmodel.UiMessage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Punto de composición raíz de la aplicación.
 *
 * Su responsabilidad se limita a coordinar navegación, ViewModels,
 * mensajes globales y estado estrictamente visual de nivel aplicación.
 * La lógica de datos y de cada feature vive en sus ViewModels/Repositories.
 */
@Composable
fun PortoMuinosApp(
    authViewModel: AuthViewModel =
        viewModel(),
    catalogViewModel: CatalogViewModel =
        viewModel(),
    favoritesViewModel: FavoritesViewModel =
        viewModel(),
    cartViewModel: CartViewModel =
        viewModel(),
    algaeChefViewModel: AlgaeChefViewModel =
        viewModel()
) {

    // Dependencias de Compose y estado visual de nivel aplicación.

    val navController =
        rememberNavController()

    val context =
        LocalContext.current

    val uriHandler =
        LocalUriHandler.current

    val appPreferences =
        remember(context) {
            AppPreferences(
                context
            )
        }

    val backStackEntry by
        navController
            .currentBackStackEntryAsState()

    val currentRoute =
        backStackEntry
            ?.destination
            ?.route

    val showBottomBar =
        currentRoute in
            bottomBarRoutes

    val passwordRecoveryPending by
        AuthDeepLinkManager
            .passwordRecoveryPending
            .collectAsStateWithLifecycle()

    val authLoginPending by
        AuthDeepLinkManager
            .authLoginPending
            .collectAsStateWithLifecycle()

    var profileImageUri by
        rememberSaveable {
            mutableStateOf(
                appPreferences
                    .getProfileImageUri()
            )
        }

    var homeSearchQuery by
        rememberSaveable {
            mutableStateOf("")
        }

    /*
     * Se utiliza únicamente al restaurar una sesión desde Splash.
     * Mantiene la Splash sobre Home hasta que la primera composición
     * principal está preparada y evita un frame visual incompleto.
     */
    var startupCoverVisible by
        remember {
            mutableStateOf(false)
        }

    val snackbarHostState =
        remember {
            SnackbarHostState()
        }

    val coroutineScope =
        rememberCoroutineScope()

    /*
     * Los avisos de interacción rápidos no se encolan como Snackbar.
     * Cada nuevo mensaje sustituye inmediatamente al anterior.
     */
    var transientMessage by
        remember {
            mutableStateOf<String?>(
                null
            )
        }

    var displayedTransientMessage by
        remember {
            mutableStateOf("")
        }

    var transientMessageVersion by
        remember {
            mutableLongStateOf(0L)
        }

    // Mensajería global y sincronización del estado asociado al usuario.

    fun showTransientMessage(
        message: String
    ) {

        displayedTransientMessage =
            message

        transientMessage =
            message

        transientMessageVersion++
    }

    suspend fun showMessage(
        message: UiMessage
    ) {

        if (message.transient) {
            showTransientMessage(
                message.text
            )
        } else {
            snackbarHostState
                .showSnackbar(
                    message.text
                )
        }
    }

    suspend fun refreshUserScopedData() {

        favoritesViewModel
            .loadFavorites()

        cartViewModel
            .loadCart()
    }

    fun clearUserScopedUiState() {

        appPreferences
            .clearProfileImage()

        profileImageUri =
            null

        homeSearchQuery =
            ""

        favoritesViewModel
            .clearLocalState()

        cartViewModel
            .clearLocalState()

        algaeChefViewModel
            .resetConversation()
    }

    fun updateProfileImage(
        uri: String
    ) {

        profileImageUri =
            uri

        appPreferences
            .setProfileImageUri(
                uri
            )
    }

    fun navigateToLoginAfterSessionEnd() {

        navController.navigate(
            AppRoutes.LOGIN
        ) {
            popUpTo(
                AppRoutes.HOME
            ) {
                inclusive = true
            }

            launchSingleTop = true
        }
    }

    fun navigateHomeAfterLogin() {

        val hasOnboarding =
            runCatching {
                navController
                    .getBackStackEntry(
                        AppRoutes.ONBOARDING
                    )
            }
                .isSuccess

        navController.navigate(
            AppRoutes.HOME
        ) {
            popUpTo(
                if (hasOnboarding) {
                    AppRoutes.ONBOARDING
                } else {
                    AppRoutes.LOGIN
                }
            ) {
                inclusive = true
            }

            launchSingleTop = true
        }
    }

    /*
     * Oculta automáticamente el aviso transitorio.
     * La versión reinicia el temporizador cuando cambia el mensaje.
     */
    // Efectos de ciclo de vida, autenticación y eventos de los ViewModels.

    LaunchedEffect(
        transientMessageVersion
    ) {

        if (
            transientMessage == null
        ) {
            return@LaunchedEffect
        }

        delay(1400)

        transientMessage =
            null
    }

    /*
     * Eventos de autenticación que requieren coordinación entre
     * varios ViewModels o navegación.
     */
    LaunchedEffect(
        authViewModel
    ) {

        authViewModel
            .events
            .collect { event ->

                when (event) {

                    AuthEvent.LoginSucceeded -> {

                        refreshUserScopedData()

                        navigateHomeAfterLogin()
                    }

                    AuthEvent
                        .RegistrationSucceeded -> {

                        refreshUserScopedData()

                        navController.navigate(
                            AppRoutes.HOME
                        ) {
                            popUpTo(
                                AppRoutes.REGISTER
                            ) {
                                inclusive = true
                            }

                            launchSingleTop = true
                        }
                    }

                    AuthEvent
                        .RegistrationRequiresConfirmation -> {

                        val previousRoute =
                            navController
                                .previousBackStackEntry
                                ?.destination
                                ?.route

                        if (
                            previousRoute ==
                            AppRoutes.LOGIN
                        ) {
                            navController
                                .popBackStack()
                        } else {
                            navController.navigate(
                                AppRoutes.LOGIN
                            ) {
                                popUpTo(
                                    AppRoutes.REGISTER
                                ) {
                                    inclusive = true
                                }

                                launchSingleTop = true
                            }
                        }
                    }

                    AuthEvent.PasswordUpdated -> {

                        clearUserScopedUiState()

                        navController.navigate(
                            AppRoutes.LOGIN
                        ) {
                            popUpTo(
                                AppRoutes.RESET_PASSWORD
                            ) {
                                inclusive = true
                            }

                            launchSingleTop = true
                        }
                    }

                    AuthEvent.LoggedOut,
                    AuthEvent.AccountDeleted -> {

                        clearUserScopedUiState()

                        navigateToLoginAfterSessionEnd()
                    }
                }
            }
    }

    LaunchedEffect(
        authViewModel
    ) {
        authViewModel
            .messages
            .collect {
                showMessage(it)
            }
    }

    LaunchedEffect(
        catalogViewModel
    ) {
        catalogViewModel
            .messages
            .collect {
                showMessage(it)
            }
    }

    LaunchedEffect(
        favoritesViewModel
    ) {
        favoritesViewModel
            .messages
            .collect {
                showMessage(it)
            }
    }

    LaunchedEffect(
        cartViewModel
    ) {
        cartViewModel
            .messages
            .collect {
                showMessage(it)
            }
    }

    /*
     * Deep link de recuperación de contraseña recibido con la app
     * ya abierta.
     */
    LaunchedEffect(
        passwordRecoveryPending,
        currentRoute
    ) {

        if (
            passwordRecoveryPending &&
            currentRoute != null &&
            currentRoute !=
            AppRoutes.SPLASH &&
            currentRoute !=
            AppRoutes.RESET_PASSWORD
        ) {

            startupCoverVisible =
                false

            navController.navigate(
                AppRoutes.RESET_PASSWORD
            ) {
                launchSingleTop = true
            }

            AuthDeepLinkManager
                .consumePasswordRecovery()
        }
    }

    /*
     * Finalización del flujo OAuth de Google tras volver desde
     * el navegador mediante portomuinos://auth.
     */
    LaunchedEffect(
        authLoginPending,
        currentRoute
    ) {

        if (
            authLoginPending &&
            currentRoute != null &&
            currentRoute !=
            AppRoutes.SPLASH
        ) {

            val sessionActive =
                authViewModel
                    .completeExternalLogin()

            if (sessionActive) {

                refreshUserScopedData()

                startupCoverVisible =
                    false

                if (
                    currentRoute !=
                    AppRoutes.HOME
                ) {
                    navigateHomeAfterLogin()
                }
            }

            AuthDeepLinkManager
                .consumeAuthLogin()
        }
    }

    Box(
        modifier =
            Modifier.fillMaxSize()
    ) {

        // Estructura visual raíz y conexión con el grafo de navegación.

    Scaffold(
            modifier =
                Modifier.fillMaxSize(),
            containerColor =
                Color.White,
            snackbarHost = {
                SnackbarHost(
                    hostState =
                        snackbarHostState,
                    modifier =
                        Modifier.padding(
                            bottom =
                                if (
                                    showBottomBar
                                ) {
                                    PortoBottomBarOverlayClearance
                                } else {
                                    0.dp
                                }
                        )
                )
            }
        ) { innerPadding ->

            val mainContentPadding =
                PaddingValues(
                    top =
                        innerPadding
                            .calculateTopPadding()
                )

            PortoNavHost(
                navController =
                    navController,
                mainContentPadding =
                    mainContentPadding,
                authViewModel =
                    authViewModel,
                catalogViewModel =
                    catalogViewModel,
                favoritesViewModel =
                    favoritesViewModel,
                cartViewModel =
                    cartViewModel,
                algaeChefViewModel =
                    algaeChefViewModel,
                profileImageUri =
                    profileImageUri,
                homeSearchQuery =
                    homeSearchQuery,
                startupCoverVisible =
                    startupCoverVisible,
                onHomeSearchQueryChange = {
                    homeSearchQuery =
                        it
                },
                onStartupCoverHidden = {
                    startupCoverVisible =
                        false
                },
                onSplashFinished = {

                    coroutineScope.launch {

                        val sessionActive =
                            authViewModel
                                .initializeSession()

                        if (sessionActive) {
                            refreshUserScopedData()
                        } else {
                            authViewModel
                                .clearSessionPresentationState()

                            clearUserScopedUiState()
                        }

                        val destination =
                            when {

                                passwordRecoveryPending ->
                                    AppRoutes
                                        .RESET_PASSWORD

                                sessionActive ->
                                    AppRoutes.HOME

                                appPreferences
                                    .isOnboardingCompleted() ->
                                    AppRoutes.LOGIN

                                else ->
                                    AppRoutes.ONBOARDING
                            }

                        if (
                            destination ==
                            AppRoutes.HOME
                        ) {
                            startupCoverVisible =
                                true
                        }

                        navController.navigate(
                            destination
                        ) {
                            popUpTo(
                                AppRoutes.SPLASH
                            ) {
                                inclusive = true
                            }

                            launchSingleTop = true
                        }

                        if (
                            destination ==
                            AppRoutes.RESET_PASSWORD
                        ) {
                            AuthDeepLinkManager
                                .consumePasswordRecovery()
                        }
                    }
                },
                onOnboardingCompleted = {
                    appPreferences
                        .markOnboardingCompleted()
                },
                onProfileImageSelected = {
                    updateProfileImage(
                        it
                    )
                },
                onShowTransientMessage = {
                    showTransientMessage(
                        it
                    )
                },
                onOpenUri = { url ->

                    runCatching {
                        uriHandler
                            .openUri(
                                url
                            )
                    }
                        .onFailure {
                            coroutineScope.launch {
                                snackbarHostState
                                    .showSnackbar(
                                        "No se pudo abrir la tienda oficial."
                                    )
                            }
                        }
                }
            )
        }

        PortoTransientMessageHost(
            visible =
                transientMessage != null,
            message =
                displayedTransientMessage,
            showBottomBar =
                showBottomBar,
            modifier =
                Modifier.align(
                    Alignment.BottomCenter
                )
        )

        if (startupCoverVisible) {
            SplashVisual(
                modifier =
                    Modifier.fillMaxSize()
            )
        }
    }
}
