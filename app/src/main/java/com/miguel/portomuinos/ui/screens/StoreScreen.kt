/*
 * Responsabilidad: Pantalla de tienda con búsqueda, categorías, carrito y acceso al detalle de
 * cada producto.
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
import androidx.compose.material3.HorizontalDivider
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
import com.miguel.portomuinos.ui.components.CartIconButton
import com.miguel.portomuinos.ui.components.PortoSearchBar
import com.miguel.portomuinos.ui.components.PortoBottomBarOverlayClearance
import com.miguel.portomuinos.ui.components.StoreProductRow
import com.miguel.portomuinos.ui.model.ProductCategory
import com.miguel.portomuinos.ui.model.ProductUi
import com.miguel.portomuinos.ui.search.matchesSearch
import com.miguel.portomuinos.ui.theme.Poppins
import com.miguel.portomuinos.ui.theme.PortoDarkGreen
import com.miguel.portomuinos.ui.theme.PortoMint
import com.miguel.portomuinos.ui.theme.PortoSurface
import com.miguel.portomuinos.ui.theme.PortoTextSecondary

@Composable
fun StoreScreen(
    products: List<ProductUi>,
    contentPadding: PaddingValues = PaddingValues(),
    cartItemCount: Int = 0,
    onCartClick: () -> Unit = {},
    onProductClick: (String) -> Unit = {},
    onAddProductClick: (String) -> Unit = {}
) {
    // Estado local de búsqueda/filtro de la tienda.

    var searchQuery by rememberSaveable {
        mutableStateOf("")
    }

    var selectedCategoryName by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    val selectedCategory =
        ProductCategory.entries.firstOrNull {
            it.name == selectedCategoryName
        }

    val filteredProducts =
        products.filter { product ->

            val matchesSearch =
                product.matchesSearch(
                    searchQuery
                )

            val matchesCategory =
                selectedCategory == null ||
                    product.category == selectedCategory

            matchesSearch && matchesCategory
        }

    // Composición principal y listado de productos.

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(contentPadding)
            .padding(horizontal = 14.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.SpaceBetween,
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Text(
                text = "Tienda",
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 26.sp,
                color = Color.Black,
                modifier =
                    Modifier.padding(
                        start = 8.dp
                    )
            )

            CartIconButton(
                itemCount = cartItemCount,
                onClick = onCartClick
            )
        }

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        PortoSearchBar(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
            },
            placeholder = "Buscar productos..."
        )

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        ProductCategoryFilters(
            selectedCategory = selectedCategory,
            onCategorySelected = { category ->
                selectedCategoryName = category?.name
            }
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        if (filteredProducts.isEmpty()) {

            Text(
                text = "No encontramos productos con esos filtros.",
                fontFamily = Poppins,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = PortoTextSecondary,
                modifier = Modifier.padding(
                    start = 8.dp,
                    top = 14.dp
                )
            )

        } else {

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    bottom =
                        24.dp +
                            PortoBottomBarOverlayClearance
                )
            ) {

                items(
                    items = filteredProducts,
                    key = { it.id }
                ) { product ->

                    StoreProductRow(
                        product = product,
                        onClick = {
                            onProductClick(product.id)
                        },
                        onAddClick = {
                            onAddProductClick(product.id)
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
}

// Componentes internos de la pantalla.

@Composable
private fun ProductCategoryFilters(
    selectedCategory: ProductCategory?,
    onCategorySelected: (ProductCategory?) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement =
            Arrangement.spacedBy(
                space = 8.dp,
                alignment = Alignment.CenterHorizontally
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        StoreCategoryChip(
            text = "Tienda",
            selected = selectedCategory == null,
            onClick = {
                onCategorySelected(null)
            }
        )

        ProductCategory.entries.forEach { category ->

            StoreCategoryChip(
                text = category.label,
                selected = category == selectedCategory,
                onClick = {
                    onCategorySelected(category)
                }
            )
        }
    }
}

@Composable
private fun StoreCategoryChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .height(37.dp)
            .background(
                color =
                    if (selected) {
                        PortoMint
                    } else {
                        PortoSurface
                    },
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
