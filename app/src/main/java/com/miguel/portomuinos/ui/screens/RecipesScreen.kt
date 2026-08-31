/*
 * Responsabilidad: Listado y filtrado de recetas disponibles en el catálogo.
 */

package com.miguel.portomuinos.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miguel.portomuinos.ui.components.PortoSearchBar
import com.miguel.portomuinos.ui.components.PortoBottomBarOverlayClearance
import com.miguel.portomuinos.ui.components.RecipeCard
import com.miguel.portomuinos.ui.model.RecipeCategory
import com.miguel.portomuinos.ui.model.RecipeUi
import com.miguel.portomuinos.ui.search.matchesSearch
import com.miguel.portomuinos.ui.theme.Poppins
import com.miguel.portomuinos.ui.theme.PortoDarkGreen
import com.miguel.portomuinos.ui.theme.PortoMint
import com.miguel.portomuinos.ui.theme.PortoSurface
import com.miguel.portomuinos.ui.theme.PortoTextSecondary

@Composable
fun RecipesScreen(
    recipes: List<RecipeUi>,
    contentPadding: PaddingValues = PaddingValues(),
    onRecipeClick: (String) -> Unit = {},
    onFavoriteRecipeClick: (String) -> Unit = {}
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedCategoryName by rememberSaveable { mutableStateOf<String?>(null) }

    val selectedCategory =
        RecipeCategory.entries.firstOrNull {
            it.name == selectedCategoryName
        }

    val filteredRecipes =
        recipes.filter { recipe ->

            val matchesSearch =
                recipe.matchesSearch(
                    searchQuery
                )

            val matchesCategory =
                selectedCategory == null ||
                        recipe.category ==
                        selectedCategory

            matchesSearch &&
                    matchesCategory
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(contentPadding)
            .padding(horizontal = 14.dp)
    ) {
        Text(
            text = "Recetas",
            fontFamily = Poppins,
            fontWeight = FontWeight.SemiBold,
            fontSize = 26.sp,
            color = Color.Black,
            modifier = Modifier.padding(start = 8.dp)
        )
        Spacer(Modifier.height(14.dp))
        PortoSearchBar(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = "Buscar recetas..."
        )
        Spacer(Modifier.height(14.dp))
        RecipeCategoryFilters(
            selectedCategory = selectedCategory,
            onCategorySelected = { selectedCategoryName = it?.name }
        )
        Spacer(Modifier.height(16.dp))

        if (filteredRecipes.isEmpty()) {
            Text(
                text = "No encontramos recetas con esos filtros.",
                fontFamily = Poppins,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = PortoTextSecondary,
                modifier = Modifier.padding(start = 8.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 2.dp,
                    end = 2.dp,
                    bottom = 24.dp + PortoBottomBarOverlayClearance
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredRecipes, key = { it.id }) { recipe ->
                    RecipeCard(
                        recipe = recipe,
                        onClick = { onRecipeClick(recipe.id) },
                        onFavoriteClick = { onFavoriteRecipeClick(recipe.id) }
                    )
                }
            }
        }
    }
}

// Componentes internos de la pantalla.

@Composable
private fun RecipeCategoryFilters(
    selectedCategory: RecipeCategory?,
    onCategorySelected: (RecipeCategory?) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CategoryChip(
            text = "Todas",
            selected = selectedCategory == null,
            onClick = { onCategorySelected(null) }
        )
        RecipeCategory.entries.forEach { category ->
            CategoryChip(
                text = category.label,
                selected = category == selectedCategory,
                onClick = { onCategorySelected(category) }
            )
        }
    }
}

@Composable
private fun CategoryChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .height(37.dp)
            .background(
                color = if (selected) PortoMint else PortoSurface,
                shape = RoundedCornerShape(50.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 13.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontFamily = Poppins,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            color = PortoDarkGreen
        )
    }
}
