/*
 * Responsabilidad: Pantalla principal con bienvenida, búsqueda, accesos al catálogo y contenidos
 * destacados.
 */

package com.miguel.portomuinos.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miguel.portomuinos.R
import com.miguel.portomuinos.ui.components.FeaturedProductCard
import com.miguel.portomuinos.ui.components.FeaturedRecipeCard
import com.miguel.portomuinos.ui.components.HomeTopBar
import com.miguel.portomuinos.ui.components.PortoBottomBarOverlayClearance
import com.miguel.portomuinos.ui.components.PortoSearchBar
import com.miguel.portomuinos.ui.components.RecipeCard
import com.miguel.portomuinos.ui.components.StoreProductRow
import com.miguel.portomuinos.ui.model.ProductUi
import com.miguel.portomuinos.ui.model.RecipeUi
import com.miguel.portomuinos.ui.model.FeaturedProductUi
import com.miguel.portomuinos.ui.model.FeaturedRecipeUi
import com.miguel.portomuinos.ui.theme.Poppins
import com.miguel.portomuinos.ui.theme.PortoBackground
import com.miguel.portomuinos.ui.theme.PortoDarkGreen
import com.miguel.portomuinos.ui.theme.PortoMint
import com.miguel.portomuinos.ui.theme.PortoPrimary
import com.miguel.portomuinos.ui.theme.PortoTextSecondary

@Composable
fun HomeScreen(
    userName: String,
    profileImageUri: String? = null,
    featuredRecipes: List<FeaturedRecipeUi>,
    featuredProducts: List<FeaturedProductUi>,
    recipes: List<RecipeUi> = emptyList(),
    products: List<ProductUi> = emptyList(),
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    contentPadding: PaddingValues = PaddingValues(),
    favoriteRecipeIds: Set<String> = emptySet(),
    cartItemCount: Int = 0,
    onProfileClick: () -> Unit = {},
    onFavoritesClick: () -> Unit = {},
    onCartClick: () -> Unit = {},
    onAlgaeChefClick: () -> Unit = {},
    onFeaturedRecipeClick: (String) -> Unit = {},
    onFavoriteRecipeClick: (String) -> Unit = {},
    onViewAllRecipesClick: () -> Unit = {},
    onFeaturedProductClick: (String) -> Unit = {},
    onAddProductClick: (String) -> Unit = {},
    onViewAllProductsClick: () -> Unit = {}
) {
    // Composición principal de Home.

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PortoBackground)
            .padding(contentPadding)
            .verticalScroll(rememberScrollState())
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp)
        ) {
            HomeTopBar(
                userName = userName,
                profileImageUri =
                    profileImageUri,
                cartItemCount = cartItemCount,
                onProfileClick = onProfileClick,
                onFavoritesClick = onFavoritesClick,
                onCartClick = onCartClick
            )
        }

        Spacer(Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp)
        ) {
            PortoSearchBar(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = "Buscar recetas, productos..."
            )
        }

        Spacer(Modifier.height(23.dp))

        // Sin búsqueda se muestra contenido editorial; con texto, resultados filtrados.

        if (searchQuery.isBlank()) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp)
            ) {
                AlgaeChefBanner(
                    onClick = onAlgaeChefClick
                )
            }

            Spacer(
                Modifier.height(7.dp)
            )

            if (featuredRecipes.isNotEmpty()) {

                FeaturedRecipesSection(
                    recipes = featuredRecipes,
                    favoriteRecipeIds =
                        favoriteRecipeIds,
                    onRecipeClick =
                        onFeaturedRecipeClick,
                    onFavoriteClick =
                        onFavoriteRecipeClick,
                    onViewAllClick =
                        onViewAllRecipesClick
                )
            }

            if (
                featuredRecipes.isNotEmpty() &&
                featuredProducts.isNotEmpty()
            ) {
                Spacer(
                    Modifier.height(12.dp)
                )
            }

            if (featuredProducts.isNotEmpty()) {

                FeaturedProductsSection(
                    products = featuredProducts,
                    onProductClick =
                        onFeaturedProductClick,
                    onViewAllClick =
                        onViewAllProductsClick
                )
            }

        } else {

            HomeSearchResults(
                recipes = recipes,
                products = products,
                onRecipeClick =
                    onFeaturedRecipeClick,
                onFavoriteRecipeClick =
                    onFavoriteRecipeClick,
                onProductClick =
                    onFeaturedProductClick,
                onAddProductClick =
                    onAddProductClick
            )
        }

        Spacer(
            Modifier.height(
                24.dp + PortoBottomBarOverlayClearance
            )
        )
    }
}

// Componentes internos de la pantalla.

@Composable
private fun HomeSearchResults(
    recipes: List<RecipeUi>,
    products: List<ProductUi>,
    onRecipeClick: (String) -> Unit,
    onFavoriteRecipeClick: (String) -> Unit,
    onProductClick: (String) -> Unit,
    onAddProductClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp)
    ) {

        if (
            recipes.isEmpty() &&
            products.isEmpty()
        ) {
            Text(
                text = "No encontramos recetas ni productos.",
                fontFamily = Poppins,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = PortoTextSecondary,
                modifier = Modifier.padding(
                    horizontal = 8.dp
                )
            )

            return@Column
        }

        if (recipes.isNotEmpty()) {

            SearchSectionTitle(
                text = "Recetas"
            )

            Spacer(
                Modifier.height(10.dp)
            )

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

                Spacer(
                    Modifier.height(12.dp)
                )
            }
        }

        if (
            recipes.isNotEmpty() &&
            products.isNotEmpty()
        ) {
            Spacer(
                Modifier.height(8.dp)
            )
        }

        if (products.isNotEmpty()) {

            SearchSectionTitle(
                text = "Productos"
            )

            Spacer(
                Modifier.height(6.dp)
            )

            products.forEach { product ->

                StoreProductRow(
                    product = product,
                    onClick = {
                        onProductClick(
                            product.id
                        )
                    },
                    onAddClick = {
                        onAddProductClick(
                            product.id
                        )
                    }
                )

                HorizontalDivider(
                    color =
                        PortoTextSecondary.copy(
                            alpha = 0.14f
                        )
                )
            }
        }
    }
}

@Composable
private fun SearchSectionTitle(
    text: String
) {
    Text(
        text = text,
        fontFamily = Poppins,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        color = Color.Black
    )
}

@Composable
private fun AlgaeChefBanner(
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(86.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(15.dp))
                .background(PortoMint.copy(alpha = 0.80f))
        )

        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 21.dp)
        ) {
            Text(
                text = "¿Qué cocino hoy?",
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = PortoPrimary
            )

            Text(
                text = "Pregúntale a AlgaeChef",
                fontFamily = Poppins,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                color = PortoPrimary
            )
        }

        Image(
            painter = painterResource(R.drawable.mascota_senalando),
            contentDescription = "AlgaeChef",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(
                    x = 10.dp,
                    y = (-5).dp
                )
                .requiredSize(
                    width = 165.dp,
                    height = 245.dp
                )
        )
    }
}

@Composable
private fun FeaturedRecipesSection(
    recipes: List<FeaturedRecipeUi>,
    favoriteRecipeIds: Set<String>,
    onRecipeClick: (String) -> Unit,
    onFavoriteClick: (String) -> Unit,
    onViewAllClick: () -> Unit
) {
    val pagerState = rememberPagerState(
        pageCount = { recipes.size }
    )

    val currentRecipe =
        recipes.getOrNull(
            pagerState.currentPage
        ) ?: recipes.first()

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        SectionHeader(
            title = "Recetas destacadas",
            actionText = "Ver todas",
            onActionClick = onViewAllClick,
            modifier = Modifier.padding(
                horizontal = 14.dp
            )
        )

        BoxWithConstraints(
            modifier = Modifier.fillMaxWidth()
        ) {

            val cardHorizontalMargin = 28.dp
            val shadowSpace = 6.dp

            val cardWidth =
                maxWidth -
                    (cardHorizontalMargin * 2)

            val cardHeight =
                cardWidth * (227f / 345f)

            val pagerHeight =
                cardHeight +
                    (shadowSpace * 2)

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(pagerHeight),
                pageSpacing = 0.dp,
                userScrollEnabled =
                    recipes.size > 1
            ) { page ->

                val recipe = recipes[page]

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            horizontal =
                                cardHorizontalMargin,
                            vertical =
                                shadowSpace
                        )
                ) {

                    FeaturedRecipeCard(
                        imageRes =
                            recipe.imageRes,
                        imageUrl =
                            recipe.imageUrl,
                        contentDescription =
                            recipe.title,
                        isFavorite =
                            recipe.id in favoriteRecipeIds,
                        onClick = {
                            onRecipeClick(
                                recipe.id
                            )
                        },
                        onFavoriteClick = {
                            onFavoriteClick(
                                recipe.id
                            )
                        }
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 28.dp
                )
        ) {

            Spacer(
                Modifier.height(8.dp)
            )

            Text(
                text = currentRecipe.title,
                fontFamily = Poppins,
                fontWeight =
                    FontWeight.SemiBold,
                fontSize = 13.5.sp,
                lineHeight = 18.5.sp,
                color = PortoDarkGreen
            )

            Spacer(
                Modifier.height(4.dp)
            )

            RecipeMetadataRow(
                timeText =
                    currentRecipe.timeLabel,
                difficulty =
                    currentRecipe.difficultyLabel,
                difficultyLevel =
                    currentRecipe.difficultyLevel
            )

            if (recipes.size > 1) {

                Spacer(
                    Modifier.height(10.dp)
                )

                PagerIndicator(
                    pageCount = recipes.size,
                    currentPage =
                        pagerState.currentPage
                )
            }
        }
    }
}

@Composable
private fun RecipeMetadataRow(
    timeText: String,
    difficulty: String,
    difficultyLevel: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        RecipeMetadata(
            text = timeText,
            iconRes = R.drawable.ic_time_compact
        )

        DifficultyMetadata(
            difficulty = difficulty,
            level = difficultyLevel
        )
    }
}

@Composable
private fun RecipeMetadata(
    text: String,
    iconRes: Int
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            modifier = Modifier
                .size(18.dp),
            tint = PortoTextSecondary
        )

        Spacer(
            Modifier.width(4.dp)
        )

        Text(
            text = text,
            fontFamily = Poppins,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            color = PortoTextSecondary
        )
    }
}

@Composable
private fun DifficultyMetadata(
    difficulty: String,
    level: Int
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        DifficultyIndicator(
            level = level
        )

        Spacer(
            Modifier.width(5.dp)
        )

        Text(
            text = difficulty,
            fontFamily = Poppins,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            color = PortoTextSecondary
        )
    }
}

@Composable
private fun DifficultyIndicator(
    level: Int
) {
    val safeLevel = level.coerceIn(1, 3)

    Row(
        modifier = Modifier
            .width(20.dp)
            .height(18.dp),
        horizontalArrangement =
            Arrangement.spacedBy(2.5.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        listOf(
            7.dp,
            12.dp,
            16.dp
        ).forEachIndexed { index, barHeight ->

            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(barHeight)
                    .clip(
                        RoundedCornerShape(1.5.dp)
                    )
                    .background(
                        if (index < safeLevel) {
                            PortoPrimary.copy(
                                alpha = 0.65f
                            )
                        } else {
                            PortoTextSecondary.copy(
                                alpha = 0.20f
                            )
                        }
                    )
            )
        }
    }
}

@Composable
private fun PagerIndicator(
    pageCount: Int,
    currentPage: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->

            val selected = index == currentPage

            Box(
                modifier = Modifier
                    .padding(horizontal = 3.dp)
                    .width(
                        if (selected) {
                            18.dp
                        } else {
                            6.dp
                        }
                    )
                    .height(6.dp)
                    .clip(CircleShape)
                    .background(
                        if (selected) {
                            PortoPrimary
                        } else {
                            PortoTextSecondary.copy(alpha = 0.25f)
                        }
                    )
            )
        }
    }
}

@Composable
private fun FeaturedProductsSection(
    products: List<FeaturedProductUi>,
    onProductClick: (String) -> Unit,
    onViewAllClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        SectionHeader(
            title = "Productos destacados",
            actionText = "Ver todos",
            onActionClick = onViewAllClick,
            modifier = Modifier.padding(horizontal = 14.dp)
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(
                start = 28.dp,
                end = 28.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = products,
                key = { it.id }
            ) { product ->

                FeaturedProductCard(
                    imageRes = product.imageRes,
                    imageUrl = product.imageUrl,
                    name = product.name,
                    price = product.priceLabel,
                    onClick = {
                        onProductClick(product.id)
                    }
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    actionText: String,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontFamily = Poppins,
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
            color = Color.Black
        )

        Box(
            modifier = Modifier
                .height(41.dp)
                .clickable(onClick = onActionClick)
                .padding(horizontal = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = actionText,
                fontFamily = Poppins,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = PortoDarkGreen
            )
        }
    }
}


