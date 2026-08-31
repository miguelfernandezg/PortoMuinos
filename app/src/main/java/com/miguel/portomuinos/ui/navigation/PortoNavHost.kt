/*
 * Responsabilidad: Define el grafo de navegación y conecta cada pantalla Compose con el estado y
 * las acciones de sus ViewModels.
 */

package com.miguel.portomuinos.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.miguel.portomuinos.ui.components.BottomNavItem
import com.miguel.portomuinos.ui.mock.algaeChefQuickSuggestions
import com.miguel.portomuinos.ui.model.FeaturedProductUi
import com.miguel.portomuinos.ui.model.FeaturedRecipeUi
import com.miguel.portomuinos.ui.model.toPriceLabel
import com.miguel.portomuinos.ui.screens.AlgaeChefScreen
import com.miguel.portomuinos.ui.screens.CartScreen
import com.miguel.portomuinos.ui.screens.FavoritesScreen
import com.miguel.portomuinos.ui.screens.ForgotPasswordScreen
import com.miguel.portomuinos.ui.screens.HomeScreen
import com.miguel.portomuinos.ui.screens.LoginScreen
import com.miguel.portomuinos.ui.screens.OnboardingScreen
import com.miguel.portomuinos.ui.screens.ProductDetailScreen
import com.miguel.portomuinos.ui.screens.ProfileScreen
import com.miguel.portomuinos.ui.screens.QrScannerScreen
import com.miguel.portomuinos.ui.screens.RecipeDetailScreen
import com.miguel.portomuinos.ui.screens.RecipesScreen
import com.miguel.portomuinos.ui.screens.RegisterScreen
import com.miguel.portomuinos.ui.screens.ResetPasswordScreen
import com.miguel.portomuinos.ui.screens.SplashScreen
import com.miguel.portomuinos.ui.screens.StoreScreen
import com.miguel.portomuinos.ui.search.matchesSearch
import com.miguel.portomuinos.ui.viewmodel.AlgaeChefViewModel
import com.miguel.portomuinos.ui.viewmodel.AuthViewModel
import com.miguel.portomuinos.ui.viewmodel.CartViewModel
import com.miguel.portomuinos.ui.viewmodel.CatalogViewModel
import com.miguel.portomuinos.ui.viewmodel.FavoritesViewModel
import kotlinx.coroutines.delay

/**
 * Grafo de navegación principal.
 *
 * Esta capa conecta las pantallas Compose con los ViewModels y mantiene
 * la navegación fuera de la lógica de negocio.
 */
@Composable
fun PortoNavHost(
    navController: NavHostController,
    mainContentPadding: PaddingValues,
    authViewModel: AuthViewModel,
    catalogViewModel: CatalogViewModel,
    favoritesViewModel: FavoritesViewModel,
    cartViewModel: CartViewModel,
    algaeChefViewModel: AlgaeChefViewModel,
    profileImageUri: String?,
    homeSearchQuery: String,
    startupCoverVisible: Boolean,
    onHomeSearchQueryChange: (String) -> Unit,
    onStartupCoverHidden: () -> Unit,
    onSplashFinished: () -> Unit,
    onOnboardingCompleted: () -> Unit,
    onProfileImageSelected: (String) -> Unit,
    onShowTransientMessage: (String) -> Unit,
    onOpenUri: (String) -> Unit
) {

    // Estado derivado que consumen las distintas rutas.

    val authState =
        authViewModel.uiState
            .collectAsStateWithLifecycle()
            .value

    val catalogState =
        catalogViewModel.uiState
            .collectAsStateWithLifecycle()
            .value

    val favoritesState =
        favoritesViewModel.uiState
            .collectAsStateWithLifecycle()
            .value

    val cartState =
        cartViewModel.uiState
            .collectAsStateWithLifecycle()
            .value

    val algaeChefState =
        algaeChefViewModel.uiState
            .collectAsStateWithLifecycle()
            .value

    val recipes =
        catalogState.catalogData
            ?.recipes
            .orEmpty()
            .map { recipe ->
                recipe.copy(
                    isFavorite =
                        recipe.id in
                            favoritesState.recipeIds
                )
            }

    val products =
        catalogState.catalogData
            ?.products
            .orEmpty()
            .map { product ->
                product.copy(
                    isFavorite =
                        product.id in
                            favoritesState.productIds
                )
            }

    val recipeProductRelations =
        catalogState.catalogData
            ?.relations
            .orEmpty()

    val homeSearchRecipes =
        if (homeSearchQuery.isBlank()) {
            emptyList()
        } else {
            recipes.filter {
                it.matchesSearch(
                    homeSearchQuery
                )
            }
        }

    val homeSearchProducts =
        if (homeSearchQuery.isBlank()) {
            emptyList()
        } else {
            products.filter {
                it.matchesSearch(
                    homeSearchQuery
                )
            }
        }

    val cartItemCount =
        cartState.quantities
            .count {
                it.value > 0
            }

    val featuredRecipes =
        recipes.take(3)
            .map { recipe ->
                FeaturedRecipeUi(
                    id = recipe.id,
                    title = recipe.title,
                    timeLabel =
                        "${recipe.durationMinutes} min",
                    difficultyLabel =
                        recipe.difficulty.label,
                    difficultyLevel =
                        recipe.difficulty.level,
                    imageRes =
                        recipe.imageRes,
                    imageUrl =
                        recipe.imageUrl
                )
            }

    val featuredProducts =
        products.take(3)
            .map { product ->
                FeaturedProductUi(
                    id = product.id,
                    name = product.name,
                    priceLabel =
                        product.price
                            .toPriceLabel(),
                    imageRes =
                        product.imageRes,
                    imageUrl =
                        product.imageUrl
                )
            }

    // Acción compartida por las pantallas que añaden productos al carrito.

    fun addProductToCart(
        productId: String,
        quantity: Int
    ) {

        val productName =
            products.firstOrNull {
                it.id == productId
            }
                ?.name
                ?: "Producto"

        cartViewModel.addToCart(
            productId = productId,
            quantity = quantity,
            productName = productName
        )
    }

    // Grafo de rutas de autenticación y contenido principal.

    NavHost(
        navController = navController,
        startDestination = AppRoutes.SPLASH,
        enterTransition = {
            EnterTransition.None
        },
        exitTransition = {
            ExitTransition.None
        },
        popEnterTransition = {
            EnterTransition.None
        },
        popExitTransition = {
            ExitTransition.None
        }
    ) {

        // Arranque y autenticación.

        composable(
            AppRoutes.SPLASH
        ) {
            SplashScreen(
                onFinished =
                    onSplashFinished
            )
        }

        composable(
            AppRoutes.ONBOARDING
        ) {

            OnboardingScreen(
                onLoginClick = {

                    onOnboardingCompleted()

                    navController.navigate(
                        AppRoutes.LOGIN
                    ) {
                        launchSingleTop = true
                    }
                },
                onRegisterClick = {

                    onOnboardingCompleted()

                    navController.navigate(
                        AppRoutes.REGISTER
                    ) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            AppRoutes.LOGIN
        ) {

            val previousRoute =
                navController
                    .previousBackStackEntry
                    ?.destination
                    ?.route

            LoginScreen(
                showBackButton =
                    previousRoute != null,
                onBackClick = {
                    navController
                        .popBackStack()
                },
                authErrorText =
                    authState.loginAuthError,
                infoText =
                    authState.loginInfoText,
                isLoading =
                    authState.loginIsLoading,
                onAuthMessageClear = {
                    authViewModel
                        .clearLoginMessages()
                },
                onLoginClick = {
                        email,
                        password ->
                    authViewModel.login(
                        email = email,
                        password = password
                    )
                },
                onRegisterClick = {

                    authViewModel
                        .prepareRegistration()

                    navController.navigate(
                        AppRoutes.REGISTER
                    ) {
                        launchSingleTop = true
                    }
                },
                onForgotPasswordClick = {

                    authViewModel
                        .prepareForgotPassword()

                    navController.navigate(
                        AppRoutes.FORGOT_PASSWORD
                    ) {
                        launchSingleTop = true
                    }
                },
                onSocialLoginClick = {
                        provider ->

                    if (
                        provider.equals(
                            "Google",
                            ignoreCase = true
                        )
                    ) {
                        authViewModel
                            .loginWithGoogle()
                    } else {
                        onShowTransientMessage(
                            "El acceso con $provider aún no está disponible."
                        )
                    }
                }
            )
        }

        composable(
            AppRoutes.FORGOT_PASSWORD
        ) {

            ForgotPasswordScreen(
                isLoading =
                    authState
                        .forgotPasswordIsLoading,
                authErrorText =
                    authState
                        .forgotPasswordAuthError,
                infoText =
                    authState
                        .forgotPasswordInfoText,
                onBackClick = {
                    navController
                        .popBackStack()
                },
                onSendClick = { email ->
                    authViewModel
                        .sendPasswordRecoveryEmail(
                            email
                        )
                },
                onAuthMessageClear = {
                    authViewModel
                        .clearForgotPasswordMessages()
                }
            )
        }

        composable(
            AppRoutes.RESET_PASSWORD
        ) {

            ResetPasswordScreen(
                isLoading =
                    authState
                        .resetPasswordIsLoading,
                authErrorText =
                    authState
                        .resetPasswordAuthError,
                onUpdatePasswordClick = {
                        newPassword ->
                    authViewModel
                        .updatePassword(
                            newPassword
                        )
                },
                onAuthErrorClear = {
                    authViewModel
                        .clearResetPasswordError()
                }
            )
        }

        composable(
            AppRoutes.REGISTER
        ) {

            val previousRoute =
                navController
                    .previousBackStackEntry
                    ?.destination
                    ?.route

            RegisterScreen(
                authErrorText =
                    authState.registerAuthError,
                isLoading =
                    authState.registerIsLoading,
                onAuthErrorClear = {
                    authViewModel
                        .clearRegisterError()
                },
                onBackClick = {
                    navController
                        .popBackStack()
                },
                onRegisterClick = {
                        fullName,
                        email,
                        password ->
                    authViewModel.register(
                        fullName = fullName,
                        email = email,
                        password = password
                    )
                },
                onLoginClick = {

                    authViewModel
                        .clearRegisterError()

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
            )
        }

        // Contenido principal de la aplicación.

        composable(AppRoutes.HOME) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment =
                    Alignment.BottomCenter
            ) {


                LaunchedEffect(startupCoverVisible) {
                    if (startupCoverVisible) {
                        /*
                         * Breve margen visual al restaurar una sesión desde un
                         * inicio en frío, antes de retirar la capa de Splash.
                         */
                        delay(280)
                        onStartupCoverHidden()
                    }
                }

                HomeScreen(
                    userName = authState.currentUserName,
                    profileImageUri =
                        profileImageUri,
                    featuredRecipes = featuredRecipes,
                    featuredProducts = featuredProducts,

                    recipes = homeSearchRecipes,
                    products = homeSearchProducts,

                    searchQuery = homeSearchQuery,
                    onSearchQueryChange = {
                        onHomeSearchQueryChange(it)
                    },

                    contentPadding = mainContentPadding,
                    favoriteRecipeIds =
                        favoritesState.recipeIds,
                    cartItemCount = cartItemCount,

                    onProfileClick = {
                        navController.navigate(
                            AppRoutes.PROFILE
                        ) {
                            launchSingleTop = true
                        }
                    },

                    onFavoritesClick = {
                        navController.navigate(
                            AppRoutes.FAVORITES
                        ) {
                            launchSingleTop = true
                        }
                    },

                    onCartClick = {
                        navController.navigate(
                            AppRoutes.CART
                        ) {
                            launchSingleTop = true
                        }
                    },

                    onAlgaeChefClick = {
                        navController.navigateToRoot(
                            AppRoutes.ALGAECHEF
                        )
                    },

                    onFeaturedRecipeClick = { recipeId ->
                        navController.navigate(
                            AppRoutes.recipeDetail(
                                recipeId
                            )
                        )
                    },

                    onFavoriteRecipeClick = { recipeId ->
                        favoritesViewModel.toggleRecipeFavorite(
                            recipeId
                        )
                    },

                    onViewAllRecipesClick = {
                        navController.navigateToRoot(
                            AppRoutes.RECIPES
                        )
                    },

                    onFeaturedProductClick = { productId ->
                        navController.navigate(
                            AppRoutes.productDetail(
                                productId
                            )
                        )
                    },

                    onAddProductClick = { productId ->
                        addProductToCart(
                            productId = productId,
                            quantity = 1
                        )
                    },

                    onViewAllProductsClick = {
                        navController.navigateToRoot(
                            AppRoutes.STORE
                        )
                    }
                )

                PortoMainBottomBar(
                    selectedItem =
                        BottomNavItem.HOME,
                    navController =
                        navController
                )
            }
        }

        composable(AppRoutes.RECIPES) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment =
                    Alignment.BottomCenter
            ) {


                RecipesScreen(
                    recipes = recipes,
                    contentPadding = mainContentPadding,
                    onRecipeClick = { recipeId ->
                        navController.navigate(
                            AppRoutes.recipeDetail(recipeId)
                        )
                    },
                    onFavoriteRecipeClick = {
                            recipeId ->
                        favoritesViewModel.toggleRecipeFavorite(
                            recipeId
                        )
                    }
                )

                PortoMainBottomBar(
                    selectedItem =
                        BottomNavItem.RECIPES,
                    navController =
                        navController
                )
            }
        }

        // Detalles navegables del catálogo.

        composable(
            route = AppRoutes.RECIPE_DETAIL,
            arguments = listOf(
                navArgument(AppRoutes.RECIPE_ID_ARGUMENT) {
                    type = NavType.StringType
                }
            )
        ) { entry ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment =
                    Alignment.BottomCenter
            ) {

                val recipeId = entry.arguments
                    ?.getString(AppRoutes.RECIPE_ID_ARGUMENT)

                val recipe =
                    recipes.firstOrNull {
                        it.id == recipeId
                    }

                val relatedProducts =
                    recipeProductRelations
                        .filter {
                            it.recipeId == recipeId
                        }
                        .mapNotNull {
                                relation ->
                            products.firstOrNull {
                                it.id ==
                                        relation.productId
                            }
                        }

                RecipeDetailScreen(
                    recipe = recipe,
                    relatedProducts =
                        relatedProducts,
                    contentPadding = PaddingValues(),
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onFavoriteClick = {
                            id ->
                        favoritesViewModel.toggleRecipeFavorite(id)
                    },
                    onProductClick = {
                            productId ->
                        navController.navigate(
                            AppRoutes.productDetail(
                                productId
                            )
                        )
                    },
                    onFavoriteProductClick = {
                            productId ->
                        favoritesViewModel.toggleProductFavorite(
                            productId
                        )
                    }
                )

                PortoMainBottomBar(
                    selectedItem =
                        BottomNavItem.RECIPES,
                    navController =
                        navController
                )
            }
        }

        composable(AppRoutes.STORE) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment =
                    Alignment.BottomCenter
            ) {


                StoreScreen(
                    products = products,
                    contentPadding = mainContentPadding,
                    cartItemCount = cartItemCount,
                    onCartClick = {
                        navController.navigate(
                            AppRoutes.CART
                        ) {
                            launchSingleTop = true
                        }
                    },
                    onProductClick = { productId ->
                        navController.navigate(
                            AppRoutes.productDetail(productId)
                        )
                    },
                    onAddProductClick = { productId ->
                        addProductToCart(
                            productId = productId,
                            quantity = 1
                        )
                    }
                )

                PortoMainBottomBar(
                    selectedItem =
                        BottomNavItem.STORE,
                    navController =
                        navController
                )
            }
        }

        composable(
            route = AppRoutes.PRODUCT_DETAIL,
            arguments = listOf(
                navArgument(AppRoutes.PRODUCT_ID_ARGUMENT) {
                    type = NavType.StringType
                }
            )
        ) { entry ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment =
                    Alignment.BottomCenter
            ) {

                val productId = entry.arguments
                    ?.getString(AppRoutes.PRODUCT_ID_ARGUMENT)

                val product =
                    products.firstOrNull {
                        it.id == productId
                    }

                val relatedRecipes =
                    recipeProductRelations
                        .filter {
                            it.productId == productId
                        }
                        .mapNotNull {
                                relation ->
                            recipes.firstOrNull {
                                it.id ==
                                        relation.recipeId
                            }
                        }

                ProductDetailScreen(
                    product = product,
                    relatedRecipes =
                        relatedRecipes,
                    contentPadding = mainContentPadding,
                    cartItemCount = cartItemCount,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onCartClick = {
                        navController.navigate(
                            AppRoutes.CART
                        ) {
                            launchSingleTop = true
                        }
                    },
                    onFavoriteClick = { id ->
                        favoritesViewModel.toggleProductFavorite(id)
                    },
                    onAddToCartClick = { id, quantity ->
                        addProductToCart(
                            productId = id,
                            quantity = quantity
                        )
                    },
                    onOfficialProductClick = {
                            sourceUrl ->

                        onOpenUri(
                            sourceUrl
                        )
                    },
                    onRecipeClick = {
                            recipeId ->
                        navController.navigate(
                            AppRoutes.recipeDetail(
                                recipeId
                            )
                        )
                    },
                    onFavoriteRecipeClick = {
                            recipeId ->
                        favoritesViewModel.toggleRecipeFavorite(
                            recipeId
                        )
                    }
                )

                PortoMainBottomBar(
                    selectedItem =
                        BottomNavItem.STORE,
                    navController =
                        navController
                )
            }
        }

        // Estado y acciones personales del usuario.

        composable(AppRoutes.CART) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment =
                    Alignment.BottomCenter
            ) {


                CartScreen(
                    products = products,
                    quantities = cartState.quantities,
                    contentPadding = mainContentPadding,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onProductClick = { productId ->
                        navController.navigate(
                            AppRoutes.productDetail(
                                productId
                            )
                        )
                    },
                    onIncrease = { productId ->
                        cartViewModel.increaseQuantity(
                            productId
                        )
                    },
                    onDecrease = { productId ->
                        cartViewModel.decreaseQuantity(
                            productId
                        )
                    },
                    onRemove = { productId ->
                        cartViewModel.removeProduct(
                            productId
                        )
                    },
                    onClearCart = {
                        cartViewModel.clearCart()
                    },
                    onContinueShopping = {
                        navController.navigateToRoot(
                            AppRoutes.STORE
                        )
                    },
                    onCheckoutClick = {
                        onOpenUri(
                            "https://www.portomuinos.com/shop/es/"
                        )
                    }
                )

                PortoMainBottomBar(
                    selectedItem =
                        BottomNavItem.STORE,
                    navController =
                        navController
                )
            }
        }

        composable(AppRoutes.PROFILE) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment =
                    Alignment.BottomCenter
            ) {


                ProfileScreen(
                    userName = authState.currentUserFullName,
                    profileImageUri =
                        profileImageUri,
                    favoriteRecipesCount =
                        favoritesState.recipeIds.size,
                    favoriteProductsCount =
                        favoritesState.productIds.size,
                    contentPadding = mainContentPadding,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onFavoritesClick = {
                        navController.navigate(
                            AppRoutes.FAVORITES
                        ) {
                            launchSingleTop = true
                        }
                    },
                    onProfileImageSelected = {
                            uri ->
                        onProfileImageSelected(uri)
                    },
                    onLogoutClick = {
                        authViewModel.logout()
                    },
                    onDeleteAccountClick = {
                        authViewModel.deleteAccount()
                    }
                )

                PortoMainBottomBar(
                    selectedItem =
                        BottomNavItem.HOME,
                    navController =
                        navController
                )
            }
        }

        composable(AppRoutes.FAVORITES) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment =
                    Alignment.BottomCenter
            ) {
                FavoritesScreen(
                    recipes = recipes.filter {
                        it.isFavorite
                    },
                    products = products.filter {
                        it.isFavorite
                    },
                    contentPadding =
                        mainContentPadding,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onRecipeClick = { recipeId ->
                        navController.navigate(
                            AppRoutes.recipeDetail(
                                recipeId
                            )
                        )
                    },
                    onProductClick = { productId ->
                        navController.navigate(
                            AppRoutes.productDetail(
                                productId
                            )
                        )
                    },
                    onFavoriteRecipeClick = {
                            recipeId ->
                        favoritesViewModel.toggleRecipeFavorite(
                            recipeId
                        )
                    },
                    onFavoriteProductClick = {
                            productId ->
                        favoritesViewModel.toggleProductFavorite(
                            productId
                        )
                    },
                    onClearRecipeFavorites = {
                        favoritesViewModel.clearRecipeFavorites()
                    },
                    onClearProductFavorites = {
                        favoritesViewModel.clearProductFavorites()
                    }
                )

                PortoMainBottomBar(
                    selectedItem =
                        BottomNavItem.HOME,
                    navController =
                        navController
                )
            }
        }


        // Herramientas transversales.

        composable(AppRoutes.QR_SCANNER) {
            QrScannerScreen(
                products = products,
                onBackClick = {
                    navController.popBackStack()
                },
                onProductDetected = { productId ->
                    navController.navigate(
                        AppRoutes.productDetail(productId)
                    ) {
                        /*
                         * Quitamos el scanner de la pila.
                         * Al pulsar atrás desde el producto no volvemos
                         * accidentalmente a una cámara ya escaneada.
                         */
                        popUpTo(AppRoutes.QR_SCANNER) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(AppRoutes.ALGAECHEF) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment =
                    Alignment.BottomCenter
            ) {


                AlgaeChefScreen(
                    quickSuggestions = algaeChefQuickSuggestions,
                    messages = algaeChefState.messages,
                    isThinking = algaeChefState.isThinking,
                    recipes = recipes,
                    contentPadding = mainContentPadding,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onNewConversation = {
                        algaeChefViewModel.resetConversation()
                    },
                    onSendMessage = { prompt ->
                        algaeChefViewModel.sendMessage(prompt = prompt, recipes = recipes)
                    },
                    onRecipeClick = { recipeId ->
                        navController.navigate(
                            AppRoutes.recipeDetail(recipeId)
                        )
                    },
                    onFavoriteRecipeClick = {
                            recipeId ->
                        favoritesViewModel.toggleRecipeFavorite(
                            recipeId
                        )
                    }
                )

                PortoMainBottomBar(
                    selectedItem =
                        BottomNavItem.ALGAECHEF,
                    navController =
                        navController
                )
            }
        }
    }
}
