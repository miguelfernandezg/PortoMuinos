/*
 * Responsabilidad: Agrupa la navegación raíz y la configuración de la barra inferior principal.
 */

package com.miguel.portomuinos.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.miguel.portomuinos.ui.components.BottomNavItem
import com.miguel.portomuinos.ui.components.PortoBottomBar

// Rutas en las que la navegación inferior permanece visible.

val bottomBarRoutes =
    setOf(
        AppRoutes.HOME,
        AppRoutes.RECIPES,
        AppRoutes.RECIPE_DETAIL,
        AppRoutes.STORE,
        AppRoutes.PRODUCT_DETAIL,
        AppRoutes.ALGAECHEF,
        AppRoutes.CART,
        AppRoutes.PROFILE,
        AppRoutes.FAVORITES
    )

// Navegación entre destinos principales sin duplicar entradas en la pila.

fun NavHostController.navigateToRoot(
    route: String
) {
    navigate(route) {
        popUpTo(AppRoutes.HOME) {
            inclusive = false
        }

        launchSingleTop = true
    }
}

@Composable
fun PortoMainBottomBar(
    selectedItem: BottomNavItem,
    navController: NavHostController
) {
    PortoBottomBar(
        selectedItem = selectedItem,

        onHomeClick = {
            navController.navigateToRoot(
                AppRoutes.HOME
            )
        },

        onRecipesClick = {
            navController.navigateToRoot(
                AppRoutes.RECIPES
            )
        },

        onQrClick = {
            navController.navigate(
                AppRoutes.QR_SCANNER
            ) {
                launchSingleTop = true
            }
        },

        onAlgaeChefClick = {
            navController.navigateToRoot(
                AppRoutes.ALGAECHEF
            )
        },

        onStoreClick = {
            navController.navigateToRoot(
                AppRoutes.STORE
            )
        }
    )
}