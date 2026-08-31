/*
 * Responsabilidad: Detalle de producto con información, favorito, cantidad, carrito, enlace
 * oficial y recetas relacionadas.
 */

package com.miguel.portomuinos.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miguel.portomuinos.R
import com.miguel.portomuinos.ui.components.CartIconButton
import com.miguel.portomuinos.ui.components.CatalogImage
import com.miguel.portomuinos.ui.components.PortoBackButton
import com.miguel.portomuinos.ui.components.RecipeCard
import com.miguel.portomuinos.ui.components.PortoBottomBarOverlayClearance
import com.miguel.portomuinos.ui.components.PortoBackButtonStyle
import com.miguel.portomuinos.ui.model.ProductUi
import com.miguel.portomuinos.ui.model.RecipeUi
import com.miguel.portomuinos.ui.model.toPriceLabel
import com.miguel.portomuinos.ui.theme.Poppins
import com.miguel.portomuinos.ui.theme.PortoDarkGreen
import com.miguel.portomuinos.ui.theme.PortoSurface
import com.miguel.portomuinos.ui.theme.PortoTextSecondary

@Composable
fun ProductDetailScreen(
    product: ProductUi?,
    relatedRecipes: List<RecipeUi> = emptyList(),
    contentPadding: PaddingValues = PaddingValues(),
    cartItemCount: Int = 0,
    onBackClick: () -> Unit = {},
    onCartClick: () -> Unit = {},
    onFavoriteClick: (String) -> Unit = {},
    onAddToCartClick:
        (productId: String, quantity: Int) -> Unit =
        { _, _ -> },
    onOfficialProductClick: (String) -> Unit = {},
    onRecipeClick: (String) -> Unit = {},
    onFavoriteRecipeClick: (String) -> Unit = {}
) {
    // La pantalla solo se compone cuando el producto existe en el catálogo.

    if (product == null) {

        MissingProductContent(
            contentPadding = contentPadding,
            onBackClick = onBackClick
        )

        return
    }

    // Cantidad elegida antes de añadir el producto al carrito.

    var quantity by rememberSaveable(product.id) {
        mutableIntStateOf(1)
    }

    // Contenido principal del producto.

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(contentPadding)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {

        ProductDetailHeader(
            isFavorite = product.isFavorite,
            cartItemCount = cartItemCount,
            onBackClick = onBackClick,
            onCartClick = onCartClick,
            onFavoriteClick = {
                onFavoriteClick(product.id)
            }
        )

        Spacer(
            modifier = Modifier.height(2.dp)
        )

        ProductHeroImage(
            product = product
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Text(
            text = product.name.uppercase(),
            fontFamily = Poppins,
            fontWeight = FontWeight.SemiBold,
            fontSize = 27.sp,
            lineHeight = 29.sp,
            color = Color.Black
        )

        if (product.subtitle.isNotBlank()) {

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = product.subtitle,
                fontFamily = Poppins,
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
                color = PortoTextSecondary
            )
        }

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        RatingPriceRow(
            product = product
        )

        if (product.description.isNotBlank()) {

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text(
                text = product.description,
                fontFamily = Poppins,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                lineHeight = 21.sp,
                color = PortoTextSecondary
            )
        }

        Spacer(
            modifier = Modifier.height(22.dp)
        )

        QuantitySelector(
            quantity = quantity,
            onDecrease = {
                if (quantity > 1) {
                    quantity--
                }
            },
            onIncrease = {
                quantity++
            }
        )

        Spacer(
            modifier = Modifier.height(22.dp)
        )

        Button(
            onClick = {
                onAddToCartClick(
                    product.id,
                    quantity
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PortoDarkGreen,
                contentColor = Color.White
            )
        ) {

            Text(
                text = "Añadir al carrito",
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
        }

        if (
            !product.sourceUrl
                .isNullOrBlank()
        ) {

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            OutlinedButton(
                onClick = {
                    onOfficialProductClick(
                        product.sourceUrl
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(
                    width = 1.dp,
                    color =
                        PortoDarkGreen.copy(
                            alpha = 0.28f
                        )
                ),
                colors =
                    ButtonDefaults
                        .outlinedButtonColors(
                            containerColor =
                                Color.White,
                            contentColor =
                                PortoDarkGreen
                        )
            ) {

                Text(
                    text =
                        "Ver en Porto-Muiños  ↗",
                    fontFamily = Poppins,
                    fontWeight =
                        FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }
        }

        if (relatedRecipes.isNotEmpty()) {
            Spacer(
                modifier = Modifier.height(
                    30.dp
                )
            )

            RelatedRecipesSection(
                recipes = relatedRecipes,
                onRecipeClick =
                    onRecipeClick,
                onFavoriteRecipeClick =
                    onFavoriteRecipeClick
            )
        }

        Spacer(
            modifier = Modifier.height(
                30.dp +
                        PortoBottomBarOverlayClearance
            )
        )
    }
}


// Componentes internos de la pantalla.

@Composable
private fun RelatedRecipesSection(
    recipes: List<RecipeUi>,
    onRecipeClick: (String) -> Unit,
    onFavoriteRecipeClick: (String) -> Unit
) {
    Text(
        text = "Recetas relacionadas",
        fontFamily = Poppins,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        color = PortoDarkGreen
    )

    Spacer(
        modifier = Modifier.height(3.dp)
    )

    Text(
        text =
            "Ideas para descubrir este producto en la cocina.",
        fontFamily = Poppins,
        fontWeight = FontWeight.Normal,
        fontSize = 11.5.sp,
        lineHeight = 16.sp,
        color = PortoTextSecondary
    )

    Spacer(
        modifier = Modifier.height(14.dp)
    )

    Column(
        verticalArrangement =
            Arrangement.spacedBy(12.dp)
    ) {
        recipes.forEach { recipe ->
            RecipeCard(
                recipe = recipe,
                onClick = {
                    onRecipeClick(
                        recipe.id
                    )
                },
                onFavoriteClick = {
                    onFavoriteRecipeClick(
                        recipe.id
                    )
                }
            )
        }
    }
}


@Composable
private fun ProductDetailHeader(
    isFavorite: Boolean,
    cartItemCount: Int,
    onBackClick: () -> Unit,
    onCartClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    /*
     * Box en lugar de un Row con SpaceBetween:
     * así "Producto" permanece geométricamente centrado
     * aunque a la derecha haya dos acciones.
     */
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
    ) {

        PortoBackButton(
            onClick = onBackClick,
            style = PortoBackButtonStyle.PLAIN,
            size = 44.dp,
            iconSize = 23.dp,
            modifier = Modifier.align(
                Alignment.CenterStart
            )
        )

        Text(
            text = "Producto",
            fontFamily = Poppins,
            fontWeight =
                FontWeight.SemiBold,
            fontSize = 24.sp,
            color = Color.Black,
            modifier = Modifier.align(
                Alignment.Center
            )
        )

        Row(
            modifier = Modifier.align(
                Alignment.CenterEnd
            ),
            horizontalArrangement =
                Arrangement.spacedBy(6.dp),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            SmallCircularAction(
                onClick =
                    onFavoriteClick
            ) {
                Icon(
                    imageVector =
                        if (isFavorite) {
                            Icons.Outlined.Favorite
                        } else {
                            Icons.Outlined.FavoriteBorder
                        },
                    contentDescription =
                        "Favorito",
                    tint =
                        PortoDarkGreen,
                    modifier =
                        Modifier.size(20.dp)
                )
            }

            /*
             * El contenedor exterior no se recorta para permitir que
             * el badge del carrito sobresalga sin perder el contador.
             */
            Box(
                modifier = Modifier.size(46.dp),
                contentAlignment =
                    Alignment.Center
            ) {

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(
                            color = PortoSurface,
                            shape = CircleShape
                        )
                )

                CartIconButton(
                    itemCount =
                        cartItemCount,
                    onClick =
                        onCartClick,
                    modifier =
                        Modifier.size(46.dp),
                    iconSize = 25.dp,
                    badgeSize = 17.dp
                )
            }
        }
    }
}

@Composable
private fun SmallCircularAction(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(PortoSurface)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
private fun ProductHeroImage(
    product: ProductUi
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(305.dp),
        contentAlignment = Alignment.Center
    ) {

        CatalogImage(
            imageRes = product.imageRes,
            imageUrl = product.imageUrl,
            contentDescription = product.name,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun RatingPriceRow(
    product: ProductUi
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {

        if (
            product.rating != null &&
            product.reviewCount != null
        ) {

            Icon(
                painter = painterResource(
                    R.drawable.ic_star_soft
                ),
                contentDescription =
                    "Valoración",
                tint = Color.Unspecified,
                modifier =
                    Modifier.size(19.dp)
            )

            Spacer(
                modifier = Modifier.size(5.dp)
            )

            Text(
                text = product.rating.toString(),
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = Color.Black
            )

            Spacer(
                modifier = Modifier.size(5.dp)
            )

            Text(
                text = "(${product.reviewCount})",
                fontFamily = Poppins,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
                color = PortoTextSecondary
            )

            Spacer(
                modifier = Modifier.size(14.dp)
            )
        }

        Text(
            text = product.price.toPriceLabel(),
            fontFamily = Poppins,
            fontWeight = FontWeight.SemiBold,
            fontSize = 26.sp,
            color = PortoDarkGreen
        )
    }
}

@Composable
private fun QuantitySelector(
    quantity: Int,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        QuantityButton(
            enabled = quantity > 1,
            onClick = onDecrease
        ) {

            Text(
                text = "−",
                fontFamily = Poppins,
                fontWeight = FontWeight.Medium,
                fontSize = 20.sp,
                color =
                    if (quantity > 1) {
                        PortoDarkGreen
                    } else {
                        PortoTextSecondary.copy(
                            alpha = 0.4f
                        )
                    }
            )
        }

        Text(
            text = quantity.toString(),
            fontFamily = Poppins,
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            color = Color.Black,
            modifier = Modifier.padding(
                horizontal = 34.dp
            )
        )

        QuantityButton(
            enabled = true,
            onClick = onIncrease
        ) {

            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = "Aumentar cantidad",
                tint = PortoDarkGreen,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun QuantityButton(
    enabled: Boolean,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(
                PortoSurface.copy(alpha = 0.55f)
            )
            .clickable(
                enabled = enabled,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
private fun MissingProductContent(
    contentPadding: PaddingValues,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(contentPadding)
            .padding(20.dp)
    ) {

        PortoBackButton(
            onClick = onBackClick,
            style = PortoBackButtonStyle.PLAIN
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Text(
            text = "No se encontró el producto.",
            fontFamily = Poppins,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            color = PortoDarkGreen
        )
    }
}