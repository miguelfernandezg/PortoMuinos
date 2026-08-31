/*
 * Responsabilidad: Centraliza las rutas de navegación para evitar literales duplicados entre
 * pantallas.
 */

package com.miguel.portomuinos.ui.navigation

object AppRoutes {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val LOGIN = "login"
    const val REGISTER = "register"

    const val HOME = "home"
    const val RECIPES = "recipes"
    const val STORE = "store"
    const val ALGAECHEF = "algaechef"
    const val QR_SCANNER = "qr_scanner"
    const val CART = "cart"
    const val PROFILE = "profile"
    const val FAVORITES = "favorites"

    const val RECIPE_ID_ARGUMENT = "recipeId"
    const val RECIPE_DETAIL = "recipe/{$RECIPE_ID_ARGUMENT}"

    const val PRODUCT_ID_ARGUMENT = "productId"
    const val PRODUCT_DETAIL = "product/{$PRODUCT_ID_ARGUMENT}"

    fun recipeDetail(recipeId: String): String =
        "recipe/$recipeId"

    fun productDetail(productId: String): String =
        "product/$productId"

    const val FORGOT_PASSWORD =
        "forgot_password"

    const val RESET_PASSWORD =
        "reset_password"
}
