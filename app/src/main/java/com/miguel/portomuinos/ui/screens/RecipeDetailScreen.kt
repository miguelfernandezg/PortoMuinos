/*
 * Responsabilidad: Detalle de receta con descripción, ingredientes, pasos, favorito y productos
 * relacionados.
 */

package com.miguel.portomuinos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miguel.portomuinos.ui.components.CatalogImage
import com.miguel.portomuinos.ui.components.FavoriteIconButton
import com.miguel.portomuinos.ui.components.PortoBackButton
import com.miguel.portomuinos.ui.components.PortoBackButtonStyle
import com.miguel.portomuinos.ui.components.PortoBottomBarOverlayClearance
import com.miguel.portomuinos.ui.components.RecipeMetaRow
import com.miguel.portomuinos.ui.model.ProductUi
import com.miguel.portomuinos.ui.model.RecipeUi
import com.miguel.portomuinos.ui.model.toPriceLabel
import com.miguel.portomuinos.ui.theme.Poppins
import com.miguel.portomuinos.ui.theme.PortoDarkGreen
import com.miguel.portomuinos.ui.theme.PortoMint
import com.miguel.portomuinos.ui.theme.PortoSurface
import com.miguel.portomuinos.ui.theme.PortoTextSecondary

@Composable
fun RecipeDetailScreen(
    recipe: RecipeUi?,
    relatedProducts: List<ProductUi> = emptyList(),
    contentPadding: PaddingValues = PaddingValues(),
    onBackClick: () -> Unit = {},
    onFavoriteClick: (String) -> Unit = {},
    onProductClick: (String) -> Unit = {},
    onFavoriteProductClick: (String) -> Unit = {}
) {
    // La pantalla solo se compone cuando la receta existe en el catálogo.

    if (recipe == null) {
        MissingRecipeContent(contentPadding, onBackClick)
        return
    }

    // La pestaña es estado puramente visual y se conserva en recreaciones.

    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    // Contenido principal de la receta.

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(
                bottom =
                    contentPadding.calculateBottomPadding() +
                            PortoBottomBarOverlayClearance
            )
    ) {
        RecipeHero(
            recipe = recipe,
            onBackClick = onBackClick,
            onFavoriteClick = { onFavoriteClick(recipe.id) }
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-30).dp)
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(Color.White)
                .padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 30.dp)
        ) {
            Text(
                text = recipe.title,
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 23.sp,
                lineHeight = 29.sp,
                color = Color.Black
            )
            Spacer(Modifier.height(10.dp))
            RecipeMetaRow(
                durationMinutes = recipe.durationMinutes,
                difficulty = recipe.difficulty,
                groupSpacing = 20.dp
            )

            if (recipe.description.isNotBlank()) {
                Spacer(Modifier.height(14.dp))

                Text(
                    text = recipe.description,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.5.sp,
                    lineHeight = 20.sp,
                    color = PortoTextSecondary
                )
            }

            Spacer(Modifier.height(20.dp))
            RecipeTabs(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
            Spacer(Modifier.height(18.dp))
            if (selectedTab == 0) {
                IngredientsContent(
                    recipe.ingredients
                )
            } else {
                StepsContent(
                    recipe.steps
                )
            }

            if (relatedProducts.isNotEmpty()) {
                Spacer(
                    Modifier.height(30.dp)
                )

                HorizontalDivider(
                    color =
                        PortoTextSecondary.copy(
                            alpha = 0.14f
                        )
                )

                Spacer(
                    Modifier.height(22.dp)
                )

                RelatedProductsSection(
                    products = relatedProducts,
                    onProductClick =
                        onProductClick,
                    onFavoriteProductClick =
                        onFavoriteProductClick
                )
            }
        }
    }
}


// Componentes internos de la pantalla.

@Composable
private fun RelatedProductsSection(
    products: List<ProductUi>,
    onProductClick: (String) -> Unit,
    onFavoriteProductClick: (String) -> Unit
) {
    Text(
        text = "Productos relacionados",
        fontFamily = Poppins,
        fontWeight = FontWeight.SemiBold,
        fontSize = 17.sp,
        color = PortoDarkGreen
    )

    Spacer(
        Modifier.height(3.dp)
    )

    Text(
        text =
            "Descubre productos Porto-Muiños vinculados a esta receta.",
        fontFamily = Poppins,
        fontSize = 11.5.sp,
        lineHeight = 16.sp,
        color = PortoTextSecondary
    )

    Spacer(
        Modifier.height(14.dp)
    )

    Column(
        verticalArrangement =
            Arrangement.spacedBy(10.dp)
    ) {
        products.forEach { product ->
            RelatedProductCard(
                product = product,
                onClick = {
                    onProductClick(
                        product.id
                    )
                },
                onFavoriteClick = {
                    onFavoriteProductClick(
                        product.id
                    )
                }
            )
        }
    }
}

@Composable
private fun RelatedProductCard(
    product: ProductUi,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(112.dp)
            .clip(
                RoundedCornerShape(17.dp)
            )
            .background(
                PortoSurface.copy(
                    alpha = 0.62f
                )
            )
            .clickable(
                onClick = onClick
            )
            .padding(10.dp),
        verticalAlignment =
            Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(88.dp)
                .clip(
                    RoundedCornerShape(13.dp)
                )
                .background(Color.White),
            contentAlignment =
                Alignment.Center
        ) {
            CatalogImage(
                imageRes = product.imageRes,
                imageUrl = product.imageUrl,
                contentDescription = product.name,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(5.dp)
            )
        }

        Spacer(
            Modifier.width(12.dp)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement =
                Arrangement.Center
        ) {
            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                verticalAlignment =
                    Alignment.Top
            ) {
                Text(
                    text = product.name,
                    fontFamily = Poppins,
                    fontWeight =
                        FontWeight.SemiBold,
                    fontSize = 12.5.sp,
                    lineHeight = 15.5.sp,
                    color = PortoDarkGreen,
                    maxLines = 2,
                    modifier =
                        Modifier.weight(1f)
                )

                Spacer(
                    Modifier.width(6.dp)
                )

                FavoriteIconButton(
                    isFavorite =
                        product.isFavorite,
                    onClick =
                        onFavoriteClick,
                    size = 32.dp,
                    iconSize = 17.dp,
                    backgroundColor =
                        Color.White.copy(
                            alpha = 0.88f
                        )
                )
            }

            Spacer(
                Modifier.weight(1f)
            )

            Text(
                text =
                    product.price.toPriceLabel(),
                fontFamily = Poppins,
                fontWeight =
                    FontWeight.SemiBold,
                fontSize = 14.sp,
                color = PortoDarkGreen
            )
        }
    }
}


@Composable
private fun RecipeHero(
    recipe: RecipeUi,
    onBackClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(326.dp)
            .background(PortoSurface)
    ) {
        CatalogImage(
            imageRes = recipe.imageRes,
            imageUrl = recipe.imageUrl,
            contentDescription = recipe.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(92.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.55f),
                            Color.White.copy(alpha = 0.18f),
                            Color.Transparent
                        )
                    )
                )
        )

        PortoBackButton(
            onClick = onBackClick,
            style = PortoBackButtonStyle.OVERLAY,
            size = 42.dp,
            iconSize = 22.dp,
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(start = 16.dp, top = 8.dp)
        )

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(
                    end = 16.dp,
                    top = 8.dp
                )
        ) {
            FavoriteIconButton(
                isFavorite =
                    recipe.isFavorite,
                onClick =
                    onFavoriteClick,
                size = 42.dp,
                iconSize = 20.dp
            )
        }
    }
}

@Composable
private fun RecipeTabs(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth()) {
            RecipeTab(
                text = "Ingredientes",
                selected = selectedTab == 0,
                onClick = { onTabSelected(0) },
                modifier = Modifier.weight(1f)
            )
            RecipeTab(
                text = "Paso a paso",
                selected = selectedTab == 1,
                onClick = { onTabSelected(1) },
                modifier = Modifier.weight(1f)
            )
        }
        HorizontalDivider(color = PortoTextSecondary.copy(alpha = 0.18f))
    }
}

@Composable
private fun RecipeTab(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().height(36.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = if (selected) PortoDarkGreen else PortoTextSecondary
            )
        }
        Box(
            modifier = Modifier
                .width(94.dp)
                .height(2.dp)
                .background(if (selected) PortoDarkGreen else Color.Transparent)
        )
    }
}

@Composable
private fun IngredientsContent(ingredients: List<String>) {
    Column(
        modifier = Modifier.padding(
            horizontal = 8.dp
        ),
        verticalArrangement =
            Arrangement.spacedBy(16.dp)
    ) {
        ingredients.forEach { ingredient ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(8.dp).background(PortoMint, CircleShape))
                Spacer(Modifier.width(12.dp))
                Text(
                    text = ingredient,
                    fontFamily = Poppins,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = PortoDarkGreen
                )
            }
        }
    }
}

@Composable
private fun StepsContent(steps: List<String>) {
    Column(
        modifier = Modifier.padding(
            horizontal = 8.dp
        ),
        verticalArrangement =
            Arrangement.spacedBy(18.dp)
    ) {
        steps.forEachIndexed { index, step ->
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .border(1.5.dp, PortoMint, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${index + 1}",
                        fontFamily = Poppins,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                        color = PortoDarkGreen
                    )
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    text = step,
                    fontFamily = Poppins,
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                    color = PortoDarkGreen,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun MissingRecipeContent(contentPadding: PaddingValues, onBackClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(contentPadding)
            .padding(14.dp)
    ) {
        PortoBackButton(
            onClick = onBackClick,
            style = PortoBackButtonStyle.PLAIN
        )
        Spacer(Modifier.height(24.dp))
        Text(
            text = "No se encontró la receta.",
            fontFamily = Poppins,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            color = PortoDarkGreen
        )
    }
}