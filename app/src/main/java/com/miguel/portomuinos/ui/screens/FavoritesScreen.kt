/*
 * Responsabilidad: Pantalla que agrupa recetas y productos favoritos del usuario y permite
 * gestionar ambos conjuntos.
 */

package com.miguel.portomuinos.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miguel.portomuinos.R
import com.miguel.portomuinos.ui.components.CatalogImage
import com.miguel.portomuinos.ui.components.FavoriteIconButton
import com.miguel.portomuinos.ui.components.PortoBottomBarOverlayClearance
import com.miguel.portomuinos.ui.components.RecipeCard
import com.miguel.portomuinos.ui.model.ProductUi
import com.miguel.portomuinos.ui.model.RecipeUi
import com.miguel.portomuinos.ui.model.toPriceLabel
import com.miguel.portomuinos.ui.theme.Poppins
import com.miguel.portomuinos.ui.theme.PortoDarkGreen
import com.miguel.portomuinos.ui.theme.PortoMint
import com.miguel.portomuinos.ui.theme.PortoSurface
import com.miguel.portomuinos.ui.theme.PortoTextSecondary

private const val FAVORITE_RECIPES_TAB = 0
private const val FAVORITE_PRODUCTS_TAB = 1

private val FavoriteDividerColor =
    Color(0xFFE8EEEC)

@Composable
fun FavoritesScreen(
    recipes: List<RecipeUi>,
    products: List<ProductUi>,
    contentPadding: PaddingValues = PaddingValues(),
    onBackClick: () -> Unit = {},
    onRecipeClick: (String) -> Unit = {},
    onProductClick: (String) -> Unit = {},
    onFavoriteRecipeClick: (String) -> Unit = {},
    onFavoriteProductClick: (String) -> Unit = {},
    onClearRecipeFavorites: () -> Unit = {},
    onClearProductFavorites: () -> Unit = {}
) {
    // Estado visual de pestaña y diálogo de vaciado.

    var selectedTab by rememberSaveable {
        mutableIntStateOf(FAVORITE_RECIPES_TAB)
    }

    var showClearDialog by rememberSaveable {
        mutableStateOf(false)
    }

    val selectedHasItems =
        if (selectedTab == FAVORITE_RECIPES_TAB) {
            recipes.isNotEmpty()
        } else {
            products.isNotEmpty()
        }

    // Contenido de la pestaña seleccionada.

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(contentPadding)
    ) {
        FavoritesHeader(
            canClear = selectedHasItems,
            onBackClick = onBackClick,
            onClearClick = {
                showClearDialog = true
            }
        )

        FavoritesTabs(
            selectedTab = selectedTab,
            onTabSelected = {
                selectedTab = it
            },
            modifier = Modifier.padding(
                horizontal = 20.dp
            )
        )

        Spacer(Modifier.height(16.dp))

        if (selectedTab == FAVORITE_RECIPES_TAB) {
            FavoriteRecipesContent(
                recipes = recipes,
                onRecipeClick = onRecipeClick,
                onFavoriteRecipeClick =
                    onFavoriteRecipeClick
            )
        } else {
            FavoriteProductsContent(
                products = products,
                onProductClick = onProductClick,
                onFavoriteProductClick =
                    onFavoriteProductClick
            )
        }
    }

    // Confirmación antes de eliminar todos los favoritos del tipo activo.

    if (showClearDialog) {
        val clearingRecipes =
            selectedTab == FAVORITE_RECIPES_TAB

        AlertDialog(
            onDismissRequest = {
                showClearDialog = false
            },
            title = {
                Text(
                    text =
                        if (clearingRecipes) {
                            "¿Vaciar recetas favoritas?"
                        } else {
                            "¿Vaciar productos favoritos?"
                        },
                    fontFamily = Poppins,
                    fontWeight =
                        FontWeight.SemiBold,
                    color = Color.Black
                )
            },
            text = {
                Text(
                    text =
                        if (clearingRecipes) {
                            "Se eliminarán todas las recetas guardadas en favoritos."
                        } else {
                            "Se eliminarán todos los productos guardados en favoritos."
                        },
                    fontFamily = Poppins,
                    fontWeight =
                        FontWeight.Normal,
                    color =
                        PortoTextSecondary
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (clearingRecipes) {
                            onClearRecipeFavorites()
                        } else {
                            onClearProductFavorites()
                        }

                        showClearDialog = false
                    }
                ) {
                    Text(
                        text = "Vaciar",
                        fontFamily = Poppins,
                        fontWeight =
                            FontWeight.SemiBold,
                        color =
                            Color(0xFFC94B4B)
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showClearDialog = false
                    }
                ) {
                    Text(
                        text = "Cancelar",
                        fontFamily = Poppins,
                        fontWeight =
                            FontWeight.Medium,
                        color =
                            PortoDarkGreen
                    )
                }
            },
            containerColor = Color.White
        )
    }
}

// Componentes internos de la pantalla.

@Composable
private fun FavoritesHeader(
    canClear: Boolean,
    onBackClick: () -> Unit,
    onClearClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(66.dp)
            .padding(
                start = 14.dp,
                end = 10.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clickable(onClick = onBackClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.ArrowBack,
                contentDescription = "Volver",
                tint = PortoDarkGreen,
                modifier = Modifier.size(23.dp)
            )
        }

        Text(
            text = "Mis favoritos",
            fontFamily = Poppins,
            fontWeight = FontWeight.SemiBold,
            fontSize = 25.sp,
            color = Color.Black
        )

        Spacer(Modifier.weight(1f))

        if (canClear) {
            TextButton(
                onClick = onClearClick
            ) {
                Text(
                    text = "Vaciar",
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.5.sp,
                    color = PortoDarkGreen.copy(
                        alpha = 0.72f
                    )
                )
            }
        }
    }
}

@Composable
private fun FavoritesTabs(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = PortoSurface,
                shape = RoundedCornerShape(17.dp)
            )
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        FavoriteTab(
            text = "Recetas",
            selected =
                selectedTab == FAVORITE_RECIPES_TAB,
            onClick = {
                onTabSelected(FAVORITE_RECIPES_TAB)
            },
            modifier = Modifier.weight(1f)
        )

        FavoriteTab(
            text = "Productos",
            selected =
                selectedTab == FAVORITE_PRODUCTS_TAB,
            onClick = {
                onTabSelected(FAVORITE_PRODUCTS_TAB)
            },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun FavoriteTab(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(40.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (selected) {
                    PortoMint.copy(alpha = 0.72f)
                } else {
                    Color.Transparent
                }
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontFamily = Poppins,
            fontWeight =
                if (selected) {
                    FontWeight.SemiBold
                } else {
                    FontWeight.Medium
                },
            fontSize = 13.sp,
            color =
                if (selected) {
                    PortoDarkGreen
                } else {
                    PortoTextSecondary
                }
        )
    }
}

@Composable
private fun FavoriteRecipesContent(
    recipes: List<RecipeUi>,
    onRecipeClick: (String) -> Unit,
    onFavoriteRecipeClick: (String) -> Unit
) {
    if (recipes.isEmpty()) {
        FavoriteEmptyState(
            title = "Aún no tienes recetas favoritas",
            subtitle =
                "Guarda las recetas que quieras cocinar más adelante."
        )
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            bottom =
                24.dp +
                    PortoBottomBarOverlayClearance
        ),
        verticalArrangement =
            Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = recipes,
            key = { it.id }
        ) { recipe ->
            RecipeCard(
                recipe = recipe,
                onClick = {
                    onRecipeClick(recipe.id)
                },
                onFavoriteClick = {
                    onFavoriteRecipeClick(recipe.id)
                }
            )
        }
    }
}

@Composable
private fun FavoriteProductsContent(
    products: List<ProductUi>,
    onProductClick: (String) -> Unit,
    onFavoriteProductClick: (String) -> Unit
) {
    if (products.isEmpty()) {
        FavoriteEmptyState(
            title = "Aún no tienes productos favoritos",
            subtitle =
                "Guarda los productos que quieras descubrir o comprar más adelante."
        )
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            bottom =
                24.dp +
                    PortoBottomBarOverlayClearance
        )
    ) {
        itemsIndexed(
            items = products,
            key = { _, product ->
                product.id
            }
        ) { index, product ->
            FavoriteStoreProductRow(
                product = product,
                onClick = {
                    onProductClick(product.id)
                },
                onFavoriteClick = {
                    onFavoriteProductClick(product.id)
                }
            )

            if (index < products.lastIndex) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(
                            FavoriteDividerColor
                        )
                )
            }
        }
    }
}

@Composable
private fun FavoriteStoreProductRow(
    product: ProductUi,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(172.dp)
            .clickable(onClick = onClick)
            .padding(
                vertical = 10.dp,
                horizontal = 2.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(112.dp)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            CatalogImage(
                imageRes = product.imageRes,
                imageUrl = product.imageUrl,
                contentDescription = product.name,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
            )
        }

        Spacer(Modifier.width(14.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = product.name,
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                lineHeight = 19.sp,
                color = Color.Black,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = product.description,
                fontFamily = Poppins,
                fontSize = 11.5.sp,
                lineHeight = 14.sp,
                color = PortoTextSecondary,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 12.dp),
                horizontalArrangement =
                    Arrangement.SpaceBetween,
                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                Text(
                    text = product.price.toPriceLabel(),
                    fontFamily = Poppins,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = Color.Black
                )

                FavoriteIconButton(
                    isFavorite = true,
                    onClick = onFavoriteClick,
                    size = 38.dp,
                    iconSize = 19.dp,
                    backgroundColor = PortoSurface
                )
            }
        }
    }
}

@Composable
private fun FavoriteEmptyState(
    title: String,
    subtitle: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(
                start = 28.dp,
                end = 28.dp,
                bottom =
                    PortoBottomBarOverlayClearance
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(
                    R.drawable.mascota_pensando
                ),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(118.dp)
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text = title,
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = PortoDarkGreen
            )

            Spacer(Modifier.height(5.dp))

            Text(
                text = subtitle,
                fontFamily = Poppins,
                fontSize = 12.5.sp,
                lineHeight = 18.sp,
                color = PortoTextSecondary
            )
        }
    }
}
